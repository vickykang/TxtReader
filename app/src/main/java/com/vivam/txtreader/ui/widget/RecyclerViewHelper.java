package com.vivam.txtreader.ui.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vivam.txtreader.R;

/**
 * Created by kangweodai on 17/01/17.
 */

public class RecyclerViewHelper {

    private final RecyclerView mRecyclerView;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mRecyclerView,
                        mRecyclerView.getChildAdapterPosition(v), v);
            }
        }
    };

    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(mRecyclerView,
                        mRecyclerView.getChildAdapterPosition(v), v);
                return true;
            }
            return false;
        }
    };

    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener);
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    private RecyclerViewHelper(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.recycler_helper, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static RecyclerViewHelper addTo(RecyclerView recyclerView) {
        RecyclerViewHelper helper = (RecyclerViewHelper) recyclerView.getTag(R.id.recycler_helper);
        if (helper == null) {
            helper = new RecyclerViewHelper(recyclerView);
        }
        return helper;
    }

    public static RecyclerViewHelper removeFrom(RecyclerView recyclerView) {
        RecyclerViewHelper helper = (RecyclerViewHelper) recyclerView.getTag(R.id.recycler_helper);
        if (helper != null) {
            helper.detach(recyclerView);
        }
        return helper;
    }

    public RecyclerViewHelper setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public RecyclerViewHelper setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }

    private void detach(RecyclerView recyclerView) {
        recyclerView.removeOnChildAttachStateChangeListener(mAttachListener);
        recyclerView.setTag(R.id.recycler_helper, null);
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, int position, View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(RecyclerView recyclerView, int position, View view);
    }
}

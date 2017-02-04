package com.vivam.txtreader.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vivam.txtreader.R;

public class ItemDialog extends Dialog {

    private RecyclerView mRecyclerView;
    private TextAdapter mAdapter;

    private CharSequence[] mItems;
    private ColorStateList[] mColors;
    private OnClickListener mListener;

    private ItemDialog(@NonNull Context context) {
        this(context, 0);
    }

    private ItemDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.view_item_dialog, null);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        mAdapter = new TextAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        RecyclerViewHelper.addTo(mRecyclerView)
                .setOnItemClickListener(new RecyclerViewHelper.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView recyclerView, int position, View view) {
                        if (mListener != null) {
                            mListener.onClick(ItemDialog.this, position);
                        }
                    }
                });
        addContentView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public CharSequence[] getItems() {
        return mItems;
    }

    public CharSequence getItem(int position) {
        return mItems != null && position < mItems.length ? mItems[position] : null;
    }

    void setItems(CharSequence[] items) {
        mItems = items;
        mAdapter.notifyDataSetChanged();
    }

    public ColorStateList[] getColors() {
        return mColors;
    }

    public ColorStateList getColor(int position) {
        return mColors != null && position < mColors.length ? mColors[position] : null;
    }

    void setColors(ColorStateList[] colors) {
        mColors = colors;
        mAdapter.notifyDataSetChanged();
    }

    public OnClickListener getOnClickListener() {
        return mListener;
    }

    void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public static class Builder {
        private Context mContext;
        private int mThemeResId;
        private CharSequence[] mItems;
        private ColorStateList[] mColors;
        private OnClickListener mListener;

        public Builder(Context context) {
            mContext = context;
            mThemeResId = 0;
        }

        public Builder(Context context, int themeResId) {
            mContext = context;
            mThemeResId = themeResId;
        }

        public Builder setItems(CharSequence[] items) {
            mItems = items;
            return this;
        }

        public Builder setColors(ColorStateList[] colors) {
            mColors = colors;
            return this;
        }

        public Builder setOnClickListener(OnClickListener listener) {
            mListener = listener;
            return this;
        }

        public ItemDialog create() {
            ItemDialog dialog = new ItemDialog(mContext, mThemeResId);
            dialog.setItems(mItems);
            dialog.setColors(mColors);
            dialog.setOnClickListener(mListener);
            return dialog;
        }

        public ItemDialog show() {
            ItemDialog dialog = new ItemDialog(mContext, mThemeResId);
            dialog.setItems(mItems);
            dialog.setColors(mColors);
            dialog.setOnClickListener(mListener);
            dialog.show();
            return dialog;
        }
    }

    private class TextAdapter extends RecyclerView.Adapter<TextHolder> {

        @Override
        public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_dialog_text, parent, false));
        }

        @Override
        public void onBindViewHolder(TextHolder holder, int position) {
            holder.textView.setText(mItems[position]);
            if (mColors != null && position < mColors.length) {
                holder.textView.setTextColor(mColors[position]);
            }
            if (position < mItems.length - 1) {
                holder.divider.setVisibility(View.VISIBLE);
            } else {
                holder.divider.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mItems != null ? mItems.length : 0;
        }
    }

    private class TextHolder extends RecyclerView.ViewHolder {

        TextView textView;
        View divider;

        public TextHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}

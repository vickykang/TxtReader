package com.vivam.txtreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vivam.txtreader.Constants;
import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;

import java.util.List;

public class ChapterListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ChapterListFragment";

    private static final String ARG_BOOK_ID = "book_ID";

    private View mEmptyView;
    private ListView mListView;
    private ChapterAdapter mAdapter;

    private Book mBook;
    private List<Chapter> mChapters;

    private DataManager mDataManager;

    public static ChapterListFragment newInstance(long bookId) {
        ChapterListFragment f = new ChapterListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        f.setArguments(args);
        return f;
    }

    public ChapterListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chapter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyView = view.findViewById(R.id.empty_view);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new ChapterAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        initData();
        syncView(mChapters != null && mChapters.size() > 0 ? Constants.STATE_SUCCESS
                : Constants.STATE_EMPTY);
    }

    private void initData() {
        mDataManager = DataManager.getInstance(getActivity());
        Bundle args = getArguments();
        if (args != null) {
            mBook = mDataManager.getBook(args.getLong(ARG_BOOK_ID));
            if (mBook != null) {
                mChapters = mBook.getChapters();
                if (mChapters != null) {
                    int N = mChapters.size();
                    for (int i = N - 1; i >= 0; i--) {
                        if (TextUtils.isEmpty(mChapters.get(i).getName())) {
                            mChapters.remove(i);
                            N--;
                        }
                    }
                }
            }
        }
    }

    private void syncView(int state) {
        switch (state) {
            case Constants.STATE_EMPTY:
                mEmptyView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                break;

            case Constants.STATE_SUCCESS:
                mEmptyView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: go to selected chapter
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private class ChapterAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mChapters != null ? mChapters.size() : 0;
        }

        @Override
        public Chapter getItem(int position) {
            return mChapters != null ? mChapters.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Chapter chapter = getItem(position);
            if (chapter == null) {
                return null;
            }
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_chapter,
                        parent, false);
                holder = new ViewHolder();
                holder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.nameTv.setText(chapter.getName());
            return convertView;
        }
    }

    private class ViewHolder {
        TextView nameTv;
    }
}

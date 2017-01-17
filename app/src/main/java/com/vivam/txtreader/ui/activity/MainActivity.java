package com.vivam.txtreader.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.ui.adapter.BookshelfAdapter;
import com.vivam.txtreader.ui.widget.RecyclerViewHelper;
import com.vivam.txtreader.ui.widget.SpacesItemDecoration;
import com.vivam.txtreader.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        RecyclerViewHelper.OnItemClickListener,
        RecyclerViewHelper.OnItemLongClickListener {

    private static final String TAG = "MainActivity";

    private static final String BOOK_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    "/心理罪1-画像.txt";

    private Toolbar mToolbar;
    private RecyclerView mBookshelf;
    private View mEmptyView;
    private FloatingActionButton mAddButton;

    private BookshelfAdapter mAdapter;

    private ArrayList<Book> mBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBookshelf = (RecyclerView) findViewById(R.id.grid_book);
        mEmptyView = findViewById(R.id.empty_view);
        mAddButton = (FloatingActionButton) findViewById(R.id.fab_add);

        setSupportActionBar(mToolbar);
        mAddButton.setOnClickListener(this);
        initBookshelf();
    }

    private void initBookshelf() {
        final int spanCount = 3;
        mAdapter = new BookshelfAdapter(this);
        mBookshelf.setLayoutManager(new GridLayoutManager(this, spanCount));
        mBookshelf.setAdapter(mAdapter);

        int width = getResources().getDisplayMetrics().widthPixels - mBookshelf.getPaddingEnd()
                - mBookshelf.getPaddingEnd();
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.cover_width);
        int hSpace = (width - itemWidth * spanCount) / spanCount;
        mBookshelf.addItemDecoration(new SpacesItemDecoration(hSpace,
                getResources().getDimensionPixelSize(R.dimen.cover_vertical_space)));

        RecyclerViewHelper.addTo(mBookshelf)
                .setOnItemClickListener(this)
                .setOnItemLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        notifyState();
    }

    private void initData() {
        File file = new File(BOOK_PATH);
        if (!file.exists()) {
            Log.w(TAG, BOOK_PATH + " not found!");
            return;
        }
        Book book = new Book();
        book.setPath(BOOK_PATH);
        book.setName(FileUtils.getName(BOOK_PATH));
        book.setSize(file.length());
        book.setCreateTime(SystemClock.currentThreadTimeMillis());
        book.setUpdateTime(book.getCreateTime());

        mBooks.add(book);
    }

    private void notifyState() {
        if (mBooks.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
            mAdapter.setData(mBooks);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mBookshelf.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.fab_add) {

        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        if (mBooks != null && position < mBooks.size()) {

        }
    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, int position, View view) {

    }
}

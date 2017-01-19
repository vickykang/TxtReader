package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.ui.adapter.BookshelfAdapter;
import com.vivam.txtreader.ui.widget.RecyclerViewHelper;
import com.vivam.txtreader.ui.widget.SpacesItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        RecyclerViewHelper.OnItemClickListener,
        RecyclerViewHelper.OnItemLongClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_READ = 1;
    private static final int REQUEST_IMPORT = 2;

    private Toolbar mToolbar;
    private RecyclerView mBookshelf;
    private View mEmptyView;
    private FloatingActionButton mAddButton;

    private BookshelfAdapter mAdapter;

    private ArrayList<Book> mBooks = new ArrayList<>();

    private DataManager mDataManager;

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
        mDataManager = DataManager.getInstance(this);
        refreshData();
    }

    private void initBookshelf() {
        final int spanCount = 3;
        mAdapter = new BookshelfAdapter(this);
        mBookshelf.setLayoutManager(new GridLayoutManager(this, spanCount));
        mBookshelf.setAdapter(mAdapter);

        int width = getResources().getDisplayMetrics().widthPixels - mBookshelf.getPaddingLeft()
                - mBookshelf.getPaddingRight();
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.cover_width);
        int hSpace = (width - itemWidth * spanCount) / (spanCount);
        mBookshelf.addItemDecoration(new SpacesItemDecoration(hSpace,
                getResources().getDimensionPixelSize(R.dimen.cover_vertical_space)));

        RecyclerViewHelper.addTo(mBookshelf)
                .setOnItemClickListener(this)
                .setOnItemLongClickListener(this);
    }

    private void refreshData() {
        mBooks.clear();
        mBooks.addAll(mDataManager.getAllBooks().values());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMPORT) {
            if (resultCode == RESULT_OK && data != null) {
                boolean isImported = data.getBooleanExtra(ScanActivity.EXTRA_IMPORTED, false);
                if (isImported) {
                    refreshData();
                    notifyState();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyState();
    }

    private void notifyState() {
        if (mBooks.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
            mBookshelf.setVisibility(View.VISIBLE);
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
            startActivityForResult(new Intent(this, ScanActivity.class), REQUEST_IMPORT);
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        if (mBooks != null && position < mBooks.size()) {
            Intent intent = new Intent(this, ReadActivity.class);
            intent.putExtra(ReadActivity.EXTRA_BOOK, mBooks.get(position));
            startActivityForResult(intent, REQUEST_READ);
        }
    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, int position, View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataManager.clearCache();
    }
}

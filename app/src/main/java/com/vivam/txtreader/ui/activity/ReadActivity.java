package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vivam.txtreader.R;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ChapterEvent;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;
import com.vivam.txtreader.thread.PaginateWork;

public class ReadActivity extends AppCompatActivity {

    private static final String TAG = "ReadActivity";

    public static final String EXTRA_BOOK = "book";

    private ViewPager mPager;
    private TextView mEmptyView;

    private PaginateWork mWork;

    private Book mBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        mPager = (ViewPager) findViewById(R.id.pager);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        handleIntent(getIntent());

        if (mBook == null) {
            mPager.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText("内容为空哦，请检查文件是否存在");
        } else {
            mWork = new PaginateWork(mBook);
            mWork.start();
        }
        mPager.setVisibility(View.GONE);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            mBook = (Book) intent.getSerializableExtra(EXTRA_BOOK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(this);
    }

    @Subscribe
    public void onChapterRefresh(ChapterEvent event) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWork.stop();
    }
}

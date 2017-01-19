package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vivam.txtreader.R;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ChapterEvent;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;
import com.vivam.txtreader.thread.PaginateWork;
import com.vivam.txtreader.ui.adapter.PageAdapter;

public class ReadActivity extends AppCompatActivity {

    private static final String TAG = "ReadActivity";

    public static final String EXTRA_BOOK = "book";

    private ViewPager mPager;
    private TextView mTextView;
    private PageAdapter mAdapter;
    private PaginateWork mWork;

    private Book mBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTextView = (TextView) findViewById(R.id.tv_content);

        handleIntent(getIntent());

        if (mBook == null) {
            // TODO: empty view
            mPager.setVisibility(View.GONE);
            return;
        }
        initTextViewParams();
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            mBook = (Book) intent.getSerializableExtra(EXTRA_BOOK);
        }
    }

    private void initTextViewParams() {
        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mWork == null) {
                    int left = mTextView.getPaddingLeft();
                    int top = mTextView.getPaddingTop();
                    int right = mTextView.getPaddingRight();
                    int bottom = mTextView.getPaddingBottom();
                    mWork = new PaginateWork(mBook, mTextView.getPaint(),
                            mTextView.getWidth() - left - right,
                            mTextView.getHeight() - top - bottom,
                            mTextView.getLineSpacingMultiplier(),
                            mTextView.getLineSpacingExtra(),
                            mTextView.getIncludeFontPadding());
                    mWork.start();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(this);
    }

    @Subscribe
    public void onPagesRefresh(ChapterEvent event) {
        Chapter chapter = event.getChapter();
        if (chapter != null) {
            mAdapter.addAllPages(chapter.getPages());
        }
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

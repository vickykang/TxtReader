package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.vivam.txtreader.Constants;
import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ChapterEvent;
import com.vivam.txtreader.data.event.ChapterMatchedEvent;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;
import com.vivam.txtreader.thread.PaginateWork;
import com.vivam.txtreader.ui.adapter.PageAdapter;
import com.vivam.txtreader.ui.fragment.ReadSettingsDialogFragment;
import com.vivam.txtreader.ui.widget.PartitionView;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity
        implements PartitionView.OnPartitionClickListener {

    private static final String TAG = "ReadActivity";

    public static final String EXTRA_BOOK = "book";

    public static final int REQUEST_CHAPTER_LIST = 0x2;

    private ViewPager mPager;
    private TextView mFakeTextView;
    private TextView mFailedTextView;
    private View mLoadingView;
    private PartitionView mPartitionView;

    private PageAdapter mAdapter;

    private PaginateWork mWork;

    private Book mBook;

    private DataManager mDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        mPager = (ViewPager) findViewById(R.id.pager);
        mFakeTextView = (TextView) findViewById(R.id.tv_content);
        mFailedTextView = (TextView) findViewById(R.id.tv_fail);
        mLoadingView = findViewById(R.id.loading);
        mPartitionView = (PartitionView) findViewById(R.id.partition);

        handleIntent(getIntent());

        if (mBook == null) {
            notifyState(Constants.STATE_EMPTY);
            return;
        }
        initTextViewParams();
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mPartitionView.setOnPartitionClickListener(this);

        mDataManager = DataManager.getInstance(this);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            mBook = (Book) intent.getSerializableExtra(EXTRA_BOOK);
        }
    }

    private void initTextViewParams() {
        mFakeTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mWork == null) {
                    int left = mFakeTextView.getPaddingLeft();
                    int top = mFakeTextView.getPaddingTop();
                    int right = mFakeTextView.getPaddingRight();
                    int bottom = mFakeTextView.getPaddingBottom();
                    mWork = new PaginateWork(mBook, mFakeTextView.getPaint(),
                            mFakeTextView.getWidth() - left - right,
                            mFakeTextView.getHeight() - top - bottom,
                            mFakeTextView.getLineSpacingMultiplier(),
                            mFakeTextView.getLineSpacingExtra(),
                            mFakeTextView.getIncludeFontPadding());
                    mWork.start();
                    notifyState(Constants.STATE_LOADING);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(this);
    }

    private void notifyState(int state) {
        switch (state) {
            case Constants.STATE_LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                mFailedTextView.setVisibility(View.GONE);
                mPager.setVisibility(View.GONE);
                break;

            case Constants.STATE_EMPTY:
            case Constants.STATE_FAILED:
                mLoadingView.setVisibility(View.GONE);
                mFailedTextView.setVisibility(View.VISIBLE);
                mPager.setVisibility(View.GONE);
                break;

            case Constants.STATE_SUCCESS:
                mLoadingView.setVisibility(View.GONE);
                mFailedTextView.setVisibility(View.GONE);
                mPager.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Subscribe
    public void onChapterMatched(ChapterMatchedEvent event) {
        List<Chapter> chapters = event.getChapters();
        if (chapters != null && chapters.size() > 0) {
            mDataManager.insertChapters(mBook, chapters);
        }
    }

    @Subscribe
    public void onPagesRefresh(ChapterEvent event) {
        Chapter chapter = event.getChapter();
        if (chapter != null) {
            notifyState(Constants.STATE_SUCCESS);
            mAdapter.addAllPages(chapter.getPages());
        } else {
            notifyState(Constants.STATE_EMPTY);
        }
    }

    @Override
    public void onClick(int area) {
        switch (area) {
            case PartitionView.AREA_LEFT:
                if (mPager != null && mPager.getCurrentItem() > 0) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
                break;

            case PartitionView.AREA_CENTER:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                ReadSettingsDialogFragment fragment = ReadSettingsDialogFragment
                        .newInstance(mBook.getId());
                fragment.show(ft, "dialog");
                break;

            case PartitionView.AREA_RIGHT:
                if (mPager != null && mPager.getAdapter() != null &&
                        mPager.getCurrentItem() < mPager.getAdapter().getCount() - 1) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
                break;
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
        mDataManager.updateTime(mBook);
    }
}

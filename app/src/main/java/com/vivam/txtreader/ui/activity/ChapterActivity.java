package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.WindowManager;

import com.vivam.txtreader.R;
import com.vivam.txtreader.ui.fragment.ChapterListFragment;

public class ChapterActivity extends FragmentActivity {

    public static final String EXTRA_BOOK_ID = "book_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = screenHeight * 2 / 3;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);

        Intent intent = getIntent();
        long bookId = 0L;
        if (intent != null) {
            bookId = intent.getLongExtra(EXTRA_BOOK_ID, 0L);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ChapterListFragment.newInstance(bookId))
                .commit();
    }
}

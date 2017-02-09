package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.vivam.txtreader.R;
import com.vivam.txtreader.ui.fragment.ChapterListFragment;

public class ChapterActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "book_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        Intent intent = getIntent();
        long bookId = 0L;
        if (intent != null) {
            bookId = intent.getLongExtra(EXTRA_BOOK_ID, 0L);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ChapterListFragment.newInstance(bookId))
                .commit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }
}

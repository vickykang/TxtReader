package com.vivam.txtreader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.vivam.txtreader.R;

public class ReadActivity extends AppCompatActivity {

    private ViewPager mPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        mPager = (ViewPager) findViewById(R.id.pager);

    }
}

package com.vivam.txtreader.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentStatePagerAdapter {

    final private ArrayList<String> mPages;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        mPages = new ArrayList<>();
    }

    public void addPage(String page) {
        mPages.add(page);
        notifyDataSetChanged();
    }

    public void addAllPages(List<String> pages) {
        mPages.addAll(pages);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }
}

package com.vivam.txtreader.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kangweodai on 17/01/17.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "SpacesItemDecoration";

    private int mHorizontalSpace;
    private int mVerticalSpace;

    public SpacesItemDecoration(int horizontalSpace, int verticalSpace) {
        mHorizontalSpace = horizontalSpace / 2;
        mVerticalSpace = verticalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.set(mHorizontalSpace, mVerticalSpace, mHorizontalSpace, 0);
    }
}

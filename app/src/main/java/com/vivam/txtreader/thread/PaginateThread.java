package com.vivam.txtreader.thread;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ChapterEvent;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;

import java.util.ArrayList;

/**
 * Created by kangweodai on 18/01/17.
 */

public class PaginateThread extends Thread {

    private static final String TAG = "PaginateThread";

    private ArrayList<Chapter> mChapters;
    private final TextPaint mPaint;
    private final int mWidth;
    private final int mHeight;
    private final float mSpacingMulti;
    private final float mSpacingExtra;
    private final boolean mIncludeFontPadding;

    private Handler mHandler;

    public PaginateThread(ArrayList<Chapter> chapters, TextPaint paint, int width, int height,
                          float spacingMulti, float spacingExtra, boolean includeFontPadding,
                          Handler handler) {
        mChapters = chapters;
        mPaint = paint;
        mWidth = width;
        mHeight = height;
        mSpacingMulti = spacingMulti;
        mSpacingExtra = spacingExtra;
        mIncludeFontPadding = includeFontPadding;
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();

        long startTime = SystemClock.currentThreadTimeMillis();

        if (mChapters == null) {
            return;
        }

        for (Chapter chapter : mChapters) {
            paginate(chapter);
            mHandler.obtainMessage(PaginateWork.MSG_PAGINATED_ONE, chapter);
        }

        Log.i(TAG, "pagination costs " + (SystemClock.currentThreadTimeMillis() - startTime));
    }

    private void paginate(Chapter chapter) {
        ArrayList<String> pages = chapter.getPages();
        if (pages == null) {
            pages = new ArrayList<>();
            chapter.setPages(pages);
        }

        String name = chapter.getName().trim();
        String content;
        if (!TextUtils.isEmpty(name)) {
            content = name + "\n\n" + chapter.getContent();
        } else {
            content = chapter.getContent();
        }
        if (TextUtils.isEmpty(content)) {
            return;
        }

        StaticLayout layout = new StaticLayout(content, mPaint, mWidth,
                Layout.Alignment.ALIGN_NORMAL, mSpacingMulti, mSpacingExtra,
                mIncludeFontPadding);

        int lines = layout.getLineCount();
        CharSequence text = layout.getText();
        int startOffset = 0;
        int height = mHeight;

        for (int i = 0; i < lines; i++) {
            if (height < layout.getLineBottom(i)) {
                // When the layout height has been exceeded
                int length = layout.getLineStart(i);
                pages.add(text.subSequence(startOffset, length).toString());
                startOffset = length;
                height = layout.getLineTop(i) + mHeight;
            }

            if (i == lines - 1) {
                // Put the reset of the text into the last page
                pages.add(text.subSequence(startOffset, layout.getLineEnd(i)).toString());
                return;
            }
        }
    }
}

package com.vivam.txtreader.thread;

import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;

import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ChapterEvent;
import com.vivam.txtreader.data.event.ChapterMatchedEvent;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;

import java.util.ArrayList;

public class PaginateWork {

    private static final String TAG = "PaginateWork";

    public static final int MSG_START_PAGINATE = 1;
    public static final int MSG_PAGINATED_ONE = 2;

    private Book mBook;
    private final TextPaint mPaint;
    private final int mWidth;
    private final int mHeight;
    private final float mSpacingMulti;
    private final float mSpacingExtra;
    private final boolean mIncludeFontPadding;

    private Handler mHandler;
    private ReadFileThread mReadThread;
    private PaginateThread mPaginateThread;

    public PaginateWork(Book book, TextPaint paint, int width, int height,
                        float spacingMulti, float spacingExtra, boolean includeFontPadding) {
        mBook = book;
        mPaint = paint;
        mWidth = width;
        mHeight = height;
        mSpacingMulti = spacingMulti;
        mSpacingExtra = spacingExtra;
        mIncludeFontPadding = includeFontPadding;

        mHandler = new Handler(mCallback);
        mReadThread = new ReadFileThread(mBook, mHandler);
    }

    public void start() {
        mReadThread.start();
    }

    public void stop() {
        mReadThread.interrupt();
        if (mPaginateThread != null) {
            mPaginateThread.interrupt();
        }
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_START_PAGINATE) {
                ArrayList<Chapter> chapters = (ArrayList<Chapter>) msg.obj;
                mPaginateThread = new PaginateThread(chapters, mPaint, mWidth, mHeight,
                        mSpacingMulti, mSpacingExtra, mIncludeFontPadding, mHandler);
                mPaginateThread.start();
                EventBus.post(new ChapterMatchedEvent(chapters));
                return true;

            } else if (msg.what == MSG_PAGINATED_ONE) {
                EventBus.post(new ChapterEvent((Chapter) msg.obj));
                return true;
            }

            return false;
        }
    };
}

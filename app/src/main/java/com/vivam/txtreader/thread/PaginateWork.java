package com.vivam.txtreader.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ChapterEvent;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;

public class PaginateWork {

    private static final String TAG = "PaginateWork";

    public static final int MSG_START_PAGINATE = 1;

    private Book mBook;
    private Handler mHandler;
    private ReadFileThread mReadThread;


    public PaginateWork(Book book) {
        mBook = book;
        mHandler = new Handler(mCallback);
        mReadThread = new ReadFileThread(mBook, mHandler);
    }

    public void start() {
        mReadThread.run();
    }

    public void stop() {
        mReadThread.interrupt();
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_START_PAGINATE) {
                
                return true;
            }
            return false;
        }
    };
}

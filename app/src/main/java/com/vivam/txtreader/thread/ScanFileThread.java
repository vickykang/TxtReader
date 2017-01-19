package com.vivam.txtreader.thread;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ScanEvent;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.ui.activity.ScanActivity;
import com.vivam.txtreader.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanFileThread extends Thread {

    private static final String TAG = "ScanFileThread";

    private DataManager mManager;
    private File mDir;
    final private String mSuffix;
    final HashMap<String, ArrayList<BookFile>> mBookFiles = new HashMap<>();
    private Handler mHandler;

    public ScanFileThread(DataManager manager, File dir, String suffix, Handler handler) {
        mManager = manager;
        mDir = dir;
        mSuffix = suffix;
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();
        long startTime = SystemClock.currentThreadTimeMillis();
        scan(mDir);
        Log.i(TAG, "end scanning, cost " +
                (SystemClock.currentThreadTimeMillis() - startTime) + " ms");
        mHandler.obtainMessage(ScanActivity.MSG_END_SCANNING, mBookFiles).sendToTarget();
    }

    private void scan(File dir) {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        String name;
        for (File file : files) {
            if (file.isDirectory()) {
                scan(file);
            } else {
                name = file.getName().toLowerCase();
                if (file.length() > FileUtils.MIN_FILE_SIZE && name.endsWith(mSuffix)
                        && !name.contains("log")) {
                    ArrayList<BookFile> list = mBookFiles.get(file.getParent());
                    if (list == null) {
                        list = new ArrayList<>();
                        mBookFiles.put(file.getParent(), list);
                    }

                    BookFile book = new BookFile();
                    book.setName(file.getName());
                    book.setPath(file.getPath());
                    book.setSize(file.length());
                    book.setImported(mManager.isImported(book.getPath()));

                    list.add(book);
                }
            }
        }
    }
}

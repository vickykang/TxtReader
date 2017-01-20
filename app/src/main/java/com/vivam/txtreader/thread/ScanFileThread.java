package com.vivam.txtreader.thread;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.ui.activity.ScanActivity;
import com.vivam.txtreader.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanFileThread extends Thread {

    private static final String TAG = "ScanFileThread";

    /** The minimum size of book file */
    public static final int MIN_FILE_SIZE = 20 * FileUtils.ONE_KB;

    public static final int MAX_FILE_DEPTH = 9;

    private DataManager mManager;
    private String mPath;
    final private String mSuffix;
    final HashMap<String, ArrayList<BookFile>> mBookFiles = new HashMap<>();
    private Handler mHandler;

    public ScanFileThread(DataManager manager, String path, String suffix, Handler handler) {
        mManager = manager;
        mPath = path;
        mSuffix = suffix;
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();
        long startTime = SystemClock.currentThreadTimeMillis();
        scan(mPath);
        Log.i(TAG, "end scanning, cost " +
                (SystemClock.currentThreadTimeMillis() - startTime) + " ms");
        mHandler.obtainMessage(ScanActivity.MSG_END_SCANNING, mBookFiles).sendToTarget();
    }

    private void scan(String path) {
        File parent = new File(path);
        if (!parent.exists()) {
            return;
        }

        File[] files = parent.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden() && file.canRead()
                        && FileUtils.getDepth(file.getAbsolutePath()) < MAX_FILE_DEPTH;
            }
        });

        if (files == null || files.length == 0) {
            return;
        }

        String name;
        for (File file : files) {
            if (file.isDirectory()) {
                scan(file.getAbsolutePath());
            } else {
                name = file.getName().toLowerCase();
                if (file.length() > MIN_FILE_SIZE && name.endsWith(mSuffix)
                        && !isDebugFile(file.getName())) {
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

    private boolean isDebugFile(String name) {
        return name.contains("debug") || name.contains("log") || name.contains("json")
                || name.contains("config") || name.contains("dump-networking")
                || name.contains("crash");
    }
}

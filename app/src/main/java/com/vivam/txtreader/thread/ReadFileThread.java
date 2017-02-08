package com.vivam.txtreader.thread;

import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.vivam.txtreader.Constants;
import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.Chapter;
import com.vivam.txtreader.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kangweodai on 17/01/17.
 */

public class ReadFileThread extends Thread {

    private static final String TAG = "ReadFileThread";

    Book mBook;
    List<Chapter> mChapters;
    Handler mHandler;

    public ReadFileThread(Book book, Handler handler) {
        mBook = book;
        mHandler = handler;
        mChapters = mBook.getChapters();
        if (mChapters == null) {
            mChapters = new ArrayList<>();
            mBook.setChapters(mChapters);
        }
    }

    @Override
    public void run() {
        super.run();
        long startTime = SystemClock.currentThreadTimeMillis();
        RandomAccessFile file;
        try {
            file = new RandomAccessFile(mBook.getPath(), "r");
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return;
        }

        final long size = mBook.getSize();
        try {

            mBook.setCharset(FileUtils.detectCharset(mBook.getPath()));
            byte[] bytes = new byte[(int) size];
            file.read(bytes);
            file.close();
            matchChapters(bytes);

        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        mHandler.obtainMessage(PaginateWork.MSG_START_PAGINATE, mChapters).sendToTarget();

        Log.i(TAG, "cost " + (SystemClock.currentThreadTimeMillis() - startTime) +
                " ms to read file");
    }

    private void matchChapters(byte[] bytes) throws UnsupportedEncodingException {
        String content = new String(bytes, mBook.getCharset());

        if (TextUtils.isEmpty(content)) {
            Log.w(TAG, mBook.getName() + " has no content.");
            return;
        }

        Pattern pattern = Constants.getChapterPattern();
        Matcher matcher = pattern.matcher(content);

        int index = 0;
        int offset = 0;

        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            final String name = content.substring(start, end);
            Log.i(TAG, "match chapter: " + name);

            if (start > 0) {
                if (index > 0 && mChapters.size() > 0) {
                    refreshChapter(mChapters.get(mChapters.size() - 1), start - 1,
                            content.substring(offset, start - 1));
                } else {
                    // The first part
                    Chapter chapter = new Chapter();
                    chapter.setBookId(mBook.getId());
                    chapter.setContent(content.substring(0, start - 1));
                    chapter.setStart(0);
                    chapter.setEnd(start - 1);
                    chapter.setIndex(mChapters.size());
                    mChapters.add(chapter);
                    // notifyChapterCompleted(chapter);
                }

                mChapters.add(newChapter(name, start));
            } else if (start == 0) {
                // The first part, which is a matched chapter
                mChapters.add(newChapter(name, start));
            }

            index++;
            offset = start + name.length();
        }
        if (offset < bytes.length) {
            int chapterSize = mChapters.size();
            if (chapterSize > 0) {
                refreshChapter(mChapters.get(chapterSize - 1), bytes.length - 1,
                        content.substring(offset));
            } else {
                // Only one chapter without name.
                Chapter chapter = new Chapter();
                chapter.setBookId(mBook.getId());
                chapter.setContent(content);
                chapter.setStart(0);
                chapter.setStart(bytes.length);
                chapter.setIndex(mChapters.size());
                mChapters.add(chapter);
                // notifyChapterCompleted(chapter);
            }
        }
    }

    private void notifyChapterCompleted(Chapter chapter) {
        mHandler.obtainMessage(PaginateWork.MSG_START_PAGINATE, chapter).sendToTarget();
    }

    private void refreshChapter(Chapter chapter, int end, String newContent) {
        chapter.setEnd(end);
        String oldContent = chapter.getContent();
        if (oldContent == null) {
            chapter.setContent(newContent);
        } else {
            chapter.setContent(oldContent + newContent);
        }
        // notifyChapterCompleted(chapter);
    }

    private Chapter newChapter(String name, int start) {
        Chapter chapter = new Chapter();
        chapter.setBookId(mBook.getId());
        chapter.setName(name != null ? name.trim() : null);
        chapter.setStart(start);
        chapter.setIndex(mChapters.size());
        return chapter;
    }
}

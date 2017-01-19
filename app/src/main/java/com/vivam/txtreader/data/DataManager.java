package com.vivam.txtreader.data;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;

import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.utils.FileUtils;

import java.util.Map;

public class DataManager {

    private static DataManager instance;

    private Context mContext;
    private BookLruCache mBookCache;

    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    private DataManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context is NULL!");
        }
        mContext = context.getApplicationContext();
        int memory = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        mBookCache = new BookLruCache(FileUtils.ONE_MB * memory / 8);
    }

    public Book getBook(String path) {
        return mBookCache.get(path);
    }

    public void putBook(String path, Book book) {
        mBookCache.put(path, book);
    }

    public Map<String, Book> getAllBooks() {
        return mBookCache.snapshot();
    }

    public void clearCache() {
        mBookCache.evictAll();
    }

    public Book importBook(BookFile file) {
        Book book = new Book(file);
        book.setCreateTime(SystemClock.currentThreadTimeMillis());
        book.setUpdateTime(book.getCreateTime());
        book.setImported(true);
        putBook(book.getPath(), book);
        return book;
    }

    public boolean isImported(String path) {
        return getBook(path) != null;
    }
}

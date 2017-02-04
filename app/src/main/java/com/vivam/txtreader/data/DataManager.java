package com.vivam.txtreader.data;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.vivam.txtreader.data.model.Book;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private static DataManager instance;

    private Context mContext;
    private ContentResolver mResolver;
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
        mResolver = mContext.getContentResolver();
        int memory = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        mBookCache = new BookLruCache(FileUtils.ONE_MB * memory / 8);
    }

    public Book getBook(String path) {
        Book book = mBookCache.get(path);
        if (book == null) {
            Cursor cursor = null;
            try {
                cursor = mResolver.query(ReaderProvider.CONTENT_URI_BOOK, null,
                        Columns.COLUMN_PATH + " =?", new String[]{path}, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    book = getBookFromCursor(cursor);
                    if (book == null) {
                        return null;
                    }
                    mBookCache.put(book.getPath(), book);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return book;
    }

    public Map<String, Book> getAllBooks() {
        Map<String, Book> books = mBookCache.snapshot();
        if (books != null) {
            Cursor cursor = null;
            try {
                cursor = mResolver.query(ReaderProvider.CONTENT_URI_BOOK, null, null, null, null,
                        null);
                if (cursor == null) {
                    return null;
                }
                books = new HashMap<>();
                while (cursor.moveToNext()) {
                    Book book = getBookFromCursor(cursor);
                    if (book != null) {
                        books.put(book.getPath(), book);
                        mBookCache.put(book.getPath(), book);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return books;
    }

    public void clearCache() {
        mBookCache.evictAll();
    }

    public Book importBook(BookFile file) {
        Book book = new Book(file);
        book.setCreateTime(SystemClock.currentThreadTimeMillis());
        book.setUpdateTime(book.getCreateTime());
        book.setImported(true);

        ContentValues values = new ContentValues();
        values.put(Columns.COLUMN_NAME, book.getName());
        values.put(Columns.COLUMN_PATH, book.getPath());
        values.put(Columns.COLUMN_SIZE, book.getSize());
        values.put(Columns.COLUMN_CREATED, book.getCreateTime());
        values.put(Columns.COLUMN_UPDATED, book.getUpdateTime());

        Uri uri = mResolver.insert(ReaderProvider.CONTENT_URI_BOOK, values);
        book.setId(ContentUris.parseId(uri));

        mBookCache.put(book.getPath(), book);

        return book;
    }

    public boolean removeBookFromShelf(Book book) {
        int count = mResolver.delete(ReaderProvider.CONTENT_URI_BOOK, Columns._ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        mBookCache.remove(book.getPath());
        return count > 0;
    }

    public boolean removeBookCompletely(Book book) {
        return removeBookFromShelf(book) && FileUtils.deleteFile(book.getPath());
    }

    public boolean isImported(String path) {
        return getBook(path) != null;
    }

    private Book getBookFromCursor(Cursor cursor) {
        Book book = new Book();
        book.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Columns._ID)));
        book.setName(cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLUMN_NAME)));
        book.setPath(cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLUMN_PATH)));
        book.setCharset(cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLUMN_CHARSET)));
        book.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.COLUMN_CREATED)));
        book.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.COLUMN_UPDATED)));
        book.setImported(true);
        return book;
    }
}

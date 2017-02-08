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
import com.vivam.txtreader.data.model.Chapter;
import com.vivam.txtreader.utils.FileUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    public Book getBook(long id) {
        Book book = mBookCache.get(id);
        if (book == null) {
            Cursor cursor = null;
            try {
                cursor = mResolver.query(ReaderProvider.CONTENT_URI_BOOK, null,
                        Columns._ID + " =?", new String[]{String.valueOf(id)}, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    book = getBookFromCursor(cursor);
                    if (book == null) {
                        return null;
                    }
                    mBookCache.put(book.getId(), book);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return book;
    }

    public Map<Long, Book> getAllBooks() {
        Map<Long, Book> books = mBookCache.snapshot();
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
                        books.put(book.getId(), book);
                        mBookCache.put(book.getId(), book);
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

        mBookCache.put(book.getId(), book);

        return book;
    }

    public boolean removeBookFromShelf(Book book) {
        int count = mResolver.delete(ReaderProvider.CONTENT_URI_BOOK, Columns._ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        mBookCache.remove(book.getId());
        return count > 0;
    }

    public boolean removeBookCompletely(Book book) {
        return removeBookFromShelf(book) && FileUtils.deleteFile(book.getPath());
    }

    public boolean isImported(String path) {
        return false;
    }

    public boolean isImported(long id) {
        return id > 0 && getBook(id) != null;
    }

    private Book getBookFromCursor(Cursor cursor) {
        Book book = new Book();
        book.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Columns._ID)));
        book.setName(cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLUMN_NAME)));
        book.setPath(cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLUMN_PATH)));
        book.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.COLUMN_SIZE)));
        book.setCharset(cursor.getString(cursor.getColumnIndexOrThrow(Columns.COLUMN_CHARSET)));
        book.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.COLUMN_CREATED)));
        book.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.COLUMN_UPDATED)));
        book.setImported(true);
        return book;
    }

    public void insertChapters(Book book, List<Chapter> chapters) {
        if (book != null && chapters != null) {
            for (Chapter c : chapters) {
                insertChapter(book.getId(), c);
            }
            book.setChapters(chapters);
            mBookCache.put(book.getId(), book);
        }
    }

    public void insertChapter(long bookId, Chapter chapter) {
        if (chapter == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Columns.COLUMN_BOOK_ID, bookId);
        values.put(Columns.COLUMN_CHAPTER_INDEX, chapter.getIndex());
        values.put(Columns.COLUMN_CHAPTER_START, chapter.getStart());
        values.put(Columns.COLUMN_CHAPTER_END, chapter.getEnd());
        mResolver.insert(ReaderProvider.CONTENT_URI_CHAPTER, values);
    }

    public void updateTime(Book book) {
        if (book == null) {
            return;
        }
        long time = SystemClock.currentThreadTimeMillis();
        book.setUpdateTime(time);
        mBookCache.put(book.getId(), book);

        ContentValues values = new ContentValues();
        values.put(Columns.COLUMN_UPDATED, time);
        mResolver.update(ReaderProvider.CONTENT_URI_BOOK, values, Columns._ID + " = ?",
                new String[]{String.valueOf(book.getId())});
    }

    public void sortBooks(List<Book> books) {
        if (books == null || books.size() < 2) {
            return;
        }

        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                return o1.getUpdateTime() == o2.getUpdateTime() ? 0 :
                        (o1.getUpdateTime() < o2.getUpdateTime() ? 1 : -1);
            }
        });
    }
}

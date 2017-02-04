package com.vivam.txtreader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ReaderProvider extends ContentProvider {

    private static final String TAG = "ReaderProvider";

    private static final String CONTENT_URI_BASE = "content://";
    private static final String AUTHORITY = "com.vivam.txtreader";

    private static final String NAME = "txtreader.db";
    private static final int VERSION = 1;

    private static final String TABLE_BOOK = "book";
    private static final String TABLE_CHAPTER = "chapter";

    public static final Uri CONTENT_URI_BOOK =
            Uri.parse(CONTENT_URI_BASE + AUTHORITY + "/" + TABLE_BOOK);
    public static final Uri CONTENT_URI_CHAPTER =
            Uri.parse(CONTENT_URI_BASE + AUTHORITY + "/" + TABLE_CHAPTER);

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /** URI matcher constant for the URI of all books */
    private static final int ALL_BOOKS = 1;
    /** URI matcher constant for the URI of  an individual book */
    private static final int BOOK_ID = 2;
    /** URI matcher constant for the URI of all chapters */
    private static final int ALL_CHAPTERS = 3;
    /** URI matcher constant for the URI of an individual chapter */
    private static final int CHAPTER_ID = 4;

    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_BOOK, ALL_BOOKS);
        sUriMatcher.addURI(AUTHORITY, TABLE_BOOK + "/#", BOOK_ID);
        sUriMatcher.addURI(AUTHORITY, TABLE_CHAPTER, ALL_CHAPTERS);
        sUriMatcher.addURI(AUTHORITY, TABLE_CHAPTER + "/#", CHAPTER_ID);
    }

    private DBHelper mOpenHelper;

    private static class SqlSelection {
        StringBuilder mWhereClause = new StringBuilder();
        List<String> mParameter = new ArrayList<>();

        public <T> void appendClause(String newClause, final T... parameters) {
            if (newClause == null || newClause.length() == 0) {
                return;
            }
            if (mWhereClause.length() != 0) {
                mWhereClause.append(" AND ");
            }
            mWhereClause.append("(")
                    .append(newClause)
                    .append(")");
            if (parameters != null) {
                for (Object p : parameters) {
                    mParameter.add(p.toString());
                }
            }
        }

        public String getSelection() {
            return mWhereClause.toString();
        }

        public String[] getParameters() {
            String[] array = new String[mParameter.size()];
            return mParameter.toArray(array);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs, match);
        return db.query(getTableName(match), projection, fullSelection.getSelection(),
                fullSelection.getParameters(), null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        String table = getTableName(match);
        if (table == null) {
            return null;
        }
        long rowId = db.insert(table, null, values);
        if (rowId == -1) {
            Log.w(TAG, "insert to " + table + " failed!");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        int match = sUriMatcher.match(uri);
        SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs, match);
        if (match == ALL_CHAPTERS || match == CHAPTER_ID) {
            count += db.delete(getTableName(match), fullSelection.getSelection(),
                    fullSelection.getParameters());
            return count;
        }
        if (match == ALL_BOOKS || match == BOOK_ID) {
            Cursor cursor = db.query(TABLE_CHAPTER, new String[]{Columns._ID},
                    fullSelection.getSelection(), fullSelection.getParameters(), null, null, null);
            String[] bookIds = null;
            if (cursor != null) {
                try {
                    bookIds = new String[cursor.getCount()];
                    int index = 0;
                    while (cursor.moveToNext()) {
                        bookIds[index++] = String.valueOf(cursor.getLong(
                                cursor.getColumnIndexOrThrow(Columns._ID)));
                    }
                } finally {
                    cursor.close();
                }
            }
            if (bookIds != null) {
                count += db.delete(TABLE_CHAPTER, Columns.COLUMN_BOOK_ID + " = ?", bookIds);
            }
            count += db.delete(TABLE_BOOK, fullSelection.getSelection(),
                    fullSelection.getParameters());
            return count;
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs, match);
        int count = db.update(getTableName(match), values, fullSelection.getSelection(),
                fullSelection.getParameters());
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private String getIdFromUri(final Uri uri) {
        return uri.getPathSegments().get(1);
    }

    private String getTableName(int match) {
        switch (match) {
            case ALL_BOOKS:
            case BOOK_ID:
                return TABLE_BOOK;
            case ALL_CHAPTERS:
            case CHAPTER_ID:
                return TABLE_CHAPTER;
        }
        return null;
    }

    private SqlSelection getWhereClause(final Uri uri, final String where, final String[] whereArgs,
                                        int uriMatch) {
        SqlSelection selection = new SqlSelection();
        selection.appendClause(where, whereArgs);
        if (uriMatch == BOOK_ID || uriMatch == CHAPTER_ID) {
            selection.appendClause(Columns._ID + " = ?", getIdFromUri(uri));
        }
        return selection;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            onUpgrade(db, 0, VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            createTableBook(db);
            createTableChapter(db);
        }

        private void createTableBook(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
            db.execSQL("CREATE TABLE " + TABLE_BOOK + " (" +
                            Columns._ID + " INTEGER PRIMARY KEY, " +
                            Columns.COLUMN_NAME + " TEXT NOT NULL, " +
                            Columns.COLUMN_PATH + " TEXT NOT NULL, " +
                            Columns.COLUMN_SIZE + " INTEGER, " +
                            Columns.COLUMN_CHARSET + " TEXT, " +
                            Columns.COLUMN_CREATED + " INTEGER, " +
                            Columns.COLUMN_UPDATED + " INTEGER" +
                            ");");
        }

        private void createTableChapter(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTER);
            db.execSQL("CREATE TABLE " + TABLE_CHAPTER + " (" +
                            Columns._ID + " INTEGER PRIMARY KEY, " +
                            Columns.COLUMN_BOOK_ID + " INTEGER, " +
                            Columns.COLUMN_CHAPTER_INDEX + " INTEGER, " +
                            Columns.COLUMN_CHAPTER_START + " INTEGER, " +
                            Columns.COLUMN_CHAPTER_END + " INTEGER, " +
                            Columns.COLUMN_CHAPTER_CONTENT + " TEXT" +
                            ");");
        }
    }
}

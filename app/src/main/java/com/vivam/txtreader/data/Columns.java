package com.vivam.txtreader.data;

import android.provider.BaseColumns;

public class Columns implements BaseColumns {

    /** the name of book, chapter, and so on */
    public static final String COLUMN_NAME = "name";
    /** the path of book file */
    public static final String COLUMN_PATH = "path";
    /** the size of book file */
    public static final String COLUMN_SIZE = "size";
    /** the charset of book file */
    public static final String COLUMN_CHARSET = "charset";
    /** the time book imported */
    public static final String COLUMN_CREATED = "created";
    /** the time book updated */
    public static final String COLUMN_UPDATED = "updated";
    /** the id of chapter that read last time */
    public static final String COLUMN_CURRENT_CHAPTER = "current_chapter";
    /** the start position of page that read last time */
    public static final String COLUMN_LAST_POSITION = "last_position";

    /** the book id of chapter */
    public static final String COLUMN_BOOK_ID = "book_id";
    /** the index of chapter */
    public static final String COLUMN_CHAPTER_INDEX = "chapter_index";
    /** the start position of chapter */
    public static final String COLUMN_CHAPTER_START = "start";
    /** the end position of chapter */
    public static final String COLUMN_CHAPTER_END = "end";
    /** the contend of chapter */
    public static final String COLUMN_CHAPTER_CONTENT = "content";
}

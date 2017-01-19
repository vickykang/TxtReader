package com.vivam.txtreader.data.event;

import com.vivam.txtreader.data.model.BookFile;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanEvent {

    private HashMap<String, ArrayList<BookFile>> mBookFiles;

    public ScanEvent(HashMap<String, ArrayList<BookFile>> bookFiles) {
        mBookFiles = bookFiles;
    }

    public HashMap<String, ArrayList<BookFile>> getBookFiles() {
        return mBookFiles;
    }
}

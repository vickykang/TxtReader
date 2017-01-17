package com.vivam.txtreader.thread;

import com.vivam.txtreader.data.model.Book;

import java.io.File;

/**
 * Created by kangweodai on 17/01/17.
 */

public class ReadFileThread extends Thread {

    Book mBook;

    public ReadFileThread(Book book) {
        mBook = book;
    }

    @Override
    public void run() {
        super.run();
        File file = new File(mBook.getPath());
        if (!file.exists()) {
            return;
        }


    }
}

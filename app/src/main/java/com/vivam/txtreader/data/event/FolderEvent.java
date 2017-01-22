package com.vivam.txtreader.data.event;

import com.vivam.txtreader.data.model.BookFile;

/**
 * Created by kangweodai on 22/01/17.
 */

public class FolderEvent {
    private BookFile file;

    public FolderEvent(BookFile file) {
        this.file = file;
    }

    public BookFile getFile() {
        return file;
    }
}

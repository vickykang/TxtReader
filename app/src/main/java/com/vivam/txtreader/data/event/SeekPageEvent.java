package com.vivam.txtreader.data.event;

/**
 * Created by kangweodai on 09/02/17.
 */

public class SeekPageEvent {
    private int page;

    public SeekPageEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }
}

package com.vivam.txtreader.data.event;

import com.vivam.txtreader.data.model.Chapter;

public class ChapterEvent {

    private Chapter chapter;

    public ChapterEvent(Chapter chapter) {
        this.chapter = chapter;
    }

    public Chapter getChapter() {
        return this.chapter;
    }
}

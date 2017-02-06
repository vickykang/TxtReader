package com.vivam.txtreader.data.event;

import com.vivam.txtreader.data.model.Chapter;

import java.util.List;

public class ChapterMatchedEvent {

    private List<Chapter> chapters;

    public ChapterMatchedEvent(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
    }
}

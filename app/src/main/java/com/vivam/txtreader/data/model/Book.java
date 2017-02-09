package com.vivam.txtreader.data.model;

import java.util.List;

public class Book extends BookFile {

    String charset;
    long createTime;
    long updateTime;
    List<Chapter> chapters;
    long currentChapterId;
    int lastPosition;

    public Book() {
    }

    public Book(BookFile file) {
        setName(file.getName());
        setPath(file.getPath());
        setSize(file.getSize());
        setImported(file.isImported);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getChapterSize() {
        return chapters != null ? chapters.size() : 0;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public long getCurrentChapterId() {
        return currentChapterId;
    }

    public void setCurrentChapterId(long currentChapterId) {
        this.currentChapterId = currentChapterId;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
}

package com.vivam.txtreader.data.model;

import java.util.ArrayList;

public class Book extends BaseModel {

    String path;
    String name;
    String charset;
    long size;
    int chapterSize;
    long createTime;
    long updateTime;
    ArrayList<Chapter> chapters;

    public Book() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getChapterSize() {
        return chapterSize;
    }

    public void setChapterSize(int chapterSize) {
        this.chapterSize = chapterSize;
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

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }
}

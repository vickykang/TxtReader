package com.vivam.txtreader.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Book extends BaseModel {

    String path;
    String name;
    String charset;
    long size;
    int chapterSize;
    ArrayList<String> chapters;
    long createTime;
    long updateTime;

    public Book() {
    }

    protected Book(Parcel in) {
        id = in.readLong();
        path = in.readString();
        name = in.readString();
        charset = in.readString();
        size = in.readLong();
        chapterSize = in.readInt();
        in.readStringList(chapters);
        createTime = in.readLong();
        updateTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(charset);
        dest.writeLong(size);
        dest.writeInt(chapterSize);
        dest.writeStringList(chapters);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
    }

    public static final Parcelable.Creator<Book> CREATOR
            = new Parcelable.Creator<Book>() {

        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

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

    public ArrayList<String> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<String> chapters) {
        this.chapters = chapters;
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
}

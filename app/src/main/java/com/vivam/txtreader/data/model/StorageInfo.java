package com.vivam.txtreader.data.model;

import java.io.Serializable;

/**
 * Created by kangweodai on 20/01/17.
 */

public class StorageInfo implements Serializable {

    public static final String STATE_MOUNTED = "mounted";

    public String path;
    public String state;
    public boolean isRemovable;

    public StorageInfo(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return STATE_MOUNTED.equals(state);
    }
}

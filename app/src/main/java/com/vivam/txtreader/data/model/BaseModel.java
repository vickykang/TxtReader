package com.vivam.txtreader.data.model;

import java.io.Serializable;

/**
 * Created by kangweodai on 17/01/17.
 */

public abstract class BaseModel implements Serializable {

    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

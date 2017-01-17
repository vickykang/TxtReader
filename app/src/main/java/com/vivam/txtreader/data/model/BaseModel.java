package com.vivam.txtreader.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kangweodai on 17/01/17.
 */

public abstract class BaseModel implements Parcelable {

    long id;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

package com.vivam.txtreader.data;

import android.util.Log;
import android.util.LruCache;

import com.vivam.txtreader.data.model.Book;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by kangweodai on 19/01/17.
 */

public class BookLruCache extends LruCache<String, Book> {

    private static final String TAG = "BookLruCache";

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BookLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Book value) {
        int size;
        try {
            size = getObjectSize(value);
        } catch (IOException e) {
            size = (int) value.getSize();
        }
        Log.i(TAG, "size of " + value.getName() + " is " + size);
        return size;
    }

    private int getObjectSize(Object obj) throws IOException {
        if (obj == null) {
            return  0;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();

        byte[] bytes = bos.toByteArray();
        return bytes == null ? 0 : bytes.length;
    }
}

package com.vivam.txtreader.utils;

import android.util.Log;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kangweodai on 17/01/17.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * Returns the name of the file, without suffix.
     *
     * @param path of file
     * @return name without suffix
     */
    public static String getName(String path) {
        String name = path;
        int index = path.lastIndexOf(File.separatorChar);
        if (index > -1 && index < path.length()) {
            name = path.substring(index + 1);
        }
        index = name.lastIndexOf('.');
        if (index > -1) {
            return name.substring(0, index);
        }
        return name;
    }

    /**
     * Returns charset of given file.
     * @param name of the file to detect
     */
    public static String detectCharset(String name) throws IOException {
        byte[] buf = new byte[4096];
        FileInputStream fis = new FileInputStream(name);
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        Log.i(TAG, "'" + name + "' charset is " + encoding);
        detector.reset();
        return encoding;
    }
}

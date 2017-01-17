package com.vivam.txtreader.utils;

import java.io.File;

/**
 * Created by kangweodai on 17/01/17.
 */

public class FileUtils {

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
}

package com.vivam.txtreader.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.vivam.txtreader.data.model.StorageInfo;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by kangweodai on 17/01/17.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static final int ONE_KB = 1024;

    public static final int ONE_MB = ONE_KB * ONE_KB;

    public static final int ONE_GB = ONE_MB * ONE_MB;

    public static final File EXTERNAL_DIR = Environment.getExternalStorageDirectory();
    public static final String EXTERNAL_PATH = EXTERNAL_DIR.getPath();
    public static final String TXT_SUFFIX = ".txt";

    public static String getName(String path) {
        String name = path;
        int index = path.lastIndexOf(File.separatorChar);
        if (index > -1 && index < path.length()) {
            name = path.substring(index + 1);
        }
        return name;
    }

    /**
     * Returns the name of the file, without suffix.
     *
     * @param path of file
     * @return name without suffix
     */
    public static String getNameWithoutSuffix(String path) {
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

    public static int getDepth(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        String[] arr = path.split("/");
        return arr.length;
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

    public static List<StorageInfo> listAvailableStorage(Context context) {
        ArrayList<StorageInfo> storages = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(
                Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            getVolumeList.setAccessible(true);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);
            if (invokes != null) {
                StorageInfo info = null;
                int len = invokes.length;
                for (int i = 0; i < len; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path);
                    File file = new File(info.path);
                    if ((file.exists()) && (file.isDirectory())
                            && (file.canWrite() || file.canRead())) {
                        Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                        String state = null;
                        try {
                            Method getVolumeState = StorageManager.class
                                    .getMethod("getVolumeState", String.class);
                            state = (String) getVolumeState.invoke(storageManager, info.path);
                            info.state = state;
                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }

                        if (info.isMounted()) {
                            info.isRemovable = ((Boolean) isRemovable.invoke(obj, new Objects[0]))
                                    .booleanValue();
                            storages.add(info);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (InvocationTargetException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IllegalAccessException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return storages;
    }
}

package com.fansion.proxydelegatedemo.utils;


import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {
    public static final String APP_CACHE_DIR = "/TruckDriver";
    /**
     * Dex存放路径
     */
    public static final String DEX_PATH_DIR = "/dex/";
    public static final String DEX_NAME = "app.apk";

    /**
     * app安装目录，在app没有卸载前该目录下的数据一般不会被清理
     * //TODO:有待使用root手机验证
     *
     * @param context
     * @return
     */
    public static String getAppPath(Context context) {
        return context.getPackageResourcePath();
    }

    public static String getDexPath(Context context) {
        return Environment.getExternalStorageDirectory() + DEX_PATH_DIR + DEX_NAME;
    }

    public static String getDexPathDir(Context context) {
        return Environment.getExternalStorageDirectory() + DEX_PATH_DIR;
    }

    public static String getCurrentPath(Context context) {
        return context.getDir("dex", 0).getAbsolutePath();
    }

    public static File getInternalDex(Context context) {
        return new File(context.getDir("dex", Context.MODE_PRIVATE), DEX_NAME);
    }

    public static File getOptDex(Context context) {
        return context.getDir("outdex", Context.MODE_PRIVATE);
    }
}

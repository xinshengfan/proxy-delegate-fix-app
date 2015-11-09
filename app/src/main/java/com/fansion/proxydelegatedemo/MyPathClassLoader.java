package com.fansion.proxydelegatedemo;

import android.content.Context;
import android.util.Log;

import com.fansion.proxydelegatedemo.utils.FileUtil;

import java.io.File;
import java.io.IOException;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class MyPathClassLoader extends PathClassLoader {
    private ClassLoader mClassLoader;
    private Context mContext;

    public MyPathClassLoader(Context context, String dexPath, PathClassLoader mClassLoader) {

        super(dexPath, mClassLoader);
        this.mClassLoader = mClassLoader;
        this.mContext = context;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        File file = null;
        try {
            clazz = mClassLoader.loadClass(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (clazz != null) {
            return clazz;
        }
        try {
            file = new File(FileUtil.getDexPath(mContext));
            Log.i("fan", "文件是否存在？" + file.exists());
            if (file.exists()) {
                Log.i("fan", "文件存在：" + file.getAbsolutePath() + " , " + mContext.getCacheDir()
                        + " ,name:" + name + " , isDexOptNeeded:" + DexFile.isDexOptNeeded
                        (file.getAbsolutePath()));
//                DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), mContext
//                        .getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), 0);
                DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), File.createTempFile
                        ("opt", "dex", mContext.getCacheDir()).getPath(), 0);
//                Class obj = dexFile.loadClass(name, ClassLoader.getSystemClassLoader());
                Class obj = Class.forName(name);
                Log.i("fan", "加载的类名:" + obj.getName());
                return obj;
            }
            Log.i("fan", "dex文件不存在");
        } catch (IOException e) {
            Log.i("fan", "加载dex文件异常:" + e.getMessage());
            e.printStackTrace();
        }
        return super.findClass(name);
    }

}

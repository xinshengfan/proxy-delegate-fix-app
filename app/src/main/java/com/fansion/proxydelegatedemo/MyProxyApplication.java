package com.fansion.proxydelegatedemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.fansion.proxydelegatedemo.utils.FileUtil;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 此Application作为app启动后的第一个入口，需要在AndroidManifest.xml中加入;
 * 原程序入口application的信息以meta-data的形式存储
 * 一般在{@link Application#onCreate()}中做替换已足够，但若用到ContentProvider时，{@link android.content
 * .ContentProvider#onCreate()}方法是在{@link Application#onCreate()}之前.
 * 因为Application是继承自{@link android.content.ContextWrapper},在ContextWrapper中系统在构建完成完善的Context
 * 之后第一次回调是通过{@link android.content.ContextWrapper#attachBaseContext(Context)
 * }方法，可在ProxyApplication中重写该方法来获取刚产生的Context来转换ClassLoader
 */
public class MyProxyApplication extends ProxyApplication {

    @Override
    protected void initProxyApplication() {
        Log.i("fan", "MyProxyApplication initProxyApplication");
        Context context = getBaseContext();
        Field loadedApkField;
        Field field;
        try {
            //用反射获取Application中的mPackageInfo
            loadedApkField = context.getClass().getDeclaredField("mPackageInfo");
            loadedApkField.setAccessible(true);
            Object mPackageInfo = loadedApkField.get(context);
            Log.i("fan", "用反射获取Application中的mPackageInfo");
            //用反射获取LoadApk中的mClassLoader
            Log.i("fan", "用反射获取LoadApk中的mClassLoader");
            field = mPackageInfo.getClass().getDeclaredField("mClassLoader");
            field.setAccessible(true);
            //拿到originalclassloader
            Log.i("fan", "拿到originalclassloader");
            Object mClassLoader = field.get(mPackageInfo);
            //创建自定义的classloader
            Log.i("fan", "创建自定义的classloader");
            ClassLoader loader;
            if (FileUtil.getInternalDex(this).exists()) {
                loader = new PathClassLoader(FileUtil.getInternalDex(this).getAbsolutePath(), ClassLoader
                        .getSystemClassLoader());
            } else {
                loader = new PathClassLoader(this.getApplicationInfo().sourceDir, ClassLoader
                        .getSystemClassLoader());
            }
//            ClassLoader loader = new MyPathClassLoader(this,
//                    this.getApplicationInfo().sourceDir,
//                    (PathClassLoader) mClassLoader);
//            ClassLoader loader = getMyClassLoader();
            //替换originalclassloader为自定义的classloader
            Log.i("fan", "替换originalclassloader为自定义的classloader");
            field.set(mPackageInfo, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClassLoader getMyClassLoader() {
        return new DexClassLoader(FileUtil.getInternalDex(this).getAbsolutePath(), FileUtil
                .getOptDex(this).getAbsolutePath(),
                null, getClassLoader());
    }
}

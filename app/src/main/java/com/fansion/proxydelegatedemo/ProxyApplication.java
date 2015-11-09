package com.fansion.proxydelegatedemo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 代理模式下，Proxy/Delegate框架中实现的Application的基类，用于做代理的的Application;
 * 提供一个用于替换当前ProxyApplication(实现类)的ClassLoader成父类的ClassLoader的抽象方法
 */
public abstract class ProxyApplication extends Application {
    /**
     * 要保证替换当前ProxyApplication的ClassLoader为父类的ClassLoader足够早，否则会出现替换不干净的情况;
     * 就有会程序中大部分使用的DelegateApplication的ClassLoader，而一小部分使用的ProxyApplication的ClassLoader,
     * 这样会出现一些未知的bug;
     */
    protected abstract void initProxyApplication();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initProxyApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("fan", "ProxyApplication onCreate");
        String className = getApplicationName();
        Log.i("fan", "ProxyApplication 获取包名：" + className);
        Application delegat = loadClassLoader(className);
        Log.i("fan", "ProxyApplication 加载实际delagateApplication");
        setBaseContext(delegat);
        Log.i("fan", "ProxyApplication 创建实际delegateApplication 实例");
    }

    private void setBaseContext(Application delegate) {
        try {
            Method attach = Application.class.getDeclaredMethod("attach", Context.class);
            attach.setAccessible(true);
//            attach.invoke(delegate, base);
            attach.invoke(delegate, getBaseContext());
            delegate.onCreate();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Application loadClassLoader(String className) {
        Application delegate = null;
        try {
            Class delegateClass = Class.forName(className, true, getClassLoader());
            delegate = (Application) delegateClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return delegate;
    }

    private String getApplicationName() {
        String className = "android.app.Application";
        String key = "DELEGATE_APPLICATION_CLASS_NAME";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(super.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            if (bundle != null && bundle.containsKey(key)) {
                className = bundle.getString(key);
                if (className.startsWith(".")) {
                    className = super.getPackageName() + className;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return className;
    }

    @Override
    public String getPackageName() {
        /**
         * 因为ProxyApplication对象的getPackageName()函数与ContentProvider对应的包名相同，
         * 就会复用ProxyApplication对象作为Context，而不会再创建一个新的packageContext ;
         * 故只需将使两个getPackageName()不一致即可
         */
        return "";
    }
}

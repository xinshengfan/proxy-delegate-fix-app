package com.fansion.proxydelegatedemo.ui;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fansion.proxydelegatedemo.R;
import com.fansion.proxydelegatedemo.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements OnClickListener {
    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("fan", "*****MainActivity onCreate*****");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mContent = (TextView) findViewById(R.id.content);
        findViewById(R.id.start_replace).setOnClickListener(this);
        printLog("******这是版本5********");
        printLog(getApplicationName());
        getPaths();
        getCurrentPath();
    }

    private void getCurrentPath() {
        String appDir = FileUtil.getCurrentPath(this);
        printLog("app根目录:" + appDir);
        File dir = new File(appDir);
        if (dir.exists() && dir.isDirectory()) {
            for (String name : dir.list()) {
                printLog(name);
            }
        }
    }

    private void getPaths() {
        String appDir = FileUtil.getAppPath(this);
        printLog("app根目录:" + appDir);
        File dir = new File(appDir);
        if (dir.exists() && dir.isDirectory()) {
            for (String name : dir.list()) {
                printLog(name);
            }
        }
    }

    private void printLog(String content) {
        StringBuilder sb = new StringBuilder(mContent.getText().toString().trim());
        sb.append("\n").append(content);
        mContent.setText(sb.toString());
    }

    private String getApplicationName() {
        String className = "android.app.Application";
        try {
            String key = "DELEGATE_APPLICATION_CLASS_NAME";
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(super.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            if (bundle != null && bundle.containsKey(key)) {
                className = bundle.getString(key);
                Log.i("Seven", "name1=" + className);
                if (className.startsWith(".")) {
                    className = super.getPackageName() + className;
                    Log.i("Seven", "name2=" + className);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return className;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_replace:
                startReplaceDex();
                break;
            default:
                break;
        }
    }

    /**
     * 开始替换dex文件，开始下载dex文件,命名为{@link com.fansion.proxydelegatedemo.utils.FileUtil#DEX_NAME},
     * 并放在{@link com.fansion.proxydelegatedemo.utils.FileUtil#DEX_PATH}下
     */
    private void startReplaceDex() {
        //此处将放在assert中的文件拷贝过来 ,下次启动程序生效
        printLog("复制文件目的：" + FileUtil.getInternalDex(this).getAbsolutePath());

        File dexFile = FileUtil.getInternalDex(this);
        if (dexFile.exists()) {
            printLog("文件" + dexFile.getName() + "存在，开始删除");
            dexFile.delete();
        }
        printLog("创建新文件：" + dexFile.getAbsolutePath());
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            printLog("开始复制文件,目录是否存在");
            inputStream = this.getAssets().open(FileUtil.DEX_NAME);
            fos = new FileOutputStream(FileUtil.getInternalDex(this));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            printLog("复制文件完成，下次启动生效");
        } catch (IOException e) {
            printLog("复制文件异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

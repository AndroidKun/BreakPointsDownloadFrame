package com.androidkun.breakpoints;

import android.os.Environment;

/**
 * Created by kun on 2016/11/10.
 * 配置类
 */
public class Config {
    /**
     * 文件下载地址
     */
    public final static String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/downloads/";
}

package com.androidkun.breakpoints.utils;

import android.content.Context;
import android.content.Intent;

import com.androidkun.breakpoints.bean.FileBean;
import com.androidkun.breakpoints.services.DownloadService;

/**
 * Created by G40-70M on 2016/11/16.
 */
public class DownloadUtils {

     public static void downLoad(Context context,String url,String savePath,String fileName,int downloadThreadCount){
         FileBean fileBean = new FileBean(0,savePath,fileName,url,0);
         fileBean.setThreadCount(downloadThreadCount);
         Intent startIntent = new Intent(context, DownloadService.class);
         startIntent.setAction(DownloadService.ACTION_START);
         startIntent.putExtra("FileBean", fileBean);
         context.startService(startIntent);
    }

    public static void pauseDownLoad(Context context,String url){
        FileBean fileBean = new FileBean(0,null,null,url,0);
        Intent pauseIntent = new Intent(context, DownloadService.class);
        pauseIntent.setAction(DownloadService.ACTION_PAUSE);
        pauseIntent.putExtra("FileBean", fileBean);
        context.startService(pauseIntent);
    }
}

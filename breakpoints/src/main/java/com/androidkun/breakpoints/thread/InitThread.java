package com.androidkun.breakpoints.thread;

import com.androidkun.breakpoints.bean.FileBean;
import com.androidkun.breakpoints.event.DownloadData;
import com.androidkun.breakpoints.event.EventMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 坤 on 2016/11/10.
 * 初始化线程
 */
public class InitThread extends Thread{

    private FileBean fileBean;

    public InitThread(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    @Override
    public void run() {
        HttpURLConnection connection =null;
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = new URL(fileBean.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            int fileLength = -1;
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                fileLength = connection.getContentLength();
            }
            if(fileLength<=0) return;
            File dir = new File(fileBean.getSavePath());
            if(!dir.exists()){
                dir.mkdir();
            }
            File file = new File(dir,fileBean.getFileName());
            randomAccessFile = new RandomAccessFile(file,"rwd");
            randomAccessFile.setLength(fileLength);
            fileBean.setLength(fileLength);
            EventMessage eventMessage = new EventMessage(1,fileBean);
            EventBus.getDefault().post(eventMessage);
        }catch (Exception e){
            DownloadData downloadData = new DownloadData();
            downloadData.setUrl(fileBean.getUrl());
            downloadData.setMsg(e.getMessage());
            EventMessage eventMessage = new EventMessage(EventMessage.TYPE_ERROR,downloadData);
            EventBus.getDefault().post(eventMessage);
            e.printStackTrace();
        }
    }
}

package com.androidkun.breakpoints.thread;

import com.androidkun.breakpoints.Config;
import com.androidkun.breakpoints.bean.FileBean;
import com.androidkun.breakpoints.bean.ThreadBean;
import com.androidkun.breakpoints.callback.DownloadCallBack;
import com.androidkun.breakpoints.event.DownloadData;
import com.androidkun.breakpoints.event.EventMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kun on 2016/11/11.
 * 下载线程
 */
public class DownloadThread extends Thread {

    private FileBean fileBean;
    private ThreadBean threadBean;
    private DownloadCallBack callback;
    private Boolean isPause = false;

    public DownloadThread(FileBean fileBean, ThreadBean threadBean, DownloadCallBack callback) {
        this.fileBean = fileBean;
        this.threadBean = threadBean;
        this.callback = callback;
    }

    public void setPause(Boolean pause) {
        isPause = pause;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        RandomAccessFile raf = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(threadBean.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            //设置下载起始位置
            int start = threadBean.getStart() + threadBean.getFinished();
            connection.setRequestProperty("Range","bytes="+start+"-"+threadBean.getEnd());
            //设置写入位置
            File file = new File(Config.downLoadPath,fileBean.getFileName());
            raf = new RandomAccessFile(file,"rwd");
            raf.seek(start);
            //开始下载
            if(connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL){
                inputStream  = connection.getInputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(bytes))!=-1){
                    raf.write(bytes,0,len);
                    //将加载的进度回调出去
                    callback.progressCallBack(len);
                    //保存进度
                    threadBean.setFinished(threadBean.getFinished()+len);
                    //在下载暂停的时候将下载进度保存到数据库
                    if(isPause){
                        callback.pauseCallBack(threadBean);
                        return;
                    }
                }
                //下载完成
                callback.threadDownLoadFinished(threadBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            DownloadData downloadData = new DownloadData();
            downloadData.setUrl(fileBean.getUrl());
            downloadData.setMsg(e.getMessage());
            EventMessage eventMessage = new EventMessage(EventMessage.TYPE_ERROR,downloadData);
            EventBus.getDefault().post(eventMessage);
        } finally {
            try {
                inputStream.close();
                raf.close();
                connection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
                DownloadData downloadData = new DownloadData();
                downloadData.setUrl(fileBean.getUrl());
                downloadData.setMsg(e.getMessage());
                EventMessage eventMessage = new EventMessage(EventMessage.TYPE_ERROR,downloadData);
                EventBus.getDefault().post(eventMessage);
            }
        }
    }
}

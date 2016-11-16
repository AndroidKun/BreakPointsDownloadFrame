package com.androidkun.breakpoints.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidkun.breakpoints.bean.FileBean;
import com.androidkun.breakpoints.event.DownloadData;
import com.androidkun.breakpoints.event.EventMessage;
import com.androidkun.breakpoints.thread.DownloadTask;
import com.androidkun.breakpoints.thread.InitThread;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kun on 2016/11/10.
 * 下载服务
 */
public class DownloadService extends Service{

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    /**
     * 下载任务集合
     */
    private List<DownloadTask> downloadTasks = new ArrayList<>();
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    private NetConnectionReceiver netConnectionReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        netConnectionReceiver = new NetConnectionReceiver();
        registerReceiver(netConnectionReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACTION_START)){
            Log.w("AAA","ACTION_START");
            FileBean fileBean = (FileBean) intent.getSerializableExtra("FileBean");
            for(DownloadTask downloadTask:downloadTasks){
                if(downloadTask.getFileBean().getUrl().equals(fileBean.getUrl())){
                    //如果下载任务中以后该文件的下载任务 则直接返回
                    DownloadData downloadData = new DownloadData();
                    downloadData.setUrl(fileBean.getUrl());
                    downloadData.setMsg("下载任务已存在");
                    EventMessage eventMessage = new EventMessage(EventMessage.TYPE_ERROR,downloadData);
                    EventBus.getDefault().post(eventMessage);
                    return super.onStartCommand(intent, flags, startId);
                }
            }
            executorService.execute(new InitThread(fileBean));
        }else if(intent.getAction().equals(ACTION_PAUSE)){
            FileBean fileBean = (FileBean) intent.getSerializableExtra("FileBean");
            DownloadTask pauseTask = null;
            for(DownloadTask downloadTask:downloadTasks){
                if(downloadTask.getFileBean().getUrl().equals(fileBean.getUrl())){
                    downloadTask.pauseDownload();
                    pauseTask = downloadTask;
                    break;
                }
            }
            //将下载任务移除
            downloadTasks.remove(pauseTask);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()){
            case 1://下载线程初始化完毕
                FileBean fileBean = (FileBean) eventMessage.getObject();
                Log.w("AAA","length:"+fileBean.getLength());
                DownloadData downloadData = new DownloadData();
                downloadData.setUrl(fileBean.getUrl());
                downloadData.setMsg("开始下载");
                downloadData.setLength(fileBean.getLength());
                EventMessage eventMsg = new EventMessage(EventMessage.TYPE_START,downloadData);
                EventBus.getDefault().post(eventMsg);
                //开始下载
                DownloadTask downloadTask = new DownloadTask(this,fileBean,fileBean.getThreadCount());
                downloadTasks.add(downloadTask);
                break;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(netConnectionReceiver);
    }

    private class NetConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                //网络连接已断开
                for (DownloadTask downloadTask : downloadTasks) {
                    downloadTask.pauseDownload();//暂停所有下载任务
                    //发送下载完成事件
                    DownloadData downloadData = new DownloadData();
                    downloadData.setUrl(downloadTask.getFileBean().getUrl());
                    downloadData.setLength(downloadTask.getFileBean().getLength());
                    downloadData.setMsg("网络断开暂停下载");
                    EventMessage message = new EventMessage(EventMessage.TYPE_PAUSE,downloadData);
                    EventBus.getDefault().post(message);
                }
            } else {
                //网络连接已连接
                for (DownloadTask downloadTask : downloadTasks) {
                    downloadTask.startDownload();//继续所有下载任务
                    DownloadData downloadData = new DownloadData();
                    downloadData.setUrl(downloadTask.getFileBean().getUrl());
                    downloadData.setLength(downloadTask.getFileBean().getLength());
                    downloadData.setMsg("网络恢复继续下载");
                    EventMessage message = new EventMessage(EventMessage.TYPE_START,downloadData);
                    EventBus.getDefault().post(message);
                }
            }
        }
    }
}

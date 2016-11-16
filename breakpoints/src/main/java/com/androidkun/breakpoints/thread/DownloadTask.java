package com.androidkun.breakpoints.thread;

import android.content.Context;
import android.util.Log;

import com.androidkun.breakpoints.bean.FileBean;
import com.androidkun.breakpoints.bean.ThreadBean;
import com.androidkun.breakpoints.callback.DownloadCallBack;
import com.androidkun.breakpoints.db.dao.ThreadDao;
import com.androidkun.breakpoints.db.impl.ThreadDaoImpl;
import com.androidkun.breakpoints.event.DownloadData;
import com.androidkun.breakpoints.event.EventMessage;
import com.androidkun.breakpoints.services.DownloadService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kun on 2016/11/11.
 * 下载任务
 */
public class DownloadTask implements DownloadCallBack {

    private FileBean fileBean;
    private int downloadThreadCount;
    private ThreadDao dao;

    /**
     * 总下载完成进度
     */
    private int finishedProgress = 0;
    /**
     * 下载线程信息集合
     */
    private List<ThreadBean> threads;
    /**
     * 下载线程集合
     */
    private List<DownloadThread> downloadThreads = new ArrayList<>();

    public DownloadTask(Context context, FileBean fileBean, int downloadThreadCount) {
        this.fileBean = fileBean;
        this.downloadThreadCount = downloadThreadCount;
        dao = new ThreadDaoImpl(context);
        //初始化下载线程
        initDownThreads();
    }

    private void initDownThreads() {
        //查询数据库中的下载线程信息
        threads = dao.getThreads(fileBean.getUrl());
        if(threads.size()==0){//如果列表没有数据 则为第一次下载
            Log.w("AAA","第一次下载");
            //根据下载的线程总数平分各自下载的文件长度
            int length = fileBean.getLength()/downloadThreadCount;
            for(int i = 0; i<downloadThreadCount; i++){
                ThreadBean thread = new ThreadBean(i,fileBean.getUrl(),i * length,
                        (i + 1) * length -1,0);
                if(i == downloadThreadCount-1){
                    thread.setEnd(fileBean.getLength());
                }
                //将下载线程保存到数据库
                dao.insertThread(thread);
                threads.add(thread);
            }
        }
        //创建下载线程开始下载
        for(ThreadBean thread : threads){
            finishedProgress+= thread.getFinished();
            DownloadThread downloadThread = new DownloadThread(fileBean, thread, this);
            DownloadService.executorService.execute(downloadThread);
            downloadThreads.add(downloadThread);
        }
        Log.w("AAA"," 开始下载："+finishedProgress);
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        finishedProgress = 0;
        threads.clear();
        downloadThreads.clear();
        initDownThreads();
    }

    /**
     * 暂停下载
     */
    public void pauseDownload(){
        for(DownloadThread downloadThread : downloadThreads){
            if (downloadThread!=null) {
                downloadThread.setPause(true);
            }
        }
    }

    @Override
    public void pauseCallBack(ThreadBean threadBean) {
        //保存下载进度到数据库
        Log.w("AAA","保存数据:"+threadBean.toString());
        dao.updateThread(threadBean.getUrl(),threadBean.getId(),threadBean.getFinished());
    }

    private long curTime = 0;
    @Override
    public void progressCallBack(int length) {
        finishedProgress += length;
        //每500毫秒发送刷新进度事件
        if(System.currentTimeMillis() - curTime >500 || finishedProgress==fileBean.getLength()){
            fileBean.setFinished(finishedProgress);
            DownloadData downloadData = new DownloadData();
            downloadData.setUrl(fileBean.getUrl());
            downloadData.setProgress((int) (finishedProgress*1.0/fileBean.getLength()*100));
            downloadData.setLength(fileBean.getLength());
            downloadData.setMsg("下载进度回调");
            EventMessage message = new EventMessage(EventMessage.TYPE_PROGRESS,downloadData);
            EventBus.getDefault().post(message);
            curTime  = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized void threadDownLoadFinished(ThreadBean threadBean) {
        for(ThreadBean bean:threads){
            if(bean.getId() == threadBean.getId()){
                //从列表中将已下载完成的线程信息移除
                threads.remove(bean);
                break;
            }
        }
        if(threads.size()==0){//如果列表size为0 则所有线程已下载完成
            //删除数据库中的信息
            dao.deleteThread(fileBean.getUrl());
            //发送下载完成事件
            DownloadData downloadData = new DownloadData();
            downloadData.setUrl(fileBean.getUrl());
            downloadData.setMsg("下载完成");
            downloadData.setFilePath(fileBean.getSavePath()+fileBean.getFileName());
            EventMessage message = new EventMessage(EventMessage.TYPE_FINISHED,downloadData);
            EventBus.getDefault().post(message);
        }
    }

    public FileBean getFileBean() {
        return fileBean;
    }

}

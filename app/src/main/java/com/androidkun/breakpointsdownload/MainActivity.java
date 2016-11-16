package com.androidkun.breakpointsdownload;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidkun.breakpoints.event.DownloadData;
import com.androidkun.breakpoints.event.EventMessage;
import com.androidkun.breakpointsdownload.adapter.RecyclerViewListAdapter;
import com.androidkun.breakpointsdownload.bean.DownloadInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewListAdapter adapter;

    /**
     * 文件地址
     */
    private static final String url1 = "http://down.360safe.com/instmobilemgr.exe";
    private static final String url2 = "http://dldir1.qq.com/invc/cyclone/QQDownload_Setup_48_773_400.exe";
    private static final String url3 = "http://dldir1.qq.com/qqyy/pc/QQPlayer_Setup_39_936.exe";
    private static final String url4 = "http://pkg.stream.qqmusic.qq.com/QQMusicForYQQ.exe";

    /**
     * 文件保存路径
     */
    public final static String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/downloads/";
    /**
     * 每个任务同时下载线程数
     */
    public static final int downThreadCount = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        List<DownloadInfo> downloadInfos = new ArrayList<>();
        DownloadInfo downInfo1 = new DownloadInfo(url1,downLoadPath,"instmobilemgr.exe",downThreadCount);
        DownloadInfo downInfo2 = new DownloadInfo(url2,downLoadPath,"QQDownload_Setup_48_773_400.exe",downThreadCount);
        DownloadInfo downInfo3 = new DownloadInfo(url3,downLoadPath,"QQPlayer_Setup_39_936.exe",downThreadCount);
        DownloadInfo downInfo4 = new DownloadInfo(url4,downLoadPath,"QQMusicForYQQ.exe",downThreadCount);
        downInfo1.setDownState("未开始");
        downInfo2.setDownState("未开始");
        downInfo3.setDownState("未开始");
        downInfo4.setDownState("未开始");
        downloadInfos.add(downInfo1);
        downloadInfos.add(downInfo2);
        downloadInfos.add(downInfo3);
        downloadInfos.add(downInfo4);
        adapter = new RecyclerViewListAdapter(this,downloadInfos);
        recyclerView.setAdapter(adapter);
    }

    private void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()){
            case EventMessage.TYPE_START://开始下载
                DownloadData start = (DownloadData) eventMessage.getObject();
                adapter.refreshState(start.getUrl(),start.getLength(),getResources().getString(R.string.state_downloading));
                break;
            case EventMessage.TYPE_PROGRESS://下载
                DownloadData progress = (DownloadData) eventMessage.getObject();
                adapter.updateProgress(progress.getUrl(),progress.getProgress());
                break;
            case EventMessage.TYPE_FINISHED://下载完成
                DownloadData finished = (DownloadData) eventMessage.getObject();
                adapter.refreshState(finished.getUrl(),finished.getLength(),getResources().getString(R.string.state_finished));
                break;
            case EventMessage.TYPE_ERROR://下载失败
                DownloadData error = (DownloadData) eventMessage.getObject();
                adapter.refreshState(error.getUrl(),error.getLength(),getResources().getString(R.string.state_fail));
                break;
            case EventMessage.TYPE_PAUSE://下载失败
                DownloadData pause = (DownloadData) eventMessage.getObject();
                adapter.refreshState(pause.getUrl(),pause.getLength(),getResources().getString(R.string.state_pause));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

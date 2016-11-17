# BreakPointsDownloadFrame
##这是一个多文件分段断点续传框架，可以帮助我们快速实现多文件分段断点续传功能。
#[CSDN地址](http://blog.csdn.net/a1533588867/article/details/53188953)
 ![github](https://github.com/AndroidKun/BreakPointsDownloadFrame/blob/master/pic/device-2016-11-16-164700.png)
#集成步骤 
##1.在Module下的build.gradle中添加依赖

     dependencies {
       ... ...
       compile 'com.androidkun:breakpoints:1.0.0'
     }


##2.开始下载以及暂停下载

    viewHoder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadUtils.downLoad(context, downloadInfo.getUrl(), downloadInfo.getSavePath(), downloadInfo.getFileName(), downloadInfo.getThreadCount());
            }
        });
        viewHoder.btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadInfo.setDownState(context.getResources().getString(R.string.state_pause));
                viewHoder.textState.setText(downloadInfo.getDownState());
                DownloadUtils.pauseDownLoad(context, downloadInfo.getUrl());
            }
        });

###参数解释：
    /**
     * 开始下载
     * @param context
     *         上下文
     * @param url
     *        下载链接
     * @param savePath
     *        保存路径
     * @param fileName
     *        下载线程数
     * @param downloadThreadCount
     */
     public static void downLoad(Context context,String url,String savePath,String fileName,int downloadThreadCount){
      ... ...
     }
      /**
     * 暂停下载
     * @param context
     *        上下文
     * @param url
     *        下载链接
     */
    public static void pauseDownLoad(Context context,String url){
        ... ...
    }

##3.在Activity中注册EventBus获取下载进度和下载状态刷新UI
    ...
    public class MainActivity extends AppCompatActivity {

    ... ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initData() {
        ... ...
    }

    private void initView(){
        ... ...
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()){
            case EventMessage.TYPE_START://开始下载
                DownloadData start = (DownloadData) eventMessage.getObject();
                adapter.refreshState(start.getUrl(),start.getLength(),getResources().getString(R.string.state_downloading));
                break;
            case EventMessage.TYPE_PROGRESS://下载进度
                DownloadData progress = (DownloadData) eventMessage.getObject();
                //刷新列表进度
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
            case EventMessage.TYPE_PAUSE://下载暂停
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

###在getEventMessage（）方法中可以获取到当前下载状态，DownloadData 封装了下载文件的url，下载进度，文件长度等信息。根据EventMessage的type区分事件类型，再做出相应处理。

    public class DownloadData {
    /**
     * 下载链接
     */
    private String url;
    /**
     * 下载进度（0-100）
     */
    private int progress;
    /**
     * 文件大小
     */
    private int length;
    /**
     * 下载消息
     */
    private String msg;
    /**
     * 文件路径
     */
    private String filePath;

    ... ...

    }

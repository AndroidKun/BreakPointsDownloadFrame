package com.androidkun.breakpointsdownload.bean;

/**
 * Created by kun on 2016/11/16.
 */
public class DownloadInfo {
    private String url;
    private String savePath;
    private String fileName;
    private int length;
    private int progress;
    private int threadCount;
    private String downState;

    public DownloadInfo(String url, String savePath, String fileName, int threadCount) {
        this.url = url;
        this.savePath = savePath;
        this.fileName = fileName;
        this.threadCount = threadCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getDownState() {
        return downState;
    }

    public void setDownState(String downState) {
        this.downState = downState;
    }
}

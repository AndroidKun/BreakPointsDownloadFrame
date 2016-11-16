package com.androidkun.breakpoints.bean;

import java.io.Serializable;

/**
 * Created by kun on 2016/11/10.
 */
public class FileBean implements Serializable {

    private int id;
    private String savePath;
    private String fileName;
    private String url;
    private int length;
    private int finished;
    private int threadCount;

    public FileBean() {
    }

    public FileBean(int id, String fileName, String url,  int finished) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.finished = finished;
    }
    public FileBean(int id, String savePath,String fileName, String url,  int finished) {
        this.id = id;
        this.savePath = savePath;
        this.fileName = fileName;
        this.url = url;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public String toString() {
        int progress = (int) (finished*1.0f/length *100);
        return "FileBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                "，progress="+progress+
                '}';
    }
}

package com.androidkun.breakpoints.event;

/**
 * Created by kun on 2016/11/10.
 */
public class EventMessage {

    public static final int TYPE_START = 0x4;
    public static final int TYPE_PROGRESS = 0x5;
    public static final int TYPE_FINISHED = 0x6;
    public static final int TYPE_ERROR = 0x7;
    public static final int TYPE_PAUSE = 0x8;

    /**
     * 1 获取下载文件的长度
     * 2 下载完成
     * 3 下载进度刷新
     */
    private int type;

    private Object object;

    public EventMessage(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

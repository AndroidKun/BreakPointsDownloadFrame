package com.androidkun.breakpoints.db.dao;

import com.androidkun.breakpoints.bean.ThreadBean;

import java.util.List;

/**
 * Created by kun on 2016/11/10.
 */
public interface ThreadDao {
    /**
     * 插入下载线程信息
     * @param threadBean
     */
    void insertThread(ThreadBean threadBean);

    /**
     * 更新下载线程信息
     * @param url
     * @param thread_id
     * @param finished
     */
    void updateThread(String url, int thread_id, int finished);

    /**
     * 删除下载线程
     * @param url
     */
    void deleteThread(String url);

    /**
     * 获取下载线程
     * @param url
     * @return
     */
    List<ThreadBean> getThreads(String url);

    /**
     * 判断下载线程是否存在
     * @param url
     * @param thread_id
     * @return
     */
    boolean isExists(String url, int thread_id);
}

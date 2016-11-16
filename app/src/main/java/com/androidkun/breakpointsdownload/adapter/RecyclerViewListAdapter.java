package com.androidkun.breakpointsdownload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidkun.breakpoints.utils.DownloadUtils;
import com.androidkun.breakpointsdownload.R;
import com.androidkun.breakpointsdownload.bean.DownloadInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kun on 2016/11/11.
 */
public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.ViewHolder> {

    List<DownloadInfo> datas;
    Context context;

    public RecyclerViewListAdapter(Context context, List<DownloadInfo> datas) {
        if (datas == null) datas = new ArrayList<>();
        this.datas = datas;
        this.context = context;
    }

    public List<DownloadInfo> getDatas() {
        return datas;
    }

    public void setDatas(List<DownloadInfo> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<DownloadInfo> newDatas) {
        int last = datas.size();
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        this.datas.addAll(newDatas);
        notifyItemInserted(last + 1);
    }

    public void removeItem(int position) {
        if (datas != null && datas.size() > position) {
            datas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearDatas() {
        this.datas = null;
        notifyDataSetChanged();
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        if (datas == null) return 0;
        return datas.size();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        return new ViewHolder(view);
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textState;
        ProgressBar progressBar;
        Button btnStart;
        Button btnPause;

        public ViewHolder(View convertView) {
            super(convertView);
            textName = (TextView) convertView.findViewById(R.id.textName);
            textState = (TextView) convertView.findViewById(R.id.textState);
            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            btnStart = (Button) convertView.findViewById(R.id.btnStart);
            btnPause = (Button) convertView.findViewById(R.id.btnPause);
        }
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHoder, final int position) {
        final DownloadInfo downloadInfo = datas.get(position);
        viewHoder.textName.setText(downloadInfo.getFileName());
        viewHoder.textState.setText(downloadInfo.getDownState());
        viewHoder.progressBar.setProgress(downloadInfo.getProgress());
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
    }

    private long curTime = 0;

    /**
     * 刷新进度
     *
     * @param url
     * @param progress
     */
    public void updateProgress(String url, int progress) {
        for (DownloadInfo data : datas) {
            if (data.getUrl().equals(url)) {
                data.setProgress(progress);
                if (System.currentTimeMillis() - curTime > 500) {
                    curTime = System.currentTimeMillis();
                    notifyDataSetChanged();
                }
                return;
            }
        }
    }

    /**
     * 刷新下载状态
     *
     * @param url
     * @param length
     * @param state
     */
    public void refreshState(String url, int length, String state) {
        for (DownloadInfo data : datas) {
            if (data.getUrl().equals(url)) {
                data.setLength(length);
                data.setDownState(state);
                notifyDataSetChanged();
                return;
            }
        }
    }
}

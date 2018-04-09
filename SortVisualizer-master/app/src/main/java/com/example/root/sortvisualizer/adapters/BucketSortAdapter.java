package com.example.root.sortvisualizer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.sortvisualizer.BucketSortModel;
import com.example.root.sortvisualizer.HeapSortModel;
import com.example.root.sortvisualizer.R;
import com.example.root.sortvisualizer.holders.BucketSortHolder;
import com.example.root.sortvisualizer.holders.HeapSortHolder;

import java.util.List;

/**
 * Created by root on 15/5/16.
 */
public class BucketSortAdapter extends RecyclerView.Adapter<BucketSortHolder> {

    private List<BucketSortModel> mList; // list for adaptor
    private Context mContext;

    public BucketSortAdapter(List<BucketSortModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public BucketSortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.bucket_sort_adapter_layout, parent, false);
        return new BucketSortHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BucketSortHolder holder, int position) {
        if (mList.get(position).getmSize() != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(" "+position+" >> ");
            for (int i = 0; i < mList.get(position).getmSize(); i++) {
                if(i==mList.get(position).getmSize()-1) {
                    sb.append(mList.get(position).getMbucketList().get(i) + "");
                } else {
                    sb.append(mList.get(position).getMbucketList().get(i) + ",");
                }
            }
            holder.mNumbers.setText(sb);
        } else {
            holder.mNumbers.setText(" "+position+" >> ");
        }
    }

    public List<BucketSortModel> getmList() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

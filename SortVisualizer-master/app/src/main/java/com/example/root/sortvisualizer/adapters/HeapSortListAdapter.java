package com.example.root.sortvisualizer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.sortvisualizer.HeapSortModel;
import com.example.root.sortvisualizer.R;
import com.example.root.sortvisualizer.holders.HeapSortHolder;

import java.util.List;

/**
 * Created by root on 14/5/16.
 */
public class HeapSortListAdapter extends RecyclerView.Adapter<HeapSortHolder> {

    private List<HeapSortModel> mList;
    private Context mContext;

    public HeapSortListAdapter(List<HeapSortModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public HeapSortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.heap_sort_adapter_layout, parent, false);
        return new HeapSortHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HeapSortHolder holder, int position) {
        holder.mNumberView.setText(mList.get(position).getmNumber() + "");

        if(mList.get(position).getColor()==0) {
            holder.mNumberView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.mSubText.setVisibility(View.GONE);
            holder.mNumberView.setTypeface(null,Typeface.NORMAL);
        } else if(mList.get(position).getColor()==1) {
            holder.mNumberView.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.mSubText.setVisibility(View.VISIBLE);
            holder.mNumberView.setTypeface(null, Typeface.NORMAL);
        } else if(mList.get(position).getColor()==2) {
            holder.mNumberView.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.mSubText.setVisibility(View.VISIBLE);
            holder.mNumberView.setTypeface(null, Typeface.BOLD_ITALIC);
        }
    }

    public List<HeapSortModel> getmList() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

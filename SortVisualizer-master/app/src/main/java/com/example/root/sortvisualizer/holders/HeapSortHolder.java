package com.example.root.sortvisualizer.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.root.sortvisualizer.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by root on 14/5/16.
 */
public class HeapSortHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.numbers)
    public TextView mNumberView;
    @Bind(R.id.subText)
    public TextView mSubText;

    public HeapSortHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

}

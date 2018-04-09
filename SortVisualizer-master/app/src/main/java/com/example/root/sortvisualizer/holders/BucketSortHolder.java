package com.example.root.sortvisualizer.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.root.sortvisualizer.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by root on 15/5/16.
 */
public class BucketSortHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.bucket_list)
    public TextView mNumbers;

    public BucketSortHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}


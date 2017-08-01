package com.machadalo.audit.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.machadalo.audit.ImageLoader;
import com.machadalo.audit.R;
import com.machadalo.audit.extras.QueueData;

import java.util.List;

/**
 * Created by DELL-PC on 2/8/2016.
 */
public class SubmittedDataAdapter extends RecyclerView.Adapter<SubmittedDataAdapter.MyViewHolder> {
    ImageLoader imgLoader;
    private List<QueueData> DatasList;
    private Activity activity;
    private String[] filepath;
    ImageView img;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView adID, adType, societyName,societyAddress,societyDate;

        public MyViewHolder(View view) {
            super(view);
            adID = (TextView) view.findViewById(R.id.adID);
            img = (ImageView) view.findViewById(R.id.imgCard);

        }
    }

    public SubmittedDataAdapter(List<QueueData> DatasList) {
        this.DatasList = DatasList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.submitted_row, parent, false);

      /*  imgLoader = new ImageLoader(parent.getContext());*/
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QueueData queueData = DatasList.get(position);
        holder.adID.setText(queueData.getAdID());


        try {
            Bitmap bmp = BitmapFactory.decodeFile(queueData.getUrl());
            if (bmp==null)
            {
                java.io.FileNotFoundException e = new java.io.FileNotFoundException();
                throw  e;
            }
            img.setImageBitmap(bmp);
        }
        catch (java.io.FileNotFoundException e)
        {
            img.setImageResource(R.drawable.imagenotfound);
        }

        /*imgLoader.DisplayImage(queueData.getUrl(), img);*/
        //holder.societyDate.setText(Data.getDa());
    }

    @Override
    public int getItemCount() {
        return DatasList.size();
    }
}

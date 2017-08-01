package com.machadalo.audit.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.machadalo.audit.ImageLoader;
import com.machadalo.audit.R;
import com.machadalo.audit.extras.QueueData;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by DELL-PC on 2/8/2016.
 */
public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> {
  ImageLoader imgLoader;
    private List<QueueData> DataList;
    private Activity activity;
    private String[] filepath;
    ImageView img;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView adID, adType, societyName,societyAddress,societyDate;

        public TextView proposalNametxt;
        public TextView societyNametxt;
        public TextView inventoryId;
        public TextView acitivityTyp;
        public TextView societyAddrs;
        public TextView DjangoUploadsign;
        public TextView Amazonsign;
        public TextView inventoryName;



        public MyViewHolder(View view) {
            super(view);
         /*   adID = (TextView) view.findViewById(R.id.adID_Queue);
            img = (ImageView) view.findViewById(R.id.imgCard_Queue);*/
            proposalNametxt = (TextView)view.findViewById(R.id.queueProposalName);
            societyNametxt = (TextView)view.findViewById(R.id.queueSocityName);
            societyAddrs = (TextView)view.findViewById(R.id.queueSocietyaddrs);
            inventoryId = (TextView)view.findViewById(R.id.queueinventoryId);
            acitivityTyp = (TextView)view.findViewById(R.id.queueactivityTyp);
            DjangoUploadsign = (TextView)view.findViewById(R.id.queueDjangoColor);
            Amazonsign = (TextView)view.findViewById(R.id.queueAmzonColor);
            inventoryName = (TextView)view.findViewById(R.id.queueinventoryName);


        }

    }

    public QueueAdapter(List<QueueData> DataList) {
        this.DataList = DataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.queued_row, parent, false);
      /*  imgLoader = new ImageLoader(parent.getContext());*/
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        QueueData queueData = DataList.get(position);
        //holder.proposalNametxt.setText(queueData.get);
        holder.proposalNametxt.setText(queueData.getProposalName());
        holder.societyNametxt.setText(queueData.getSocietyName());
        holder.societyAddrs.setText(queueData.getSocietyAddress());
        holder.inventoryId.setText(queueData.getInventoryId());
        holder.inventoryName.setText(queueData.getInventoryName());
        holder.acitivityTyp.setText(queueData.getActivityType());

       // Log.e("queueadapter",queueData.getSocietyAddress()+" "+queueData.getSocietyName());

        if (queueData.getIsAmazonUploaded().compareTo("True")==0){
            holder.Amazonsign.setBackgroundColor(Color.GREEN);
        }
        else if (queueData.getIsAmazonUploaded().compareTo("False")==0){
            holder.Amazonsign.setBackgroundColor(Color.RED);
        }
        else {
            holder.Amazonsign.setBackgroundColor(Color.WHITE);
        }


        if (queueData.getIsDjangoUploaded().compareTo("True")==0){
            holder.DjangoUploadsign.setBackgroundColor(Color.GREEN);
        }
        else if (queueData.getIsAmazonUploaded().compareTo("False")==0){
            holder.DjangoUploadsign.setBackgroundColor(Color.RED);
        }
        else
            holder.DjangoUploadsign.setBackgroundColor(Color.WHITE);


    }

    @Override
    public int getItemCount() {
        return DataList.size();
    }
}

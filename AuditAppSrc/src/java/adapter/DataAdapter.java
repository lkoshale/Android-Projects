package com.machadalo.audit.adapter;

/**
 * Created by Asna Euphoria on 23-01-2016.
 */


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.machadalo.audit.CallActivity;
import com.machadalo.audit.ImageLoader;
import com.machadalo.audit.R;
import com.machadalo.audit.ShowInventoriesActivity;
import com.machadalo.audit.extras.Data;
import com.machadalo.audit.sqllite.DataBaseHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    ImageLoader imgLoader;
    private List<Data> DatasList;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView adID, adType, societyName,societyAddress,societyDate,adBadge,inventoryType,business_name;
        public TextView campaignName, supplierID, supplierName,supplierAddress1, supplierAddress2,distance;
        ImageView img;
        LinearLayout distanceLayout;
        CardView assignedCard;
        Button viewMap, call;

        public MyViewHolder(View view) {

            super(view);
            campaignName = (TextView) view.findViewById(R.id.campaignName);
            supplierID = (TextView) view.findViewById(R.id.societyID);
            supplierName = (TextView) view.findViewById(R.id.societyName);
            supplierAddress1 = (TextView) view.findViewById(R.id.societyAddress1);
            supplierAddress2 = (TextView) view.findViewById(R.id.societyAddress2);
            img = (ImageView) view.findViewById(R.id.imgCard);
            distance = (TextView)view.findViewById(R.id.distancetext);
            distanceLayout = (LinearLayout)view.findViewById(R.id.assignedDistanceLayout);
            assignedCard = (CardView) view.findViewById(R.id.assigned_cv);
            viewMap = (Button)view.findViewById(R.id.assignedViewMap);
            call = (Button)view.findViewById(R.id.assignedCall);

        }
    }

    public DataAdapter(Context context,List<Data> DatasList) {
        this.DatasList = DatasList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list_row, parent, false);
        imgLoader = new ImageLoader(parent.getContext());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

       final Data Data = DatasList.get(position);
//        holder.adID.setText(Data.getadID()+": ");
//        holder.adType.setText(Data.getAudit_type());
        holder.campaignName.setText(Data.getCampaignName());
        holder.supplierID.setText(Data.getSupplierID());
        holder.supplierName.setText(Data.getSupplierName());
        holder.supplierAddress1.setText(Data.getSupplierAddress1());
        holder.supplierAddress2.setText(Data.getSupplierAddress2());
//        holder.societyDate.setText(Data.getDate());
//        holder.business_name.setText("Business name: "+Data.getBusiness_name());
//        holder.adBadge.setText(Data.getInventoryType());

        imgLoader.DisplayImage(Data.getUrl(), holder.img);
        //holder.societyDate.setText(Data.getDa());
        if (Data.distance != -1) {
            float dis = (float) Data.distance/1000;
            holder.distanceLayout.setVisibility(View.VISIBLE);

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            holder.distance.setText(df.format(dis)+" km from current location");
        }

        holder.assignedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowInventoriesActivity.class);

               // selectedDate = getDateOnGivenDay(spinner.getSelectedItem().toString());

                intent.putExtra("shortlistedSpacesId", Data.getShortlistedSpacesId());
                intent.putExtra("supplierName", Data.getSupplierName());
                intent.putExtra("supplierId", Data.getSupplierID());
                intent.putExtra("supplierLongitude", Data.getLongitude());
                intent.putExtra("supplierLatitude", Data.getLatitude());
                intent.putExtra("supplierAddress", Data.getSupplierAddress1() + " " + Data.getSupplierAddress2());
                intent.putExtra("selectedDate", Data.getSelectedDate());
                //Log.d(TAG, "selected date is before calling inventory activity" + selectedDate);

                Log.d("AssignedFrag", Data.getShortlistedSpacesId());
                context.startActivity(intent);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CallActivity.class);
                intent.putExtra("supplierID",Data.getSupplierId());
                context.startActivity(intent);

               /*
                String number = "+919790756214";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                context.startActivity(intent);*/
            }
        });

        holder.viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uri = Data.getMapUri();

                if (uri != null) {

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return DatasList.size();
    }
}

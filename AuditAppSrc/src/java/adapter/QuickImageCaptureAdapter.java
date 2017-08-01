package com.machadalo.audit.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Messenger;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.DemoCapture;
import com.machadalo.audit.QuickCaptureViewImagesActivity;
import com.machadalo.audit.QuickImageCaptureView;
import com.machadalo.audit.R;
import com.machadalo.audit.Services.QuickImageCaptureBulkUploadAmazonTask;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lokesh on 03-07-2017.
 */




public class QuickImageCaptureAdapter extends RecyclerView.Adapter<QuickImageCaptureAdapter.MyViewHolder> {


    private List<QuickImageCaptureTable> mList;
    public Context context;
    public  HashMap<String,Integer> counts;
    public Drawable dr;
    public Handler handler;



    public QuickImageCaptureAdapter(Context context, List<QuickImageCaptureTable> list, Handler handler){
        this.context = context;
        this.mList = list;
        this.handler = handler;
        dr = context.getResources().getDrawable(R.drawable.custom_button);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quick_image_capture_view_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final QuickImageCaptureTable object = mList.get(position);
        holder.proposalName.setText(object.getProposlaName());
        holder.supplierName.setText(object.getSupplierName());
        counts  = getUploadedImageCount(object.getID());
        holder.inevntoryName.setText(object.getInventoryName());
        holder.activityTyp.setText(object.getAcitvityType());

        int total = counts.get("total");
        int completed = counts.get("uploaded");
        int remaining = counts.get("remaining");


        if (completed < total) {
            holder.Upload.setEnabled(true);
            holder.Upload.setBackgroundColor(Color.RED);
            holder.Upload.setTextColor(Color.WHITE);
        }
        else {

            holder.Upload.setBackgroundColor(Color.GRAY);
        }

        if (total > 0){
            holder.ViewImages.setEnabled(true);
            holder.ViewImages.setBackgroundColor(Color.RED);
            holder.ViewImages.setTextColor(Color.WHITE);
            holder.ViewImages.setVisibility(View.VISIBLE);
        }

        Log.e("QuickView",String.valueOf(remaining));
        holder.ImageCount.setText(String.valueOf(completed)+"/"+String.valueOf(total)+" Images are uploaded successfully");

       holder.Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, QuickImageCaptureBulkUploadAmazonTask.class);
                intent.putExtra("supplierName", object.supplierName);
                intent.putExtra("ID",String.valueOf(object.getID()));
                intent.putExtra("inventoryName",object.getInventoryName());
                intent.putExtra("activityTyp",object.getAcitvityType());

                context.startService(intent);
                holder.Upload.setBackgroundColor(Color.BLACK);

                Toast.makeText(context, "Image Sync started !! ", Toast.LENGTH_SHORT).show();

            }
        });

        holder.ViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuickCaptureViewImagesActivity.class);
                intent.putExtra("supplierName", object.supplierName);
                intent.putExtra("inventoryName",object.getInventoryName());
                intent.putExtra("activityTyp",object.getAcitvityType());
                intent.putExtra("ID",String.valueOf(object.getID()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(context, DemoCapture.class);
                i1.putExtra("supplierName",object.getSupplierName());
                i1.putExtra("mode","CaptureMode");
                i1.putExtra("actualactivity","AssignedAcitivty");
                i1.putExtra("activityType",object.getAcitvityType());
                i1.putExtra("inventoryType",object.getInventoryName());
                i1.putExtra("ID",String.valueOf(object.getID()));

                //handler to reciev msg from service that
                //image as ben uploaded
                i1.putExtra("msg",new Messenger(handler));

                //set flag
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView supplierName , ImageCount , inevntoryName , activityTyp ,proposalName;
        private Button Upload , ViewImages, Capture;

        public MyViewHolder(View itemView) {
            super(itemView);

            proposalName = (TextView)itemView.findViewById(R.id.quickProposalName);
            supplierName = (TextView)itemView.findViewById(R.id.quickListsupplierName);
            inevntoryName =(TextView)itemView.findViewById(R.id.QuickActivityInventoryName);
            activityTyp = (TextView)itemView.findViewById(R.id.QuickActivityActivityName);
            ImageCount = (TextView) itemView.findViewById(R.id.quickListNumber);
            Upload = (Button)itemView.findViewById(R.id.quickListButton);
            ViewImages = (Button)itemView.findViewById(R.id.quickViewImageButton);
            Capture = (Button)itemView.findViewById(R.id.quickCaptureBtn);

            Upload.setEnabled(false);
            Upload.setBackgroundColor(Color.GRAY);
            ViewImages.setEnabled(false);
            ViewImages.setBackgroundColor(Color.GRAY);



        }

    }

    public HashMap<String,Integer> getUploadedImageCount(Long ID){

        HashMap<String,Integer> hashset = new HashMap<>();

//        String Query = "SELECT * FROM "+ QuickImageCaptureTable.TABLE_NAME+" WHERE "+QuickImageCaptureTable.KEY_SUPPLIER_NAME
//                + " = \'"+supplierName+"\' ;";

        String Query  = "SELECT * FROM "+ QuickImageCaptureTable.TABLE_NAME + " LEFT OUTER JOIN "
                    + QuickImageCapturePathTable.TABLE_NAME + " ON "
                    + QuickImageCaptureTable.KEY_ID + " = "+QuickImageCapturePathTable.KEY_QuickImageCaptureID+" ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

//        ID = QuickImageCaptureTable.getID(context,supplierName,inventoryName,activityTyp);

        if(ID == -1){
            Log.e("In QuickImgeCapturetabl","ERROR !!!!!!");
        }

        int totalCount = 0;
        int remainingCount = 0;
        int completedCount = 0;

        while (cursor.moveToNext()){

            long id_1 = cursor.getLong(cursor.getColumnIndex(QuickImageCaptureTable.KEY_ID));
             //Log.e("IN while",String.valueOf(id_1));

            if ( ID == id_1 ) {

                String completed = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_IS_AMAZON_UPLOADED));
               // Log.e("inQuickImage", completed + " ");

                if (completed != null) {
                    totalCount++;
                }

                if (completed != null && completed.compareTo("true") == 0) {
                    completedCount++;
                } else if (completed != null && completed.compareTo("false") == 0) {
                    remainingCount++;
                }

            }

        }

        hashset.put("total",totalCount);
        hashset.put("uploaded",completedCount);
        hashset.put("remaining",remainingCount);

        return hashset;
    }





}

package com.machadalo.audit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.machadalo.audit.fragments.PendingFragment;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.ShortlistedInventoryDetailsTable;

import java.util.List;

/**
 * Created by root on 2/2/17.
 */

public class ShowInventoryAdapter extends RecyclerView.Adapter<ShowInventoryAdapter.ViewHolder> {

    List<ShortlistedInventoryDetailsTable> shortlistedInventoryDetailsData;
    public  ShowInventoriesActivity  activity;
    public String supplierlatitude;
    public String supplierlongitude;
    public String supplierName;
    public String address;
    public String selectedDate;
    public String inventoryType;
    public Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView inventoryType;
        public TextView inventoryId;
        public Spinner validActivities;
        public Button bCapture;
        public Button bViewImages;
        public TextView NumberOfImagesTextView;

        public ViewHolder(View view) {
            super(view);
            inventoryType = (TextView)view.findViewById(R.id.pending_inventoryTYP);
            inventoryId = (TextView)view.findViewById(R.id.pending_inventoryID);
            validActivities = (Spinner) view.findViewById(R.id.pending_invsible_spinner);
            bCapture  = (Button) view.findViewById(R.id.bCapture);
            bViewImages = (Button)view.findViewById(R.id.bViewImages);
            NumberOfImagesTextView = (TextView)view.findViewById(R.id.assignedImagesTaken);

        }
    }

    public ShowInventoryAdapter(Context context, List<ShortlistedInventoryDetailsTable> shortlistedInventoryDetailsData, ShowInventoriesActivity instance, String latitude, String longitude, String supplierName, String address, String selectedDate){

        this.context = context;
        this.shortlistedInventoryDetailsData = shortlistedInventoryDetailsData;
        this.activity = instance;
        this.supplierlatitude = latitude;
        this.supplierlongitude = longitude;
        this.supplierName = supplierName;
        this.address = address;
        this.selectedDate = selectedDate;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //Log.d("CallingOnBindView", Integer.toString(position));

        final String shortlistedInventoryId;

        shortlistedInventoryId = shortlistedInventoryDetailsData.get(position).getShortlistedInventoryDetailsID();

        holder.inventoryType.setText(shortlistedInventoryDetailsData.get(position).getInventoryName());
        //set inventory typ string
        inventoryType = shortlistedInventoryDetailsData.get(position).getInventoryName();

        //Log.d("InventoryName", shortlistedInventoryDetailsData.get(position).getInventoryName());

        holder.inventoryId.setText(shortlistedInventoryDetailsData.get(position).getInventoryId());
        //Log.d("InventoryId", shortlistedInventoryDetailsData.get(position).getInventoryId());

        List<String> validActs = shortlistedInventoryDetailsData.get(position).getValidActivities();
        //Log.d("SizeOfValidActs", Integer.toString(validActs.size()));

        //Log.d("InventoryGlobalId", shortlistedInventoryDetailsData.get(position).getShortlistedInventoryDetailsID());

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, validActs);
        holder.validActivities.setAdapter(adapter);

        holder.NumberOfImagesTextView.setText(String.valueOf(getTotalImagesCount(shortlistedInventoryId))+" Images Taken in Total");

        PendingFragment pf = new PendingFragment();
        String Assigndate = pf.getAssignedDayByInventoryID(shortlistedInventoryDetailsData.get(position).getShortlistedInventoryDetailsID(),(String)holder.validActivities.getSelectedItem());
        String currentDate = Utils.getCurrentDateString();
        //Selected date is currrent date
        Log.e("in Showinventoryadapter","AssignedDate "+Assigndate);
        if (currentDate.compareTo(Assigndate)==0) {
            holder.bCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

               //     Intent demoCaptureIntent = new Intent(activity, DemoCapture.class);
                    //   demoCaptureIntent.putExtra("id", "");

             /*   demoCaptureIntent.putExtra("address", address);
                demoCaptureIntent.putExtra("location", "");
                demoCaptureIntent.putExtra("submitStatus", "");
                demoCaptureIntent.putExtra("inventDate", "");
                demoCaptureIntent.putExtra("adcounter", "");
                demoCaptureIntent.putExtra("actualactivity", "AssignedAcitivty");
                demoCaptureIntent.putExtra("imagepath", "NULL");

                demoCaptureIntent.putExtra("latitude", latitude);
                demoCaptureIntent.putExtra("longitude", longitude);
                demoCaptureIntent.putExtra("supplierName", supplierName);
                demoCaptureIntent.putExtra("activityType", (String)holder.validActivities.getSelectedItem());
                demoCaptureIntent.putExtra("activityDate", selectedDate);
                demoCaptureIntent.putExtra("shortlistedInventoryDetailsId", shortlistedInventoryId);
                demoCaptureIntent.putExtra("inventoryType", holder.inventoryType.getText());
                activity.startActivity(demoCaptureIntent);
                */

                    /*
                    Intent locationFindIntent = new Intent(activity, LocationFind.class);
                    locationFindIntent.putExtra("supplierName", supplierName);
                    locationFindIntent.putExtra("activityType", (String) holder.validActivities.getSelectedItem());
                    locationFindIntent.putExtra("activityDate", selectedDate);
                    locationFindIntent.putExtra("shortlistedInventoryDetailsId", shortlistedInventoryId);
                    locationFindIntent.putExtra("inventoryType", holder.inventoryType.getText());






                    Log.d("ShowInvAdapterDetails", "details");
                    Log.d("SelectedItem", (String) holder.validActivities.getSelectedItem());
                    Log.d("ShortInvId", shortlistedInventoryId);
                    Log.d("suppName", supplierName);

                    activity.startActivity(locationFindIntent);
                    */

                    // location is added in background
                    //so calling directly the camera class

                    Intent demoCaptureIntent = new Intent(activity, DemoCapture.class);
                    demoCaptureIntent.putExtra("id", "");
                    demoCaptureIntent.putExtra("address", address);     //this adress id eifferent from what passing from location
                    demoCaptureIntent.putExtra("location", "");
                    demoCaptureIntent.putExtra("submitStatus", "");
                    demoCaptureIntent.putExtra("inventDate", "");
                    demoCaptureIntent.putExtra("adcounter", "");
                    demoCaptureIntent.putExtra("actualactivity", "AssignedAcitivty");
                    demoCaptureIntent.putExtra("imagepath", "NULL");
//                    demoCaptureIntent.putExtra("latitude", latitude);
//                    demoCaptureIntent.putExtra("longitude", longitude)
                    demoCaptureIntent.putExtra("supplierName", supplierName);
                    demoCaptureIntent.putExtra("activityType",(String) holder.validActivities.getSelectedItem());
                    demoCaptureIntent.putExtra("activityDate", selectedDate);
                    demoCaptureIntent.putExtra("shortlistedInventoryDetailsId",shortlistedInventoryId);
                    demoCaptureIntent.putExtra("comment", " ");   //TODO made comment null as its taken in democaptur class and initailized there
                    demoCaptureIntent.putExtra("inventoryType", inventoryType);
                    activity.startActivity(demoCaptureIntent);
                    //finish();
                }
            });
        }else{
            holder.bCapture.setBackgroundColor(Color.GRAY);
        }

        holder.bViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,ViewImagesActivity.class);
                intent.putExtra("ssid",shortlistedInventoryId);
                intent.putExtra("activityTyp", (String)holder.validActivities.getSelectedItem());

                activity.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return shortlistedInventoryDetailsData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inventoryDetailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list, parent, false);
        return new ViewHolder(inventoryDetailView);
    }


    public int getTotalImagesCount(String ssid){

        int count = 0;

        DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
        SQLiteDatabase db = db_handle.getReadableDatabase();

        String Query = "SELECT * FROM " + InventoryImagePathTable.TABLE_NAME + " WHERE " + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID
                + " = " + "\'" + ssid + "\' ;";

        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()){
            count++;
        }

        return count;
    }

}


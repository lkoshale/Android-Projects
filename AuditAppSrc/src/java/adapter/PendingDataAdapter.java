package com.machadalo.audit.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.DemoCapture;
import com.machadalo.audit.R;
import com.machadalo.audit.ViewImagesActivity;
import com.machadalo.audit.extras.PendingData;
import com.machadalo.audit.fragments.PendingFragment;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.PendingTable;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Lokesh on 14-06-2017.
 */


// Adapter used for recyclerView in the pending Fragment and also in CompletedFragment
// Has teo different modes one for each fragment

public class PendingDataAdapter extends RecyclerView.Adapter<PendingDataAdapter.MyvViewHolder> {

    // List to display in recyclerview
    private List<PendingData> mPendingList;
    public Context context;

    //if this is true then its Completed Fragment mode
    // if false then pending Fragment
    //Modes are set in the constructor of the adapter to determine which fargment is calling it
    public boolean CalledFromCompletedFragment = false;

    // The number of required image so it can transfer to completed Tab
    public static final int REQUIRED_IMAGE_NUM = 1;
    public Handler handler;


    public class MyvViewHolder extends RecyclerView.ViewHolder{

      //  public TextView campaignName,societyID,Societyname,msocietyAddress,count,pendingdays;
        public TextView proposalName,ShortlistedSocietyName,InventoryName,InventoryId,InventoryTyp,pendingDaysCount,pendingImageNumber;
        public Button Capture ,ViewImages;
        public CheckBox move ;



        public MyvViewHolder(View view){
            super(view);
            // initilaize all hoder here find by id

            proposalName = (TextView)view.findViewById(R.id.pending_proposaldTXT);
            ShortlistedSocietyName = (TextView)view.findViewById(R.id.pending_ssIdTxt);
            InventoryId = (TextView)view.findViewById(R.id.pending_inventoryID);
            InventoryName = (TextView)view.findViewById(R.id.pending_inventoryTYP);
            InventoryTyp = (TextView)view.findViewById(R.id.pending_selectedActivity);
            pendingDaysCount = (TextView) view.findViewById(R.id.pending_countTxt);

            pendingImageNumber = (TextView) view.findViewById(R.id.pendingImageNumber);
            ViewImages = (Button)view.findViewById(R.id.pending_view_Images);

            //initailize the button and checkbox
            Capture = (Button)view.findViewById(R.id.pending_capture);
            move = (CheckBox)view.findViewById(R.id.pending_toCompletedCheckBox);

            /*
            campaignName = (TextView)view.findViewById(R.id.campaignName);
            societyID = (TextView)view.findViewById(R.id.societyID);
            Societyname = (TextView)view.findViewById(R.id.societyName);
            msocietyAddress = (TextView)view.findViewById(R.id.societyAddress1);
            count = (TextView)view.findViewById(R.id.societyAddress2);
            pendingdays = (TextView)view.findViewById(R.id.pending_days_textView);
            */
        }

    }

    //Constructor which sets the mode called in completed fragment
    public PendingDataAdapter(Context context,List<PendingData>mList,boolean CompltedFragmentMode){
        this.context = context;
        this.mPendingList = mList;
        this.CalledFromCompletedFragment = CompltedFragmentMode;
    }

    //constructor without mode by default pending fragment-> called from pending fragment
    public PendingDataAdapter(Context context , List<PendingData>mList, Handler handler){
        this.mPendingList = mList;
        this.context = context;
        this.handler = handler;
    }

    //infalte the layout
    public MyvViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_list, parent, false);  //added pending list istead of data_list

        return new MyvViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyvViewHolder holder, int position) {
        //bind the views here

        final PendingData pendingData = mPendingList.get(position);
        holder.proposalName.setText(pendingData.getProposalName());
        holder.ShortlistedSocietyName.setText(pendingData.getSupplierName());
        holder.InventoryId.setText(pendingData.getInventoryID());
        holder.InventoryName.setText(pendingData.getInventoryName());
        holder.InventoryTyp.setText(pendingData.getInventoryTyp());
        holder.pendingImageNumber.setText(String.valueOf(numImagePresent(pendingData.getShortlistedInventoryDetailsId(),pendingData.getInventoryTyp())+" Images Taken"));

        if( numImagePresent(pendingData.getShortlistedInventoryDetailsId(),pendingData.getInventoryTyp()) <= 0 ){
            holder.ViewImages.setEnabled(false);
            holder.ViewImages.setBackgroundColor(Color.GRAY);
        }
        else{
            holder.ViewImages.setEnabled(true);
            holder.ViewImages.setBackgroundColor(Color.RED);
        }


        //OnClickListner for the capture button
        // starts the activity Democapture
        holder.Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent demoCaptureIntent = new Intent(context, DemoCapture.class);
                demoCaptureIntent.putExtra("id", "");
                demoCaptureIntent.putExtra("address", pendingData.getSupplierAddress());     //this adress id eifferent from what passing from location
                demoCaptureIntent.putExtra("location", "");
                demoCaptureIntent.putExtra("submitStatus", "");
                demoCaptureIntent.putExtra("inventDate", "");
                demoCaptureIntent.putExtra("adcounter", "");
                demoCaptureIntent.putExtra("actualactivity", "AssignedAcitivty");
                demoCaptureIntent.putExtra("imagepath", "NULL");
//                demoCaptureIntent.putExtra("slatitude",pendingData.getSupplierLat());
//                demoCaptureIntent.putExtra("slongitude",pendingData.getSupplierLon());
                demoCaptureIntent.putExtra("supplierName", pendingData.getSupplierName());
                demoCaptureIntent.putExtra("activityType", pendingData.getInventoryTyp());
                demoCaptureIntent.putExtra("activityDate", pendingData.getInventoryAssignedDate());
                demoCaptureIntent.putExtra("shortlistedInventoryDetailsId", pendingData.getShortlistedInventoryDetailsId());
                demoCaptureIntent.putExtra("comment", " ");   //TODO made comment null as its taken in democaptur class and initailized there
                demoCaptureIntent.putExtra("inventoryType", pendingData.getInventoryName());

                demoCaptureIntent.putExtra("msg",new Messenger(handler));

                context.startActivity(demoCaptureIntent);
            }
        });

        holder.ViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewImagesActivity.class);
                intent.putExtra("ssid", pendingData.getShortlistedInventoryDetailsId());
                intent.putExtra("activityTyp",pendingData.getInventoryTyp());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        });


        //MODE : if pending Frag mode the set the cehck box and pendingdays Count
        if (CalledFromCompletedFragment == false) {

            holder.pendingDaysCount.setText("Pending from : " + pendingData.getPendingDaysCount() + " days");

            holder.move.setChecked(false);

            //Listener in the checkbox makes isCompleted Feild true of pending table
            holder.move.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    Log.e("InCheckBox", String.valueOf(isChecked));

                    //if checked
                    if (isChecked) {

                        //if number of images taken is greater then or equal to the required image
                        if ( numImagePresent(pendingData.getShortlistedInventoryDetailsId(), pendingData.getInventoryTyp())>= REQUIRED_IMAGE_NUM ) {

                            // Update the PendingTable make isCompleted true
                            DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
                            SQLiteDatabase db = db_handle.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put(PendingTable.KEY_isCompleted, "true");

                            db.update(PendingTable.TABLE_NAME, values, PendingTable.KEY_ShortlistedInventoryID + " = \'" + pendingData.getShortlistedInventoryDetailsId() + "\'" + " AND " + PendingTable.KEY_ACTIVTY_TYP + " = \'" + pendingData.getInventoryTyp() + "\'", null);

                        } else {
                            //else not enough images taken
                            // mark it unchecked and show user a toast
                            Log.e("Checkboxclick", "no photo");
                            holder.move.setChecked(false);
                            Toast.makeText(context, "No photo takken for this inventory", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        //TODO listener for unchecking the checkbox


//                        DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
//                        SQLiteDatabase db = db_handle.getWritableDatabase();
//
//                        ContentValues values = new ContentValues();
//                        values.put(PendingTable.KEY_isCompleted, "false");
//
//                        //db.update(CoordinateAddressMapTable.TABLE_NAME,contentValues,CoordinateAddressMapTable.KEY_LAT+ " = "+"\'"+Double.toString(lat)+"\'",null);
//
//                        db.update(PendingTable.TABLE_NAME, values, PendingTable.KEY_ShortlistedInventoryID + " = \'" + pendingData.getShortlistedInventoryDetailsId() + "\'" + " AND " + PendingTable.KEY_ACTIVTY_TYP + " = \'" + pendingData.getInventoryTyp() + "\'", null);
//                        //Log.e()
//                        //db_handle.printTotalRows(PendingTable.TABLE_NAME);
                    }

                }
            });


        }else {
            //Completed mode

            //mek the checkbox and pending Count INVISIBLE from layout
            holder.pendingDaysCount.setVisibility(View.INVISIBLE);
            holder.move.setVisibility(View.INVISIBLE);

        }

        /*
        PendingData pendingData = mPendingList.get(position);
        holder.campaignName.setText(pendingData.getProposalID());
        holder.societyID.setText(pendingData.getSupplierID());
        holder.Societyname.setText(pendingData.getSupplierName());
        holder.msocietyAddress.setText(pendingData.getSupplierAddress());
        holder.count.setTextColor(Color.BLACK);
        holder.count.setText("Total Pending Inventory Count : "+pendingData.getItemPendingCount());
        holder.pendingdays.setVisibility(View.VISIBLE);
        holder.pendingdays.setTextColor(Color.RED);
        holder.pendingdays.setText("Pending From : "+pendingData.getPendingDaysCount()+" Days");
        Log.v("pendingDays",pendingData.getPendingDaysCount());
        */



    }

    @Override
    public int getItemCount() {
        return mPendingList.size();
    }


    // return number of images taken for the given shortlistedinventoryTableId and activty type
    // queries from the inventoryImagePath table
    public int numImagePresent(String ssid , String activityTyp) {

        // entry is made in ImageInventoryPath table as the image is clicked and
        // submitted button is pressed

        //query for the Data
        DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
        SQLiteDatabase db = db_handle.getReadableDatabase();

        String Query = "SELECT * FROM " + InventoryImagePathTable.TABLE_NAME + " WHERE " + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID
                + " = " + "\'" + ssid + "\' ;";

        Cursor cursor = db.rawQuery(Query, null);

        // count of images taken
        int count = 0;

        // iterate over all same ssid's
        while (cursor.moveToNext()) {
            // check the activity type
            String checkActivityTyp = cursor.getString(cursor.getColumnIndex(InventoryImagePathTable.KEY_INVENTORY_ACTIVITY_TYPE));
           //if it matches increment the count
            if (checkActivityTyp.compareTo(activityTyp) == 0) {
                count++;
            }

        }


        return count;

    }


}// Adpter class ENDS


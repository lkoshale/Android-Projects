package com.machadalo.audit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.Services.QuickImageCaptureBulkUploadAmazonTask;
import com.machadalo.audit.adapter.QuickCaptureImageViewAdapter;
import com.machadalo.audit.adapter.QuickImageCaptureAdapter;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
*  activity for taking images by entering data manually
*
*  the recyclerview cards are populated as the user inserts data by clciking add Floating button
*  each of it s entered in QuickImageCaptureTable
*
* the recyclerview is populated from that table
 */

public class QuickImageCaptureView extends AppCompatActivity{

    private RecyclerView recyclerView;
    private QuickImageCaptureAdapter mAdapter;
    private List<QuickImageCaptureTable> mList = new ArrayList<>();
    public FloatingActionButton fab;
    public TextView AddNewItem;
    public Handler handler;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_image_capture_view);

        context = getApplicationContext();

        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);

        //Handler is for reciving messages from the Democapture Activity
        // and gets msg if the image is uploaded successfully
        // so re-render the recylerView after uploading finished in background

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                String replyString = reply.getString("msg");
                String uploaded = reply.getString("Uploaded");
                //Log.e("Inside QUICKHANDLER",replyString);

                mAdapter.notifyDataSetChanged();
            }
        };


        AddNewItem = (TextView)findViewById(R.id.QuickEmptyTextView);
        recyclerView = (RecyclerView)findViewById(R.id.QuickActivityRecyclerView);
        mAdapter = new QuickImageCaptureAdapter(getApplicationContext(),mList ,handler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                QuickImageCaptureTable object = mList.get(position);
//                Intent intent = new Intent(QuickImageCaptureView.this, QuickCaptureViewImagesActivity.class);
//
//                intent.putExtra("supplierName",object.getSupplierName());
//                startActivity(intent);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        //to add more supplier and inventory to take images
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getApplicationContext(), AlertDialogue.class);
                startActivity(i1);
            }
        });

        //populates the data from table in list for recyclerview
        prepareData();

        //show the msg how to add society if its null
        if(mList.size() == 0){
            AddNewItem.setVisibility(View.VISIBLE);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        //prepare list
        prepareData();
        if(mList.size() == 0){
            AddNewItem.setVisibility(View.VISIBLE);
        }
    }


    //populates the list from the Table QuickImageCapture
    public void prepareData(){

        mList.clear();
        mAdapter.notifyDataSetChanged();

        String Query = "SELECT * FROM "+ QuickImageCaptureTable.TABLE_NAME+ " ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){

            Long id = cursor.getLong(cursor.getColumnIndex(QuickImageCaptureTable.KEY_ID));
            String proposalName = cursor.getString(cursor.getColumnIndex(QuickImageCaptureTable.KEY_PROPOSAL_NAME));
            String supplierName = cursor.getString(cursor.getColumnIndex(QuickImageCaptureTable.KEY_SUPPLIER_NAME));
            String inventoryName = cursor.getString(cursor.getColumnIndex(QuickImageCaptureTable.KEY_INVENTORY_NAME));
            String activityTyp = cursor.getString(cursor.getColumnIndex(QuickImageCaptureTable.KEY_ACTIVITY_TYPE));

            QuickImageCaptureTable object = new QuickImageCaptureTable(proposalName,supplierName,inventoryName,activityTyp,id);
            mList.add(object);

        }
        cursor.close();
         mAdapter.notifyDataSetChanged();
    }



   /* public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }



    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private QuickImageCaptureView.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final QuickImageCaptureView.ClickListener clickListener){

            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }

            });

        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
*/

}//end acitivity

package com.machadalo.audit;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.machadalo.audit.adapter.QuickCaptureImageViewAdapter;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
* activity for the view images button in QuickCapture acitvity
*  shows all the images taken for the card in list view
*
 */

public class QuickCaptureViewImagesActivity extends AppCompatActivity {

    //required data
    public String  supplierName;
    private RecyclerView recyclerView;


    //there are two different tables for Quickcapture ID is a foriegnKey in the
    //QuickImagePathTable
    //Id from the quickCaptureImagePath table use to store path of images
    //taken by quick capture
    public String ID;
    private QuickCaptureImageViewAdapter mAdapter;
    public List<Bitmap> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_capture_view_images);

        supplierName = getIntent().getStringExtra("supplierName");
        ID = getIntent().getStringExtra("ID");

        //set recyclerView and adpters for it
        recyclerView = (RecyclerView)findViewById(R.id.QuickImageRecycler_view);
        mAdapter = new QuickCaptureImageViewAdapter(getApplicationContext(),mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //populate the recyclerview data from table
        prepareDataForList();

    }

    public void prepareDataForList(){

        //clear all elements(pics)
        mList.clear();
        mAdapter.notifyDataSetChanged();

        //Query from the table based on foreignKey Id from QuickImageCaptureTable in QuickImageCapturePathTable
        String Query = "SELECT * FROM "+ QuickImageCapturePathTable.TABLE_NAME+" WHERE "+ QuickImageCapturePathTable.KEY_QuickImageCaptureID
                + " = \'"+ID+"\' ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()) {

            String localPath = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_LOCAL_PATH));
            String imageName = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_IMAGE_NAME));

            Uri imageUri = Uri.fromFile(new File(localPath));
           // Log.e("QuickImageView",imageUri.toString() + "   "+imageName);

            ContentResolver cr = getContentResolver();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            mList.add(bitmap);
        }

        cursor.close();
        mAdapter.notifyDataSetChanged();

    }



}//end acitivity

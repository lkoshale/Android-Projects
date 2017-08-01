package com.machadalo.audit;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.machadalo.audit.adapter.ViewImagesActivityAdapter;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  opens images on demand from the assigned fragment
 */

public class ViewImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Bitmap>mList = new ArrayList<>();
    private ViewImagesActivityAdapter mAdapter;
    String ssid ;
    String activityTyp;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        Intent intent = getIntent();

        ssid = intent.getStringExtra("ssid");
        activityTyp = intent.getStringExtra("activityTyp");


        recyclerView = (RecyclerView)findViewById(R.id.ViewImagesRecyclerView);
        mAdapter = new ViewImagesActivityAdapter(this,mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
    }

    void prepareData(){

        mList.clear();
        mAdapter.notifyDataSetChanged();

        DataBaseHandler db_handle = DataBaseHandler.getInstance(this);
        SQLiteDatabase db = db_handle.getReadableDatabase();

        String Query = "SELECT * FROM " + InventoryImagePathTable.TABLE_NAME + " WHERE " + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID
                + " = " + "\'" + ssid + "\' ;";

        Cursor cursor = db.rawQuery(Query, null);

        // count of images taken

        // iterate over all same ssid's
        while (cursor.moveToNext()) {
            // check the activity type
            String checkActivityTyp = cursor.getString(cursor.getColumnIndex(InventoryImagePathTable.KEY_INVENTORY_ACTIVITY_TYPE));
            //if it matches increment the count
            if (checkActivityTyp.compareTo(activityTyp) == 0) {

                Log.e("inCheckrequiredImage ", checkActivityTyp);

                String localPath = cursor.getString(cursor.getColumnIndex(InventoryImagePathTable.KEY_LOCAL_IMAGE_PATH));
                String imageName = cursor.getString(cursor.getColumnIndex(InventoryImagePathTable.KEY_IMAGE_PATH));

                Uri imageUri = Uri.fromFile(new File(localPath));
                Log.e("QuickImageView",imageUri.toString() + "   "+imageName);

                ContentResolver cr = getContentResolver();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                mList.add(bitmap);

                count++;
            }

        }

        if (mList.size() == 0){
            Toast.makeText(getApplicationContext(),"No Images Found",Toast.LENGTH_SHORT).show();
            finish();
        }

        mAdapter.notifyDataSetChanged();

    }


}

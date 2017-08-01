package com.machadalo.audit;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/*
* Activity that shows images on demand from the queued
*  fragment by ontouch in its recycler view
 */

public class QueueImageViewActivity extends AppCompatActivity {


    //each card has atmost 1 image so only one path
    //path of the image
    public String Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_image_view);

        //get data
        Path = getIntent().getStringExtra("path");
        ImageView imageView = (ImageView) findViewById(R.id.queueImageView);

        Uri imageUri = Uri.fromFile(new File(Path));

        //open image as bitmap from storage
        ContentResolver cr = getContentResolver();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        //set view
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }

    }
}//end activity

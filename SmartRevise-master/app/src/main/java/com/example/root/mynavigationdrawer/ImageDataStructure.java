package com.example.root.mynavigationdrawer;

import android.graphics.Bitmap;

/**
 * Created by root on 31/12/15.
 */
public class ImageDataStructure {

    public String id;
    public Bitmap img;
    public String description ;
    public String path;
    public byte[] img_byte_array;

    public ImageDataStructure(  ) {

    }

    public Bitmap getImage()  {

        return img;


    }


    public String getDescription()  {

        return description ;


    }

    public  String getId() {

        return  id;

    }


    public  String getPath() {

        return  path;

    }


    public  byte[] getByteArray() {

        return  img_byte_array;

    }


}

package com.example.root.mynavigationdrawer;

import android.provider.BaseColumns;

/**
 * Created by root on 28/12/15.
 */
public class ImageContract {

    public ImageContract() { }

    public static abstract class ImageDetails implements BaseColumns {

        public static final String TABLE_NAME = "ImagesDetails";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String KEY_NAME = "image_name";
        public static final String KEY_IMAGE = "image_data";


    }


}

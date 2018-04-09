package com.example.root.mynavigationdrawer;

import android.provider.BaseColumns;

/**
 * Created by root on 5/1/16.
 */
public class ImageTag {

    public static abstract class ImageDetails implements BaseColumns {

        public static final String TABLE_NAME = "ImageTag";
        public static final String IMAGE_ID = "image_id";
        public static final String TagName = "tag_name";

    }

}


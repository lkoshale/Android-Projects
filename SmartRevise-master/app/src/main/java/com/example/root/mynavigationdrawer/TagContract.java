package com.example.root.mynavigationdrawer;

import android.provider.BaseColumns;

/**
 * Created by root on 5/1/16.
 */
public class TagContract {

    public TagContract() { }
    public static abstract class ImageDetails implements BaseColumns {

        public static final String TABLE_NAME = "Tag";
        public static final String TAG_NAME = "tag_name";

    }

}

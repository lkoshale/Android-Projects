package com.example.root.mynavigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by root on 10/1/16.
 */
public class CommonMethods {

    public Context context;


   public CommonMethods( Context contex) {

       this.context = contex;

    }

    public void show_message( CharSequence text) {

        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText( context , text, duration);
        toast.show();

    }





}

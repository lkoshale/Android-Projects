package com.machadalo.audit;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 7/11/2015.
 */
public class manageScreens {
    public static void saveToPrefrence(Context context, String[] preferenceName, String[]preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_logged_in", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName[0], preferenceValue[0]);
        editor.putString(preferenceName[1], preferenceValue[1]);
        editor.putString(preferenceName[2], preferenceValue[2]);
        editor.putString(preferenceName[3], preferenceValue[3]);
        editor.apply();
    }

    public static String readFromPrefrence(Context context, String prefrenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_logged_in", Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefrenceName, defaultValue);
    }
}

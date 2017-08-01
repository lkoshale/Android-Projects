package com.machadalo.audit;

/**
 * Created by nikhil on 7/10/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {

    private static Context mContext;

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static  String getCurrentDateString(){

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    }

    public static SimpleDateFormat getCurrentDate() {

        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static  String getTimeStamp(){

        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }

    public static StringBuffer ConvertInputStreamToStringBuffer( InputStream stream ) {
        try {
            StringBuffer chain = new StringBuffer("");
            BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chain.append(line);
            }
            return chain;
        }
        catch (Exception e){
            Log.d("ConInpStrToStrBuff", e.getMessage());
        }
        return null;
    }

    public static void displayToastMessage(Context context, String message, int toastMessageType) {
        Toast.makeText(context, message , toastMessageType).show();
    }



    public static void printOnScreenNoLine( String message ) {
        System.out.print(message);
    }

    public static void printOnScreenNewLine( String message ) {
        System.out.println(message);
    }

    public static String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public static String addDays(int days)
    {
        // adds days to cuurent date and returns result in String
        SimpleDateFormat currentDate = Utils.getCurrentDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return currentDate.format(cal.getTime());

    }


    public static File makeZipfile( String proposalName, Context context ){

        DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance(context);
        SQLiteDatabase db = dataBaseHandler.getReadableDatabase();

        String Query = " SELECT * FROM "+ QuickImageCapturePathTable.TABLE_NAME + " ;";

        Cursor cursor = db.rawQuery(Query,null);

        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> detail = new ArrayList<>();

        int i = 0;
        while (cursor.moveToNext()){
            String localPath = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_LOCAL_PATH));

            String id = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_QuickImageCaptureID));

            if (id != null && localPath != null){

                DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
                SQLiteDatabase db1 = db_handle.getReadableDatabase();

                String DetailQuery = " SELECT * FROM "+ QuickImageCaptureTable.TABLE_NAME+ " WHERE "
                        +QuickImageCaptureTable.KEY_ID +" = "+id+" ;";
               // Log.e("in CpaFrag",DetailQuery);

                Cursor cursor1 = db1.rawQuery(DetailQuery,null);

                String path = null;
                String proposalNameString,suppliername,inventoryTyp,activityTyp;
                boolean flag = false;

                while (cursor1.moveToNext()){
                    proposalNameString = cursor1.getString(cursor1.getColumnIndex(QuickImageCaptureTable.KEY_PROPOSAL_NAME));

                    if (proposalNameString.compareTo(proposalName) != 0) {
                        flag = true;
                        continue;
                    }

                    suppliername = cursor1.getString(cursor1.getColumnIndex(QuickImageCaptureTable.KEY_SUPPLIER_NAME));
                    inventoryTyp = cursor1.getString(cursor1.getColumnIndex(QuickImageCaptureTable.KEY_INVENTORY_NAME));
                    activityTyp = cursor1.getString(cursor1.getColumnIndex(QuickImageCaptureTable.KEY_ACTIVITY_TYPE));

                    path = proposalNameString+"/"+suppliername+"/"+inventoryTyp+"/"+activityTyp+"/";
                }

                if (flag== true)
                    continue;

                images.add(localPath);
                if (path!=null ){
                    detail.add(path);
                }
                else {
                    detail.add("images/");
                }

                //Log.e("image paths", localPath+" "+path);
                i++;

            }

        }

        File file = new File(Environment.getExternalStorageDirectory() + File.separator +"mail.zip");

        try {

            file.createNewFile();
           // Log.e("CaptureFrag","new File created");

            if (images.size() <= 0) {

                Toast.makeText(context,"Proposal Name Invalid OR No images have been Taken Yet !!!",Toast.LENGTH_LONG).show();
                return null;

            }

            zip(images,file,detail);

        }catch (IOException e){
            e.printStackTrace();
            Log.e("Utils Mail sending ","Error creating file");
            return null;
        }


        return file;

    }

    public static void zip(ArrayList<String> _files, File zipFileName, ArrayList<String> path) {

        int BUFFER = 2048;
        //Log.e("CapFrag length of file",String.valueOf(_files.size()));

        try {

            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
               // Log.e("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(path.get(i)+_files.get(i).substring(_files.get(i).lastIndexOf("/")+1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}



package com.machadalo.audit.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.AlertDialogue;
import com.machadalo.audit.AuditorCapture;
import com.machadalo.audit.BarcodeScan;
import com.machadalo.audit.DemoBarcode;
import com.machadalo.audit.DemoCapture;
import com.machadalo.audit.IntentIntegrator;
import com.machadalo.audit.IntentResult;
import com.machadalo.audit.QuickImageCaptureView;
import com.machadalo.audit.R;
import com.machadalo.audit.Utils;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Capturefragment extends Fragment {
    /*Main Variable from Assighned*/

    private String url = "http://android.infiniteloopsinc.com/audit/media/assigned.php"; // our Url to server
    final String uploadFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));

    /*Main Variable from Assighned*/
    private View myFragmentView;
    private Button btncapture;

    public Capturefragment() {
        // Required empty public constructor
    }

    TextView txtbarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = null;
        myFragmentView = inflater.inflate(R.layout.capture_fragment, container, false);
//        btncapture = (Button) myFragmentView.findViewById(R.id.btnCapture);
//        btncapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i1 = new Intent(getContext(), DemoCapture.class);
//                i1.putExtra("id", "");
//                i1.putExtra("address", "");
//                i1.putExtra("location", "");
//                i1.putExtra("submitStatus", "");
//                i1.putExtra("inventDate", "");
//                i1.putExtra("adcounter", "");
//                i1.putExtra("actualactivity", "AssignedAcitivty");
//                i1.putExtra("imagepath", "NULL");
//                startActivity(i1);
//
//
//            }
//        });
        txtbarcode = (TextView) myFragmentView.findViewById(R.id.brcodeResult);

        Button btnBarcode = (Button) myFragmentView.findViewById(R.id.btnBarcode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getContext(), BarcodeScan.class);
                startActivity(i1);
            }
        });
        Button btnName = (Button) myFragmentView.findViewById(R.id.btnSetName);
        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getContext(), AlertDialogue.class);
                startActivity(i1);
            }
            });

        //TODO set intent here
        Button btnView = (Button)myFragmentView.findViewById(R.id.btnViewImages);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuickImageCaptureView.class);
                startActivity(intent);
            }

        });



        // Inflate the layout for this fr6agment
        return myFragmentView;
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


   /* public File makeZipfile( String proposalName ){

        DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance(getContext());
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

                DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
                SQLiteDatabase db1 = db_handle.getReadableDatabase();

                String DetailQuery = " SELECT * FROM "+QuickImageCaptureTable.TABLE_NAME+ " WHERE "
                        +QuickImageCaptureTable.KEY_ID +" = "+id+" ;";
                Log.e("in CpaFrag",DetailQuery);

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

                    Log.e("image paths", localPath+" "+path);
                    i++;

            }

        }

        File file = new File(Environment.getExternalStorageDirectory() + File.separator +"send.zip");

        try {

            file.createNewFile();
            Log.e("CaptureFrag","new File created");

            if (images.size() <= 0) {

                Toast.makeText(context,"Proposal Name Invalid OR No images have been Taken Yet !!!",Toast.LENGTH_LONG).show();
                return null;

            }

            Utils.zip(images,file,detail);

        }catch (IOException e){
            e.printStackTrace();
            Log.e("CapturefRag","Error creating file");
            return null;
        }


        return file;

    }*/



}

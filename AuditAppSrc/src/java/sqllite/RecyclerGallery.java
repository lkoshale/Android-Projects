package com.machadalo.audit.sqllite;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.machadalo.audit.R;
import com.machadalo.audit.adapter.GalleryAdapter;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.extras.GalleryData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerGallery extends AppCompatActivity {
    private String jsonResult;
    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();
    String getIntActivity;
    private String url = "http://android.infiniteloopsinc.com/audit/media/submitted.php"; // our Url to server


    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    String strAdCount;
    int getIntentAct;
    /*Main Variable from Assighned*/
    private View myFragmentView;
    private RecyclerView recyclerView;
    private GalleryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<GalleryData> dataList = new ArrayList<>();



    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_gallery);
        // use this setting to improve performance if you know that changes
// Check for SD Card


        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new GalleryAdapter(dataList);
        /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);*/
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "machadalo");
            // Create a new folder if no folder named SDImageTutorial exist
            file.mkdirs();


        }

        if (file.isDirectory()) {
            listFile = file.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
                prepareDataData(FilePathStrings[i], FileNameStrings[i]);

            }
        }

    }
    /*Frame Work*/
    private void prepareDataData(String filePath, String fileName) {
        GalleryData gallerydata = new GalleryData(filePath, fileName);
        dataList.add(gallerydata);
        mAdapter.notifyDataSetChanged();
    }
}

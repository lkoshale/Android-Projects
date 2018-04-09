package com.example.root.mynavigationdrawer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class FromMainActivityToTopicInformation extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int ADD_DESCRIPTION_ACTIVITY_REQUEST_CODE = 101;

    private static  Uri fileUri;
    private static byte [] img;
    static String title = "" ;
    static String id ;
    String description = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_main_activity_to_topic_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if ( savedInstanceState !=  null ) {

            title = savedInstanceState.getString("title") ;
            setTitle( title);
            print( "Recreating the Acitivity...setting title again") ;

        }

        else {


            try {

                if ( getIntent().getExtras() != null ) {

                    title = getIntent().getExtras().getString("title");
                    id = getIntent().getExtras().getString("id") ; //
                    setTitle(title);
                    print("Creating Activity first time...setting title");



                }


            } catch (Exception e) {

                System.out.println("errotr in MainActivity to TopicInfo(): " + e.getMessage().toString());

            }


        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        try {
            print ( "onStart() called....") ;
//            setTitle(title + " Dashboard"); // setting title of the new Activity . title is fetched from previous Activity
  //          print("Setting title in  onStart() of FromMainActivityToTopicInforamtion()") ;

        } catch (Exception e) {
            print( "error in setting title() : " + e.getMessage().toString() ) ;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

        try {

           // title = getIntent().getExtras().getString("title") ;
     //       setTitle(title + " Dashboard"); // setting title of the new Activity . title is fetched from previous Activity
            img = BitMapUtility.getBytes(BitmapFactory.decodeFile(fileUri.getPath()));
            print ( "FromMainActivityToTopicInformation(): Calling onResume()..");
    //        print("Setting title in  onResume() of FromMainActivityToTopicInforamtion()") ;

        } catch (Exception e) {
            print( "error in setting title() : " + e.getMessage().toString() ) ;
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        print("FromMainActivityToTopicInformation(): onPause() called....") ;
        // save the image to database .

        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        print ( "FromMainActivityToTopicInformation(): onStop() called.... ") ;
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        print ( "FromMainActivityToTopicInformation(): onDestroy() called...") ;
        // The activity is about to be destroyed.
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        print("FromMainActivityToTopicInformation(): onRestart() called...") ;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // first onPause() is called , then onSaveInstanceState() called.
        super.onSaveInstanceState(outState);
        outState.putString("title", title); // save the title
        outState.putString("category_id", id);

        print("FromMainActivityToTopicInformation(): onSaveInstanceState() called...leaving this Activity..saving data") ;

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // This never gets Called . i don't know when it's get called. one case is screen orientation change. i don't know any other case .

        title = savedInstanceState.getString("title") ;
        id = savedInstanceState.getString("category_id");
        setTitle(title);
        print("FromMainActivityToTopicInformation(): Recreating the Acitivity...setting title again") ;
        print("FromMainActivityToTopicInformation(): setting category id ") ;


    }





    public void launchCamera( View view) {

        // create Intent to take a picture and return control to the calling application
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        print("Image storing at" + fileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        // start the image capture Intent
        ;
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
*/
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("category_id", id);
        startActivity( intent); // launch camera Activty

    }


    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SmartRevise");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

                //print("MyCameraApp failed to create directory");
                return null;

            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                //Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                // Bitmap takenImage = BitmapFactory.decodeFile(fileUri.getPath()); // This will crash if image is not loaded in main memnory properly. will look after it.
                // Load the taken image into a preview

                // ivPreview.setImageBitmap(takenImage);

                //---------------------

                if (fileUri != null) {

                    DatabaseHelper image_helper = new DatabaseHelper(this);
                    if (image_helper != null && fileUri != null) {

                        print("About to insert image ");
                        image_helper.addImage(image_helper, fileUri.getPath().toString(), img, description, id);
                        //             Intent intent = new Intent(this, FromTopicInformationToAddDescriptionForImage.class);
                        //            intent.putExtra("title", "Add Description");

                        //          startActivityForResult(intent, ADD_DESCRIPTION_ACTIVITY_REQUEST_CODE); // using startActivtyForResult because i need to send Description Data back to  this Activity

                    } else {
                        print("fileuri is null");
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                    print("user cancelled the image capture");

                }

            } else {

                print("Image capture failed");

            }

        }

    }

    public void print ( String str ) {

        System.out.println(str) ;
    }


    public void launchRevisionImages( View view) {

        // create Intent to take a picture and return control to the calling application
        print ("launchRevision called");
        Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
        intent.putExtra( "category_id", id) ;
        print ( "category id in launcRevisionImages= " + id) ;
        startActivity( intent);

    }

} // onActivity





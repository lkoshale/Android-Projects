package com.example.root.mynavigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;


public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private  Vector<String> Images = new Vector<String>() ;
    private  String path;
    private String category_id;
   // private CameraPreview mPreview;
    private RelativeLayout mLayout;
    private ACameraPreview mPreview;
    private Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //mLayout = new RelativeLayout(this); // get a Layout
        mLayout =  (RelativeLayout)findViewById(R.id.camera_preview);
        //setContentView(mLayout); // set the ContentView to this layout
        mCamera = getCameraInstance();
        //mPreview = new CameraPreview(this, mCamera ) ;
        if ( mCamera == null) { //

            CommonMethods cm = new CommonMethods( this) ;
            cm.show_message("onCreate(): Cannot obtain camera instance. Aborted");
            finish();

        }

        //mPreview = new CameraPreview(this, 0, CameraPreview.LayoutMode.FitToParent, mCamera); // initialize CameraPreview instance
         mPreview = new  ACameraPreview( this, mCamera);
        if ( mPreview == null ) {

            System.out.println ( "mPreview is null ") ;
        }

        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayout.addView(mPreview, 0, previewLayoutParams); // add the Preview Class to this layout


        ///FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        //preview.addView(mPreview);

        //setFocus(mCamera); // sets the focus

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera

                        try {
                            mCamera.takePicture(null, null, mPicture);
                        } catch (Exception e) {
                            System.out.println ( "Error  in taking Picture  : " + e.getMessage().toString() ) ;
                        }
                    }
                }
        );

        category_id = getIntent().getExtras().getString("category_id");
        Button saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        System.out.println("CameraActivity(): Save button clicked. Printing Paths  ");
                        printPath();
                        //           DatabaseHelper helper = new DatabaseHelper(CameraActivity.this);
                        //         helper.addBulkImage( helper, Images, "",  category_id);
                        CommonMethods cm = new CommonMethods(CameraActivity.this);
                        cm.show_message("Your Images Have been saved Successfully. you can  add Tags and Description by tapping on each of them  later.");
                        finish();

                    }
                }
        );


    }

    private void setFocus(  Camera mCamera ) {

        Camera.Parameters params = mCamera.getParameters();
// set the focus mode
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
// set Camera parameters
        mCamera.setParameters(params);

    }


    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }



    private boolean checkCameraHardware(Context context) {

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }



    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = FromMainActivityToTopicInformation.getOutputMediaFile(FromMainActivityToTopicInformation.MEDIA_TYPE_IMAGE);

            if (pictureFile == null){

                Log.d("Error", "Error creating media file, check storage permissions: ");
                return;
            }

            try {

                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                Log.d("Message", "File saved") ;
                System.out.println("File is saved..") ;
                fos.close();
                path = pictureFile.getPath().toString();
                parameters = mCamera.getParameters();
                System.out.println ( "\n CameraActivity() : "  + "Image w = " + parameters.getPictureSize().width + " h=" + parameters.getPictureSize().height ) ;

             //   restartActivity(); // This does not calles onSaveInstance state and onRestoreInstance state .
                recreate(); // recreate this acticity to click again. supported API11+. this method calles onSaveInstanceState and onRestoreInstanceState

            } catch (FileNotFoundException e) {
                //Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                   // Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    public  void restartActivity( ) {

        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // first onPause() is called , then onSaveInstanceState() called.
        super.onSaveInstanceState(outState);
        //outState.putString("title", title); // save the title
       // outState.putString("category_id", id);
        outState.putString("image", path);

        System.out.println("CameraActivity(): calling onSaveInstanceState called..") ;

     //   print("onSaveInstanceState() called...leaving this Activity..saving data") ;

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // This never gets Called . i don't know when it's get called. one case is screen orientation change. i don't know any other case .

/*
        title = savedInstanceState.getString("title") ;
        id = savedInstanceState.getString("category_id");
        setTitle(title);
        print("Recreating the Acitivity...setting title again") ;
        print("setting category id ") ;

*/
        String p = savedInstanceState.getString("image") ;

        DatabaseHelper helper = new DatabaseHelper(CameraActivity.this);
        helper.addImage(helper, p, null, "", category_id);
        //Images.addElement(p);
        System.out.println ( "CameraActivity(): saved the image") ;
        System.out.println("CameraActivity(): calling onRestoreInstanceState called..") ;

    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
        System.out.println("CameraActivity(): onStop() called ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        //releaseCamera(); // releasing camera
        System.out.println ( "CameraActivity(): onPause() called") ;
        mPreview.stop();
        mLayout.removeView(mPreview); // This is necessary.
        mPreview = null;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("CameraActivity(): onDestroy called") ;

    }

    @Override
    protected void onResume() {

        super.onResume();
        System.out.println("CameraActivity(): onResume() called") ;
/*
        mCamera = getCameraInstance();
        if ( mCamera == null) {

            CommonMethods cm = new CommonMethods( this) ;
            cm.show_message("Cannot obtain camera instance. Aborted");
            finish();

        }

        mPreview = new CameraPreview(this, 0, CameraPreview.LayoutMode.FitToParent, mCamera);
        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Un-comment below lines to specify the size.
        //previewLayoutParams.height = 500;
        //previewLayoutParams.width = 500;

        // Un-comment below line to specify the position.
        //mPreview.setCenterPosition(270, 130);

        mLayout.addView(mPreview, 0, previewLayoutParams);*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("CameraActivity(): onRestart() called") ;
    }


    public void printPath() {

        for ( int i = 0; i < Images.size(); i++ ) {

            System.out.println ( "CameraActivity() : " + Images.get(i)) ;

        }

    }

}

package com.example.root.mynavigationdrawer;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;


/**
 * Created by root on 25/12/15.
 */


public class UploadPhotoFragment extends android.support.v4.app.Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.photodetails, container, false);



    }




/*
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SmartRevise");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

                Log.d("MyCameraApp", "failed to create directory");
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

                try {

                    print("Image has been successfully captured");
                    UploadPhotoFragment fragment = new UploadPhotoFragment();

                    if ( findViewById(R.id.fragment_container) != null) {

                        getSupportFragmentManager().beginTransaction().add( R.id.fragment_container, fragment).commit() ;
                        print ( "Added the fragment") ;

                    }
                    else {

                        print ( "fragment container null") ;


                    }
                } catch (Exception e) {

                    print("error in onActivityResult() : " + e.getMessage().toString() );

                }

                //Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                // Bitmap takenImage = BitmapFactory.decodeFile(fileUri.getPath()); // This will crash if image is not loaded in main memnory properly. will look after it.
                // Load the taken image into a preview

                // ivPreview.setImageBitmap(takenImage);

                //---------------------

    */
/*

                if ( fileUri != null) {

                    Intent selfiSrc = new Intent(this, DisplayPictureActivity.class);
                    selfiSrc.putExtra("imgurl", fileUri);
                    ImageView ivPreview = (ImageView) findViewById(R.id.displayme);
                    if ( ivPreview != null ) {
                        print ( "imageview exists in MyActivyt") ;
                    }
                    else {
                        print ( "Null found in MyActivity") ;

                    }
                    startActivity(selfiSrc);


                    print("saved to : " + fileUri );

                }
                else {
                    print ( "fileuri is null") ;
                }
                //  Toast.makeText(this, "Image saved to:\n" +
                //         data.getData(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED)
                // User cancelled the image capture
                print ( "user cancelled the image capture");
            } else {
                // Image capture failed, advise user
                print( "Image capture failed");
            }*//*


            }

        }


    } // onActivity
*/

    public void print ( String str ) {

        System.out.println(str) ;
    }




}

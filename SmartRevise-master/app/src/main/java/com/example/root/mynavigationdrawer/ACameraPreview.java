package com.example.root.mynavigationdrawer;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.security.Policy;
import java.util.List;

/**
 * Created by root on 13/1/16.
 */
public class ACameraPreview extends SurfaceView implements SurfaceHolder.Callback {


    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    protected List<Camera.Size> mPreviewSizeList;
    protected List<Camera.Size> mPictureSizeList;

    public ACameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        parameters = mCamera.getParameters();

        mPreviewSizeList = parameters.getSupportedPreviewSizes();
        mPictureSizeList = parameters.getSupportedPictureSizes();


    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            System.out.println("ACamerPreview() : surfaceCreated() is called");
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("Error", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        System.out.println("ACamerPreview() : surfaceDestroyed() is called");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        System.out.println("ACamerPreview() : surfaceChanged() is called");
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {

            parameters = mCamera.getParameters();

            if ( parameters == null ) {

                System.out.println ( "Parameteres is null ") ;

            }
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            Camera.Size previewSize = determinePreviewSize(w, h) ;
            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRotation() == Surface.ROTATION_0) {

                mCamera.setDisplayOrientation(90);

            } else if (display.getRotation() == Surface.ROTATION_270) {

                mCamera.setDisplayOrientation(180);
            }

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;

            }
            mCamera.setPreviewDisplay(mHolder);
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            if ( sizes == null ){

                System.out.println( "Sizes List is null ");
            }
            Camera.Size size = sizes.get(0);
            for(int i=0;i<sizes.size();i++)
            {
                if(sizes.get(i).width > size.width)
                    size = sizes.get(i);
            }

            System.out.println("surfaceChanged() : w= " + w + " h= " + h) ;
            System.out.println("surfaceChanged(): setting image size: width :  " + size.width + " height : " + size.height) ;
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setPictureSize(size.width, size.height);
           // parameters = mCamera.getParameters();
            //Camera.Size s = parameters.getPictureSize();
            mCamera.setParameters(parameters);
            //System.out.println ( "\n Set image w= " + s.width + " h = " + s.height ) ;
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("Error", "Error starting camera preview: " + e.getMessage());
        }
    }



    public void stop() {
        if (null == mCamera) {
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }


    protected Camera.Size determinePreviewSize( int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
        /*
        if (portrait) {
            reqPreviewWidth = reqHeight;
            reqPreviewHeight = reqWidth;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }



*/

        reqPreviewWidth = reqWidth;
        reqPreviewHeight = reqHeight;

        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        System.out.println("ACamerPreview() : returning preview size : " + retSize.width   + " and "  + retSize.height );

        return retSize;
    }




}




package com.machadalo.audit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import org.opencv.android.Utils;


public class ImageCompareActivity extends AppCompatActivity {

    String photoPath1 = null;
    String photoPath2 = null;

    static final int REQUEST_TAKE_PHOTO = 1;

    Button bPic1;
    Button bPic2;
    Button bCompare;
//    TextView textView1;
//    TextView textView2;
    ImageView imageV1;
    ImageView imageV2,fullImageVw;
    Bitmap imageBitmap1 = null;
    Bitmap imageBitmap2 = null;
    TextView resultTxt;
    LinearLayout fullImage,twoImage;


//    static{ System.loadLibrary("opencv_java3"); }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.e("OpenCV", "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume()
    {
        super.onResume();
       /* if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }*/
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_compare);

        twoImage = (LinearLayout)findViewById(R.id.imageLinearlayout);
        fullImage = (LinearLayout)findViewById(R.id.FullimageLinearlayout);
        fullImageVw = (ImageView)findViewById(R.id.imageViewFull);

        bPic1 = (Button)findViewById(R.id.pic1Button);
        bPic2 = (Button)findViewById(R.id.pic2Button);
        bCompare = (Button)findViewById(R.id.compareButton);
//        textView1 = (TextView)findViewById(R.id.ImageCompareTxt1);
//        textView2 = (TextView)findViewById(R.id.imageCompareTxt2);
        imageV1 = (ImageView)findViewById(R.id.CompareImage1);
        imageV2 = (ImageView)findViewById(R.id.CompareImage2);
        resultTxt = (TextView)findViewById(R.id.CompareImageanswertxt);

        bPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(1);
            }
        });

        bPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(2);
            }
        });

        bCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Comp();
                compareImage();
            }
        });


    }


    public void Comp(){

        Mat img1 = new Mat();
        Mat img2 = new Mat();

        if (imageBitmap1 != null && imageBitmap2 !=null) {

            Utils.bitmapToMat(imageBitmap1, img1);
            Utils.bitmapToMat(imageBitmap2, img2);

            FeatureDetector Orbdetector = FeatureDetector.create(FeatureDetector.ORB);
            DescriptorExtractor OrbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

            Mat descriptors1 = new Mat();
            MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
            Orbdetector.detect(img1, keypoints1);
            OrbExtractor.compute(img1, keypoints1, descriptors1);

            Mat descriptors2 = new Mat();
            MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
            Orbdetector.detect(img2, keypoints2);
            OrbExtractor.compute(img2, keypoints2, descriptors2);


            MatOfDMatch matches = new MatOfDMatch();
            matcher.match(descriptors1,descriptors2,matches);

            double max_dist = 0;
            double min_dist = 100;

            List<DMatch> matchesList = matches.toList();

            //-- Quick calculation of max and min distances between keypoints
            for( int i = 0; i < descriptors1.rows(); i++ )
            { double dist = matchesList.get(i).distance;
                if( dist < min_dist ) min_dist = dist;
                if( dist > max_dist ) max_dist = dist;
            }

            LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
            MatOfDMatch gm = new MatOfDMatch();

            for( int i = 0; i < descriptors1.rows(); i++ )
            {
                if( matchesList.get(i).distance <= 3*min_dist )
                {
                    good_matches.addLast( matchesList.get(i));
                }
            }
            gm.fromList(good_matches);

            Mat img_matches = new Mat();

            List<KeyPoint>keypoints2_List = keypoints2.toList();
            List<KeyPoint>keypoints1_List = keypoints1.toList();

            LinkedList<Point> objList = new LinkedList<Point>();
            LinkedList<Point> sceneList = new LinkedList<Point>();
            for(int i=0;i<good_matches.size();i++){
                objList.addLast(keypoints2_List.get(good_matches.get(i).trainIdx).pt);
                sceneList.addLast(keypoints1_List.get(good_matches.get(i).queryIdx).pt);
            }


            MatOfPoint2f obj = new MatOfPoint2f();
            obj.fromList(objList);

            MatOfPoint2f scene = new MatOfPoint2f();
            scene.fromList(sceneList);

//            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(obj,scene);
           /* Features2d.drawMatches(
                    img1,
                    keypoints1,
                    img2,
                    keypoints2,
                    gm,
                    img_matches);
*/
            Mat img1_m = new Mat();
            Imgproc.cvtColor(img1,img1_m, Imgproc.COLOR_RGBA2RGB, 1);
            Mat img2_m = new Mat();
            Imgproc.cvtColor(img2,img2_m, Imgproc.COLOR_RGBA2RGB, 1);

            Mat outputImage = new Mat();
            Bitmap comboBmp = combineImages(imageBitmap1, imageBitmap2);
            Utils.bitmapToMat(comboBmp, outputImage);

            Features2d.drawMatches(img1_m, keypoints1, img2_m, keypoints2, gm, outputImage);

            Bitmap bitmap = Bitmap.createBitmap(outputImage.cols(), outputImage.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outputImage, bitmap);

            twoImage.setVisibility(View.GONE);
            fullImage.setVisibility(View.VISIBLE);
            fullImageVw.setImageBitmap(bitmap);
            resultTxt.setText(String.valueOf(good_matches.size()));

            /*Log.e("ImgComp",img_matches.height()+" "+ img_matches.width()+" "+img_matches.channels());
            resultTxt.setText(String.valueOf(good_matches.size()));
//            Bitmap tempIm = null;
//            Utils.matToBitmap(img_matches,tempIm);


            Bitmap bmp = null;
           Mat tmp = new Mat (img_matches.height(), img_matches.width(), CvType.CV_8U, new Scalar(4));
            try {
                //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);

                Imgproc.cvtColor(img_matches, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
                Log.e("InCreteBtm","createbitmap "+tmp.rows()+" "+tmp.cols());
                bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bmp);

            }
            catch (CvException e){
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
            }

            if (bmp != null) {
                Log.e("InImageComp", "bitmap");
                imageV2.setImageBitmap(bmp);
            }*/

        }

    }


    public void compareImage(){



        Mat img1 = new Mat();
        Mat img2 = new Mat();
        if (imageBitmap1 != null && imageBitmap2 !=null) {
            Utils.bitmapToMat(imageBitmap1, img1);
            Utils.bitmapToMat(imageBitmap2, img2);

            Mat hist1 = getHist(img1);
            Mat hist2 = getHist(img2);

            double compValue1 = Imgproc.compareHist(hist1,hist2, Imgproc.CV_COMP_BHATTACHARYYA);
            double compValue2 = Imgproc.compareHist(hist1,hist2,  Imgproc.CV_COMP_CORREL);
            double ans = getValue(compValue1,compValue2);
            String both = "val1: "+String.valueOf((1-compValue1)*100)+"\n val2:"+String.valueOf(compValue2*100);
            resultTxt.setText(String.valueOf(ans)+"  % matched \n"+both);
        }

    }

    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }


    public double getValue(double val1,double val2){


        double set1 = (1.0- val1)*100;
        double set2 = val2*100;
        double answer = set2;


        double diff = Math.abs(set1-set2);

        Log.e("imgeComp",diff+" -->" +set1+" "+set2+"  ");

        if (set2 < 0 || set2 <10.0)
            return 0.0;


        if (set2 >35.0) {

            if (set2 > 80.0 && set2 > set1) {
                return set2;
            }

            if (diff < 10.0) {
                if (set1 >= set2)
                    return ((2 * set1 + set2) / 3);
                else
                    return ((2 * set2 + set1) / 3);
            }

            if (diff > 10.0) {

                if (set1 > set2)
                    return set1;
                else
                    return set2;
            }
        }

        return answer;
    }


    private File createImageFile(int i) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        if (i==1)
            photoPath1 = image.getAbsolutePath();
        else if (i==2)
            photoPath2 = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent(int i) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(i);
            } catch (IOException ex) {
                // Error occurred while creating the File
                    ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
               /* Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);*/
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            if (photoPath1 != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
               imageBitmap1 = BitmapFactory.decodeFile(photoPath1, options);
                imageV1.setImageBitmap(imageBitmap1);
            }

            if (photoPath2 != null) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                imageBitmap2 = BitmapFactory.decodeFile(photoPath2, options);
                imageV2.setImageBitmap(imageBitmap2);

            }


        }

    }


    public static Mat getHist(Mat src){

        Vector<Mat> bgr_planes = new Vector<Mat>();
        Core.split(src, bgr_planes);
        MatOfInt histSize = new MatOfInt(256);
        final MatOfFloat histRange = new MatOfFloat(0f, 256f);
        boolean accumulate = false;
        Mat b_hist = new  Mat();
        Imgproc.calcHist(bgr_planes, new MatOfInt(0),new Mat(), b_hist, histSize, histRange, accumulate);
        return b_hist;


    }


}

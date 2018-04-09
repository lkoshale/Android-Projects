package com.example.root.mynavigationdrawer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by root on 28/12/15.
 */
public class BitMapUtility {



        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }


        public static  int [] getDimensions( String path) {

            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options) ;
                //BitmapFactory.decodeResource(Resources.getSystem(), image_id, options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;
                int arr[] = { imageHeight, imageWidth} ;
                System.out.print ( "got the dimensions : height " + imageHeight + "  width: " + imageWidth ) ;
                return arr ;

            } catch (Exception e) {
                System.out.print( "error in getDimensions():" + e.getMessage().toString() );
            }

            return null;

        }

        public static int calculateInSampleSize(  BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            System.out.println("raw image width : " + width);
            System.out.println("raw image height: " + height);
            System.out.println("required width : " + reqWidth);
            System.out.println("required height: " + reqHeight);

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                System.out.println ( " halfHeight : " + halfHeight + " halfWidth : " + halfWidth) ;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                System.out.println("x= "  + (halfHeight/inSampleSize ) );
                System.out.println ("y= " + (halfWidth/inSampleSize )) ;
                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                    System.out.println(" insampleSize : " + inSampleSize);
                }
            }

            System.out.print( "returning sample size of : "  + inSampleSize );
            //if ( inSampleSize == 1 )
               //  inSampleSize++;

            return inSampleSize;
        }


        public static Bitmap decodeSampledBitmapFromResource(String path ,
                                                             int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
         //   BitmapFactory.decodeResource(res, resId, options);
            BitmapFactory.decodeFile( path, options) ;

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            System.out.println ( "BitMapUtiltiy() : sampleSize : " + options.inSampleSize ) ;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            //return BitmapFactory.decodeResource(res, resId, options);
            System.out.print( "successfully decoded image into : " + reqWidth + " and " + reqHeight + " size ");
            return BitmapFactory.decodeFile( path, options) ;

        }


}



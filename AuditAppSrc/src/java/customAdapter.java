package com.machadalo.audit;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by nikhil on 7/4/2015.
 */
class customAdapter extends ArrayAdapter<String> {
    ProgressDialog pDialog;
    Bitmap bitmap;
    String url;
    private ImageLoader imgLoader;
    ImageView img;
    customAdapter(Context context, ArrayList<String> pictures) {
        super(context,R.layout.submitted_listlview ,pictures);
    }

    private static class ViewHolder {  // view holder holds all information of a list item

        ImageView imageView;
        String imageURL;
        Bitmap bitmap;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {   // override method with walks by list
        LayoutInflater customInflater = LayoutInflater.from(getContext());
        // View customview = customInflater.inflate(R.layout.custum_row,parent,false);
        String picture = getItem(position); // get current list picture name
        //TextView bannertext = (TextView) customview.findViewById(R.id.bannerText);
        //img = (ImageView) customview.findViewById(R.id.bannerView);
        //String url = "http://infolitics.byethost4.com/media/uploads/"+"1"+".jpg";
        //new LoadImage().execute("http://www.allindiaflorist.com/imgs/arrangemen4.jpg");


        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = customInflater.inflate(R.layout.submitted_listlview, null);
            //viewHolder = new ViewHolder();
            img = (ImageView)convertView.findViewById(R.id.list_image);
            //   convertView.setTag(viewHolder);
            imgLoader = new ImageLoader(getContext());
        }
        // viewHolder = (ViewHolder)convertView.getTag();
        // viewHolder.imageURL = "http://infolitics.byethost4.com/media/uploads/"+picture;
        url = "http://android.infiniteloopsinc.com/audit/media/uploads/"+picture;
        imgLoader.DisplayImage(url, img);
        return convertView;
        //return customview;


    }

    private  class DownloadAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {
        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            // TODO Auto-generated method stub
            //load image directly

            ViewHolder viewHolder = params[0];
            try {
                URL imageURL = new URL(viewHolder.imageURL);
                viewHolder.bitmap = BitmapFactory.decodeStream(imageURL.openStream());  // bitmap is loaded from server from here
            } catch (IOException e) {
                // TODO: handle exception
                viewHolder.bitmap = null;
            }
            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {

            // TODO Auto-generated method stub
            if (result.bitmap == null) {
                result.imageView.setImageResource(R.mipmap.ic_launcher); // assigning  image if picture doesnot exist
            } else {
                result.imageView.setImageBitmap(result.bitmap); // if picture exists then assign bitmap
            }
        }
    }







}

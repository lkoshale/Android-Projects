package com.machadalo.audit;


import android.app.Activity;
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
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by nikhil on 7/4/2015.
 */
class SubmittedAdapter extends ArrayAdapter<String> {
    ProgressDialog pDialog;
    Bitmap bitmap;
    Activity context;
    String url;
    private ImageLoader imgLoader;
    ImageView img;
    TextView txtinventid,txtaddress;
    ArrayList<String> inventid,address;
    public LayoutInflater inflater;
    SubmittedAdapter(Activity context, ArrayList<String> pictures,ArrayList<String> inventid,ArrayList<String> address) {
        super(context,R.layout.submitted_listlview ,pictures);
        this.inventid = inventid;
        this.address = address;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {  // view holder holds all information of a list item

        ImageView imageView;
        String imageURL;
        Bitmap bitmap;
        TextView txtinventid,txtaddress;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {   // override method with walks by list
        LayoutInflater customInflater = context.getLayoutInflater();

        View customview = customInflater.inflate(R.layout.submitted_listlview, null,true);
        String picture = getItem(position);

        /*Not Working*/
        /*txtinventid = (TextView)customview.findViewById(R.id.listInventoryid);
        txtaddress = (TextView)customview.findViewById(R.id.listaddress);*/

        /*Till Here*/

        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = customInflater.inflate(R.layout.submitted_listlview, null);
            //viewHolder = new ViewHolder();
            img = (ImageView)customview.findViewById(R.id.list_image);
            txtinventid = (TextView)customview.findViewById(R.id.listInventoryid);
            txtaddress = (TextView)customview.findViewById(R.id.listaddress);
            imgLoader = new ImageLoader(getContext());
        }


        // viewHolder = (ViewHolder)convertView.getTag();
        // viewHolder.imageURL = "http://infolitics.byethost4.com/media/uploads/"+picture;
        url = "http://android.infiniteloopsinc.com/audit/media/inventory/"+picture;
        imgLoader.DisplayImage(url, img);
        txtinventid.setText(inventid.get(position));
        txtaddress.setText(address.get(position));
        //return convertView;
        return customview;


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

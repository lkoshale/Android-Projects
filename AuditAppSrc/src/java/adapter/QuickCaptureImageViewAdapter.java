package com.machadalo.audit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.machadalo.audit.R;

import java.util.List;

/**
 * Created by Lokesh on 04-07-2017.
 */

public class QuickCaptureImageViewAdapter extends RecyclerView.Adapter<QuickCaptureImageViewAdapter.MyViewHolder> {

    public List<Bitmap>mList;
    public Context context;

    public QuickCaptureImageViewAdapter(Context context,List<Bitmap>bitmaps){
        this.mList = bitmaps;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quick_image_view_list_layout, parent, false);

       return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bitmap bitmap = mList.get(position);

        holder.imageView.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.QuickImageView_image_view);
        }
    }

}

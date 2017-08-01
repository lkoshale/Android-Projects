package com.machadalo.audit.adapter;

/**
 * Created by Asna Euphoria on 23-01-2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.machadalo.audit.ImageLoader;
import com.machadalo.audit.R;
import com.machadalo.audit.extras.GalleryData;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    ImageLoader imgLoader;
    private List<GalleryData> DatasList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public MyViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.imgCard);

        }
    }


    public GalleryAdapter(List<GalleryData> DatasList) {
        this.DatasList = DatasList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.galleryrow, parent, false);
        imgLoader = new ImageLoader(parent.getContext());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GalleryData Data = DatasList.get(position);
        imgLoader.DisplayImage(Data.getUrl(), holder.img);
        Bitmap bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(Data.getfilePath()), 80, 120);

        holder.img.setImageBitmap(bmp);
        //holder.societyDate.setText(Data.getDa());
    }

    @Override
    public int getItemCount() {
        return DatasList.size();
    }
}

package com.machadalo.audit;

/**
 * Created by DELL-PC on 12/15/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewCustomAdapter extends BaseAdapter
{
    public String id[];
    public String details[];


    public Activity context;
    public LayoutInflater inflater;

    public ListViewCustomAdapter(Activity context,String[] id, String[] details) {
        super();

        this.context = context;
        this.id = id;
        this.details = details;


        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return id.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder
    {

        TextView txtViewId;
        TextView txtViewDetails;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_single, null);


            holder.txtViewId = (TextView) convertView.findViewById(R.id.txtId);
            holder.txtViewDetails = (TextView) convertView.findViewById(R.id.txtDetails);

            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();


        holder.txtViewId.setText(id[position]);
        holder.txtViewDetails.setText(details[position]);

        return convertView;
    }

}
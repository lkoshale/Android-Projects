package com.machadalo.audit.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.machadalo.audit.LocationFind;
import com.machadalo.audit.R;
import com.machadalo.audit.Utils;
import com.machadalo.audit.extras.PendingData;

import java.util.ArrayList;
import java.util.List;

// @NOT being used now
// adapter for the recyclerview of pendingDetailActivity class

/**
 * Created by Lokesh on 15-06-2017.
 */

//Adapter Used in the pending Detail Activity
public class PendingDetailDataAdapter extends RecyclerView.Adapter<PendingDetailDataAdapter.ViewHolder> {

    private List<PendingData>mList = new ArrayList<>();
    private Context activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView inventoryName;
        public TextView inventoryId;
        public Spinner validActivities;
        public Button bCapture;

        public ViewHolder(View view) {
            super(view);
            inventoryName = (TextView)view.findViewById(R.id.pending_inventoryTYP);
            inventoryId = (TextView)view.findViewById(R.id.pending_inventoryID);
            validActivities = (Spinner) view.findViewById(R.id.pending_invsible_spinner);
            bCapture  = (Button) view.findViewById(R.id.bCapture);

        }

    }

    public PendingDetailDataAdapter(Context context, List<PendingData>list){
        activity = context;
        this.mList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inventoryDetailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list, parent, false);
        return new ViewHolder(inventoryDetailView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PendingData pendingData = mList.get(position);
        holder.inventoryName.setText(pendingData.getInventoryName());
        holder.inventoryId.setText(pendingData.getInventoryID());

        List<String> validAct = pendingData.getValidActs();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item, validAct);
        holder.validActivities.setAdapter(adapter);

        /*
        holder.validActivities.setVisibility(View.INVISIBLE);
        holder.inventoryType.setVisibility(View.VISIBLE);
        holder.inventoryType.setText(pendingData.getInventoryTyp());
        */
        holder.bCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationFindIntent = new Intent(activity, LocationFind.class);
                locationFindIntent.putExtra("supplierName", pendingData.getSupplierName());
                locationFindIntent.putExtra("activityType",(String)holder.validActivities.getSelectedItem());
                locationFindIntent.putExtra("activityDate", Utils.getCurrentDateString());  //TODO changed currentDte to utils date
                locationFindIntent.putExtra("shortlistedInventoryDetailsId", pendingData.getShortlistedInventoryDetailsId());  //todo supplieed ssid
                locationFindIntent.putExtra("inventoryType", " ");

                Log.v("ShowInvAdapterDetails", "details");
              //  Log.d("SelectedItem", (String)holder.validActivities.getSelectedItem());
             //   Log.d("ShortInvId", shortlistedInventoryId);
             //   Log.d("suppName", supplierName);

                activity.startActivity(locationFindIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();  //TODO mList.size
    }

}

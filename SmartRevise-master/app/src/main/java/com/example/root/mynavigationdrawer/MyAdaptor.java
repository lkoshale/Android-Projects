package com.example.root.mynavigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 * Created by root on 22/12/15.
 */

    public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {
        private HashMap mDataset;
        private Boolean when_no_category = false;
        private ActionMode mActionMode;
        private static Context context;
        private String [] to_display;
        private int position;

        public interface OnItemClickListener {
            public void onItemClicked(int position);
        }

        public interface OnItemLongClickListener {
            public boolean onItemLongClicked(int position);
        }

        //private Vector<String> dataSet;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            // each data item is just a string in this case
            public TextView mTextView;
            public Button  mButton;
            public ViewHolder(View v) {

                super(v);
                mTextView =    (TextView)v.findViewById(R.id.tvInformation);
                mButton =    (Button)v.findViewById(R.id.mybutton);
                //((Activity)context).registerForContextMenu( v ); // registers this view for Long Click Event
//                v.setOnCreateContextMenuListener( this );

            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add( Menu.NONE,R.id.delete, Menu.NONE,R.string.delete_category);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdaptor(Context context, HashMap myDataset, Boolean var) {
            mDataset = myDataset;
            when_no_category = var;
            this.context = context;
            to_display = new String[myDataset.size()];
            myDataset.keySet().toArray(to_display);

        }

       public MyAdaptor ( Context context, String [] data, Boolean var) {

           this.context = context;
           to_display = data;
           when_no_category = var;


       }


    // Create new views (invoked by the layout manager)
        @Override
        public MyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v;

            v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.myhomelayout, parent, false);

            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v); // get a new ViewHolder

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
    /*
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
//            holder.mTextView.setText(mDataset[position]);
              //holder.mButton.setText(mDataset[position]);

            try {

                holder.mButton.setText(dataEnum.nextElement());

            }
            catch ( Exception e ) {

                System.out.print( "Error in onBindViewHolder() " + e.getMessage().toString() );

            }

        }

    */@Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) { // display data on ViewHolder

//            final String id =  mDataset.get( to_display[position]).toString() ; //  get the actual id of the category clicked

            if ( !when_no_category) {
                System.out.print("no_category is false..setting title to button: " + to_display[position]);
                viewHolder.mButton.setText(to_display[position]);

            }
            else { // true
                System.out.println ( "no_category is true..making button disappear") ;
                viewHolder.mButton.setVisibility(View.GONE);
                viewHolder.mTextView.setText(to_display[position]);

            }
            viewHolder.mButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition( viewHolder.getAdapterPosition());
                    return false;
                }
            });



    }

    // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            if ( when_no_category ) {

                return 1;
            }

            return    mDataset.size();


        }

        public int GetPosition() {
            return position;
    }

        public void setPosition(int position) {
            this.position = position;
    }

/*
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback( ) {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.delete:
                    DatabaseHelper helper = new DatabaseHelper( context );
                    helper.DeleteCategoryCascade( helper, (String)mode.getTag()) ;
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };


*/





}



package com.example.root.mynavigationdrawer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class ScreenSlidePagerActivity extends FragmentActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;


    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_container);
        // Instantiate a ViewPager and a PagerAdapter.
        String category_id = getIntent().getExtras().getString("category_id");
        mPager = (ViewPager) findViewById(R.id.pager);  // found Pager , ok
        ArrayList<ImageDataStructure> myObjects = new ArrayList<ImageDataStructure>();
        print("getting category id as : "  + category_id + " in ScreenSliderPagerActivity");
        myObjects = loadGivenCategoryDataFromdb( category_id ) ;

        if ( myObjects != null ) {

            mPagerAdapter = new ScreenSlidePagerAdapter(  getSupportFragmentManager(), myObjects); // initialize adaptor with all data you got to display
            mPager.setAdapter(mPagerAdapter);
            //print("..called the ViewPager with Adaptor..");

        }
        else {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your Image Box is Empty !")
                    .setMessage("Please upload some images by clicking Upload Notes button.After that we Promise to  show you your Notes here! ")
                    .setCancelable(false)
                    .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    public ArrayList<ImageDataStructure> loadGivenCategoryDataFromdb( String id) {


        try {
            DatabaseHelper db = new DatabaseHelper(this);
            ArrayList<ImageDataStructure> myObjects = new ArrayList<ImageDataStructure>();
            ImageDataStructure[] ds = db.getImageDetails(db, id);

            if ( ds == null) {

                return null;

            }

            for (int i = 0; i < ds.length; i++) {
                myObjects.add(ds[i]);
            }

            return myObjects;

        } catch (SQLiteException e) {
            print("Error in loadGivenCategoryDataFromdb() " + e.getMessage().toString() );
        }
        return  null;

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter { // This class provides images that will be rendered.

        private ArrayList<ImageDataStructure> itemData;
        private long baseId = 0;

        public ScreenSlidePagerAdapter(FragmentManager fm,  ArrayList<ImageDataStructure> itemData ) {
            super(fm);
            this.itemData = itemData;
        }

        @Override
        public Fragment getItem(int position) {
        //    return new ScreenSlidePageFragment();
            print("calling getItem..." + position) ;
            ImageDataStructure object = itemData.get(position);
            return ScreenSlideFragment.newInstance(position, object);
        }

        @Override
        public int getCount() {
            return itemData.size();
        }

        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }


    }

    public void print ( String str ) {

        System.out.println(str) ;
    }

}

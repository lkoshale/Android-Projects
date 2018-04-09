package com.example.root.mynavigationdrawer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int START_ADD_NEW_CATEGORY_ACTIVITY = 109;
    private static  final int DELETE_CATEGORY_ACTIVITY = 110;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public HashMap data;
    public Vector<String> data_to_display;
    public String[] to_display;


    // camera constants
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    //public  static ImageDetailsHelper image_helper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewCategory.class);
                startActivityForResult(intent, START_ADD_NEW_CATEGORY_ACTIVITY);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);


        //  code for using RecdyclerView Logic


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view); // find RecyclerView

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager); // connect the Layout Manager

        // specify an adapter (see also next example)
        // Database operations
        DatabaseHelper helper = new DatabaseHelper(this);
//        image_helper = new ImageDetailsHelper( this) ;
        print("Creating image database...");
        //helper.DeleteCategoryDatabase(helper); // clean database before inserting
        //helper.DeleteImageDatabase( helper);
        //System.out.println("Database Deleted") ;
        //helper.InitializeCategoryDatabase(helper, topics); //  insert
        //System.out.println( "Category Database initialized..") ;
        data = helper.ReadAndReturnCategoryDatabase(helper); // Display .

        if (data.isEmpty()) {


            //setContentView(R.layout.when_1no_data_in_category);

//            TextView tv = (TextView) findViewById(R.id.tvContentMain);

            //          tv.setText("No Categories Exist yet. Create your own Category from the \"Create New Categpory\" option from the Drawer");
            String[] initial = {"No Categories Exist yet. Create your own Category from the \"Create New Categpory\" option from the Drawer \n OR Clcicking Add button."};
            mAdapter = new MyAdaptor(this, initial, true); // pass this data to Adapter class
            mRecyclerView.setAdapter(mAdapter); // connect the Adapter to RecyclerView
            print("Data is empty.");
            //tv.setVisibility(View.VISIBLE);

            //mLayoutManager.addView( tv );

        } else {

            // now we have data. Bingo !

            // use a linear layout manager
            to_display = new String[data.size()];
            data.keySet().toArray(to_display);
            mAdapter = new MyAdaptor(this, data, false); // pass this data to Adapter class
            mRecyclerView.setAdapter(mAdapter); // connect the Adapter to RecyclerView
            registerForContextMenu( mRecyclerView);

            AddIems(navigationView, to_display);  // adding Items to Navigation Drawer Dynamically
            display(data);
            System.out.println("printing image table data");
            helper.PrintImageDatabase(helper);

            mRecyclerView.addOnItemTouchListener(

                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // TODO Handle item click
                            String id = "";
                            print("Something got clicked: " + position);
                            String fetched_topic = to_display[position];
                            print("fetched topic " + fetched_topic);
                            if (data.containsKey(fetched_topic)) {
                                id = data.get(fetched_topic).toString();
                            }
                            Intent topicinformation = new Intent();
                            topicinformation.setClass(MainActivity.this, FromMainActivityToTopicInformation.class);
                            topicinformation.putExtra("title", to_display[position]);
                            topicinformation.putExtra("id", id);
                            print("inserting id and title : " + id + " " + to_display[position]);
                            startActivity(topicinformation);

                        }
                    })

            );
/*
            mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    System.out.print("Long clicked on the view..");
                    MainActivity.this.registerForContextMenu(v);
                    return true;

                }
            });*/

        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the ghar/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_ADD_NEW_CATEGORY_ACTIVITY) {

            if (resultCode == RESULT_OK) {

                show_message("Your New Category has been created");

            }


        }

        if ( requestCode == DELETE_CATEGORY_ACTIVITY) {

            if ( resultCode == RESULT_OK ) {

                show_message("your category has been deleted asshole");

            }

        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
            print("Navigating to home");


        } else if (id == R.id.nav_gallery) {
            print("creating a new category ");
            Intent intent = new Intent(this, AddNewCategory.class);
            startActivityForResult(intent, START_ADD_NEW_CATEGORY_ACTIVITY);

        } else if (id == R.id.nav_manage) {
            print("navigating to settings ");

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if ( id == R.id.delete_category ) {


            Intent intent = new Intent(this, DeleteCategory.class);
            intent.putExtra("data", data ) ;
            startActivityForResult(intent, DELETE_CATEGORY_ACTIVITY);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*

    public void launchCamera( View view) {

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = UploadPhotoFragment.getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        print("Image storing at" + fileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        // start the image capture Intent
        UploadPhotoFragment firstfragment = new UploadPhotoFragment( );
        firstfragment.setArguments( getIntent().getExtras() );
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, firstfragment).commit();
                ;
//        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }
*/


    public void AddIems(NavigationView navigationView, String[] data) {


        try {

            final Menu menu = navigationView.getMenu();
//            MenuItem item = (MenuItem)navigationView.findViewById(R.id.alltopics);
            final SubMenu subMenu = menu.addSubMenu("Last 10 Topics Recently Revised");
//            Drawable d = new BitmapDrawable(getResources(), "/root/AndroidStudioProjects/MyNavigationDrawer/app/src/main/res/drawable/notes.png");

            for (int i = 0; i < data.length; i++) {

//                menu.add( R.id.mygroup, menu.NONE, menu.NONE,  data[i]) ;

                subMenu.add(data[i]);

            }

            for (int i = 0; i < data.length; i++) {

//                menu.add( R.id.mygroup, menu.NONE, menu.NONE,  data[i]) ;

                subMenu.getItem(i).setIcon(R.drawable.notes);
                print("Setting image for item " + i);

            }

            refreshNavigationView(navigationView);
        } catch (Exception e) {

            print("Error in AddItems() : " + e.getMessage().toString());

        }

    }


    public void refreshNavigationView(NavigationView mNavigationView) {


        try {
            for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {
                final View child = mNavigationView.getChildAt(i);
                if (child != null && child instanceof ListView) {
                    final ListView menuView = (ListView) child;
                    final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                    final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                    wrapped.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {

            print("Error in refreshNavigationView() : " + e.getMessage().toString());

        }


    }

    public void print(String str) {

        System.out.println(str);
    }

    public void display(HashMap hmap) {

        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }


    }


    public void show_message(CharSequence text) {

        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        System.out.print("yes..menue is created");
        String id = (String) v.getTag();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(
                R.menu.context_menu, menu
        );
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        System.out.print("item on the menue is clicked");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if ( info != null ) {
            Long id = info.id;
            int position = info.position;

            System.out.println ( "got the item id in onContextItemSelected():  " + id ) ;
            System.out.println ( "got the item position clicked  in onContextItemSelected():  " + position ) ;

        }
        else {
            System.out.print( "info is null...");
        }


        //View v = info.targetView;
        //String id = (String) v.getTag();
        switch (item.getItemId()) {

            case R.id.delete:

                DatabaseHelper helper = new DatabaseHelper(this);
            //    helper.DeleteCategoryCascade(helper, id);

                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

}
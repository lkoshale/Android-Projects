package com.machadalo.audit;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawer extends Fragment {
    Fragment fragment = null;
    ExpandableListView expListView;
    private HashMap<Integer, List<String>> childActions = new HashMap<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean userLearnedDrawer;
    private boolean FromSavedInstanceState=false;
    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ListView listView;
    private TextView textView;
    HashMap<String, List<String>> listDataChild;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    public NavigationDrawer() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = Boolean.valueOf(readFromPrefrence(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            FromSavedInstanceState = true;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        textView = (TextView) v.findViewById(R.id.nameView);

        return v;
    }

    public void setUp(int fragmentId,DrawerLayout drawerLayout, Toolbar toolbar,String name){
        textView.setText(name);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {

                getActivity().invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    saveToPrefrence(getActivity(), KEY_USER_LEARNED_DRAWER, userLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }
        };

        Log.i("mytag", userLearnedDrawer + " " + FromSavedInstanceState);
        if (!userLearnedDrawer && !FromSavedInstanceState) {
            drawerLayout.openDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });




        // setting list adapter


        //getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
//        mDrawerLayout.closeDrawer(expListView);


    }



    // Catch the events related to the drawer to arrange views according to this
    // action if necessary...
    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerStateChanged(int status) {

        }

        @Override
        public void onDrawerSlide(View view, float slideArg) {

        }

        @Override
        public void onDrawerOpened(View view) {
        }

        @Override
        public void onDrawerClosed(View view) {
        }
    };




    public static void saveToPrefrence(Context context, String prefrenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefrenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPrefrence(Context context, String prefrenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefrenceName, defaultValue);
    }

}


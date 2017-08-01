package com.machadalo.audit.extras;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.machadalo.audit.AssignedFragmentAdapter;
import com.machadalo.audit.R;
import com.machadalo.audit.adapter.DataAdapter;
import com.machadalo.audit.sqllite.AuditGS;
import com.machadalo.audit.sqllite.DataBaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ratan on 7/27/2015.
 */
public class TabFragment extends Fragment {

    public static String TAG = "TabFragment";

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 5 ;
    /*Main Variable from Assighned*/
    private String jsonResult;
    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();
    String getIntActivity;
    private String url = "http://android.infiniteloopsinc.com/audit/media/navcounter.php"; // our Url to server
    private String awsURL = "http://android.infiniteloopsinc.com/audit/media/navcounter.php"; // our Url to server
    DataBaseHandler sqlitedb;
    private List<AuditGS> queueData = new ArrayList<>();
    ProgressDialog dialog = null;
    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    String strAdCount;
    int getIntentAct;
    /*Main Variable from Assighned*/
    private View myFragmentView;
    private RecyclerView recyclerView;
    private DataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Data> dataList = new ArrayList<>();
    String updatecount;
    String asscount;
    String countpending;
    int queueCount=0;

    private int auditCount,pendingCount,submitCount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            View x =  inflater.inflate(R.layout.tab_layout, null);
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        /**
         *Set an Apater for the View Pager
         */

        View rootView = null;
        myFragmentView = inflater.inflate(R.layout.fragment_one, container, false);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recycler_view);

        sqlitedb = DataBaseHandler.getInstance(getContext());
        queueData = sqlitedb.up_getAllPendingAudits();
        auditCount = sqlitedb.getAuditCount();
        queueCount=queueData.size();

        auditCount = sqlitedb.getAuditCount();
        pendingCount = sqlitedb.getAuditPendingCount();
        submitCount = auditCount - pendingCount;
/*
        Toast.makeText(getContext(),
                auditCount+" "+pendingCount+" "+ submitCount, Toast.LENGTH_LONG).show();*/
        Log.e("Counter" ,auditCount+" "+pendingCount+" "+ submitCount );
        viewPager.setAdapter(new AssignedFragmentAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.setupWithViewPager(viewPager);
        return x;

    }

/*

    class AssignedFragmentAdapter extends FragmentPagerAdapter{

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public AssignedFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        *//**
         * Return fragment with respect to Position .
         *//*

        @Override
        public Fragment getItem(int position)
        {
          switch (position){
              case 0 :
                  return new Capturefragment();
              case 1 :
                  Log.e("INFO", "assigned fragment");
                  return new AssignedFragment();
              case 2 : return new PendingFragment();
              case 3 : return new CompletedFragment();
              case 4 : return new QueueFragment();
          }
        return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        *//**
         * This method returns the title of the tab according to the position.
         *//*

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Capture";
                case 1 :
                    return "Assigned (" +auditCount+ ")";
                case 2 :
                    return "Pending (" +pendingCount+")" ;
                case 3 :
                    return "Completed (" + submitCount+")";
                case 4 :
                    return "Queued (" +queueCount+ ")";

            }
                return null;
        }
    }*/

}

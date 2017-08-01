package com.machadalo.audit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.machadalo.audit.fragments.AssignedFragment;
import com.machadalo.audit.fragments.Capturefragment;
import com.machadalo.audit.fragments.CompletedFragment;
import com.machadalo.audit.fragments.QueueFragment;
import com.machadalo.audit.fragments.PendingFragment;

/**
 * Created by root on 27/1/17.
 */

public class AssignedFragmentAdapter extends FragmentPagerAdapter {
    public static int int_items = 5 ;

    //Class variable for the counts to be displayed at the title of the tab
    public static int AssignedCount=0,PendingCount=0,CompletedCount=0,QueedCount=0;

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    public AssignedFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return fragment with respect to Position .
     */

    //Edited by lokesh : instance of the adapter is passed to the
    // fragment for changing the tab tittle count dynamically
    @Override
    public Fragment getItem(int position)
    {
        switch (position){
            case 0 :
                return new Capturefragment();
            case 1 :
                Log.e("INFO", "assigned fragment");
                AssignedFragment AF = new AssignedFragment();
                AF.setAssignedFragmentAdapter(this);
                return AF;
            case 2 :
                PendingFragment pf = new PendingFragment();
                pf.setAssignedFragmentAdapter(this);
                return pf;
            case 3 :
                CompletedFragment cf = new CompletedFragment();
                cf.setAssignedFragmentAdapter(this);
                return cf;
            case 4 :
                QueueFragment QF = new QueueFragment();
                QF.setAssignedFragmentAdapter(this);
                return QF;
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


    /**
     * This method returns the title of the tab according to the position.
     */

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0 :
                return "Capture";
            case 1 :
                return "Assigned "+"( "+String.valueOf(AssignedCount)+")";
            case 2 :
                return "Pending " +"( "+String.valueOf(PendingCount)+")";
            case 3 :
                return "Completed"+"( "+String.valueOf(CompletedCount)+")";
            case 4 :
                return "Queued "+"( "+String.valueOf(QueedCount)+")";

        }
        return null;
    }

    // Sets the count of each tabs

    public void setPendingCount(int pendingCount){
        PendingCount = pendingCount;
    }

    public void setCompletedCount(int completedCount){
        CompletedCount = completedCount;
    }

    public void setQueedCount(int queedCount){
        QueedCount = queedCount;
    }

    public void setAssignedCount(int assignedCount){
        AssignedCount = assignedCount;
    }
}

package com.example.root.sortvisualizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 15/5/16.
 */
public class BucketSortModel {
    /*

      node structure for BucketSort List . because each index of the List will contain a bucket which is essentially  a list ,
      that's why it contains a list which represents a bucket at any given index, a variable size which is size of the
      bucket as size grows when elements are inserted.

     */

    private List<Integer> mBucketList = new ArrayList<>();
    private int mSize;

    public List<Integer> getMbucketList() {
        return mBucketList;
    }

    public void setMbucketList(List<Integer> mbucketList) {
        this.mBucketList = mbucketList;
    }

    public int getmSize() {
        return mSize;
    }

    public void setmSize(int mSize) {
        this.mSize = mSize;
    }

}

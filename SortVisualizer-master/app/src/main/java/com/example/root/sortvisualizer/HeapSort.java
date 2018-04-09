package com.example.root.sortvisualizer;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by root on 14/5/16.
 */
public class HeapSort  {

    List<HeapSortModel> input;
    int heap_capacity;
    int heap_size;

    public HeapSort(List<HeapSortModel> input){
        this.input = input;
        this.heap_capacity = input.size() - 1 ;
        this.heap_size = input.size() - 1 ;
        build_heap();
        print();
    }

    int parent(int index) {
        return index/2;
    }
    int left_child(int index) {
        int l = 2 * index + 1;
        if ( l <= heap_size ) {
            return l;
        }
        else {
            return -1;
        }

    }
    int right_child( int index) {
        int r =  2 * index + 2;
        if ( r <= heap_size) {
            return r;
        }
        else {
            return -1;
        }

    }


    int get_min_index(int root, int left, int right){

        int min_index = root;
        if ( left == -1 && right == -1 ) {
            return root;

        }

        if ( left == -1 ) {
            if ( input.get(right).getmNumber() < input.get(root).getmNumber() )  {
                return right;
            }
            else {
                return root;
            }
        }

        if ( right == -1 ) {

            if ( input.get( left ).getmNumber() < input.get( root ).getmNumber()) {
                return left;
            }
            else {
                return root;
            }
        }


        if ( input.get( left).getmNumber() < input.get( right ).getmNumber() ) {
            min_index = left;
        }
        else {
            min_index = right;
        }
        if ( input.get( min_index).getmNumber() < input.get( root).getmNumber()) {
            return min_index;
        }
        else{
            return root;
        }
    }

    void swap( int first, int second) {

        HeapSortModel temp = input.get(first);
        input.set(first, input.get(second));
        input.set(second, temp);
    }

    void heapify( int root ) {
        if ( root <= heap_size ) {

           // System.out.print("heapifying: \n " + root);
            int left = left_child(root);
            int right = right_child(root);
            if ( left == -1 && right == -1 )
                return;

            int min_index = get_min_index(root, left, right);
            if (min_index == root) {
                return;
            }
            swap(root, min_index);
            heapify(min_index);
        }
    }

    void build_heap( ){
        int n = heap_size + 1;
        int k = n/2 - 1;
        for ( ; k >= 0; k--) {
           // System.out.println("i=" + i);
            heapify(k);
        }

    }

    void print( ) {
        int i;
        int v;
        System.out.println (" printing .....\n");
        System.out.println ( "heap size : " + heap_size) ;
        for ( i = 0; i <= heap_size; i++ ) {
            v = input.get(i).getmNumber();
            System.out.print(v + " ");
        }

    }

    HeapSortModel  extract_min() {
        if ( heap_size >= 0 ) {
            HeapSortModel min = input.get(0);
            swap(0, heap_size);
            heap_size--;
            heapify(0);
            return min;
        }
        else {
            return null;
        }
    }

//
//    public static void  main( String [] args) {
//        List<Integer> mNumbersList = new ArrayList<>();
//        Random rn = new Random();
//        int max = 10;
//        int min = 1;
//        int i;
//        mNumbersList.add(0, -100);
//        for( i = 1; i <=10; i++ ) {
//            mNumbersList.add(i, rn.nextInt(max - min + 1) + min);
//        }
//        HeapSort h = new HeapSort(mNumbersList);
//        h.print();
//        for ( i = 1; i < mNumbersList.size(); i++ ) {
//            System.out.println (h.extract_min() + " " );
//        }
//    }

}

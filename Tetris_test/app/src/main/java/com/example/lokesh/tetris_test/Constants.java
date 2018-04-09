package com.example.lokesh.tetris_test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by lokesh on 9/9/17.
 */

public class Constants {

    public static int SCORE = 0;

    public static boolean inComing = false;
    public static boolean gameOver = false;

    public static Canvas canvas;
    public static int DEFAULT_COLOR;
    public static int ROW;
    public static int COL;
    public static int mX;
    public static int mY;
    public static int STROKE_WIDTH;
    public static int STROKE_COLOR;


    public static String HIGH_SCORE = "highScore";
    public static int HIGHSCORE = 0;

    public static final int LEFT = -100;
    public static final int RIGHT = 100;

    public static final int IBLOCK = 0;
    public static final int OBLOCK = 1;
    public static final int JBLOCK = 2;
    public static final int LBLOCK = 3;
    public static final int SBLOCK = 4;
    public static final int ZBLOCK = 5;

    public static List<Integer> COLORS = new ArrayList<>();

    public static void setCOLORS() {
        COLORS.add(Color.rgb(169, 26, 201));
        COLORS.add(Color.rgb(96, 26, 201));
        COLORS.add(Color.rgb(26, 201, 96));
        COLORS.add(Color.rgb(136, 188, 24));
        COLORS.add(Color.rgb(219, 19, 102));
        COLORS.add(Color.rgb(255, 140, 40));

    }

    public static int getINT(){
        Random random = new Random();
        int k = random.nextInt(6);
        return k;
    }

    public static int getColor(){
        Random random = new Random();
        int k = random.nextInt(Constants.COLORS.size());

        if(k>COLORS.size()) k =0;

        return COLORS.get(k);
    }

    public static void setCanvas(Canvas canvas) {
        Constants.canvas = canvas;
    }

    public static void setStrokeColor(int strokeColor) {
        STROKE_COLOR = strokeColor;
    }

    public static void setStrokeWidth(int strokeWidth) {
        STROKE_WIDTH = strokeWidth;
    }

    public static void setDefaultColor(int defaultColor) {
        DEFAULT_COLOR = defaultColor;
        Log.e("Default color :",DEFAULT_COLOR+" "+Color.BLACK);
    }

    public static void setmX(int mX) {
        Constants.mX = mX;
    }

    public static void setmY(int mY) {
        Constants.mY = mY;
    }

    public static void setROW(int ROW) {
        Constants.ROW = ROW;
    }

    public static void setCOL(int COL) {
        Constants.COL = COL;
    }

    public static boolean isInComing() {
        return inComing;
    }

    public static void setInComing(boolean inComing) {
        Constants.inComing = inComing;
    }
}

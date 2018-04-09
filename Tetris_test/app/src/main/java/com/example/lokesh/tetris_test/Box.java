package com.example.lokesh.tetris_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

/**
 * Created by lokesh on 8/9/17.
 */

public class Box {

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;


    private int posR,posC;
    public boolean isRep = false;

    private int color;
    private int left;
    private int up;
    private int right;
    private int bottom;
    private boolean isColored;
    private Paint paint;


    public Box(int left,int up,int right,int bottom,int i,int j){

        posR = i;
        posC = j;
        this.color = Constants.DEFAULT_COLOR;
//        this.surfaceHolder = surfaceHolder;
//        this.canvas = canvas;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.up = up;
        isColored = false;

        paint = new Paint();
    }

    public int getBottom() {
        return bottom;
    }

    public int getRight() {
        return right;
    }

    public int getLeft() {
        return left;
    }

    public int getUp() {
        return up;
    }

    public void setColor(int color) {
        this.color = color;

        if(color == Constants.DEFAULT_COLOR){
            isColored = false;
        }
        else
            isColored = true;
    }

    public int getColor() {
        return color;
    }

    public int getPosC() {
        return posC;
    }

    public int getPosR() {
        return posR;
    }

    public boolean isRep() {
        return isRep;
    }

    public void setRep(boolean rep) {
        isRep = rep;
    }

    public boolean isColored() {

        if(color==Constants.DEFAULT_COLOR)
            return false;
        else
            return true;
    }


    public void  invalidate(){
        isColored = false;
        this.color = Constants.DEFAULT_COLOR;
    }



//
//    public void draw(){
//        if (surfaceHolder.getSurface().isValid()) {
//
//            canvas = surfaceHolder.lockCanvas();
//
//            isColored = false;
//
//            paint.setColor(Constants.DEFAULT_COLOR);
//            paint.setStyle(Paint.Style.FILL);
//
//            canvas.drawRect(left, up, right, bottom, paint);
//
//            paint.setColor(Constants.STROKE_COLOR);
//            paint.setStrokeWidth(Constants.STROKE_WIDTH);
//            paint.setStyle(Paint.Style.STROKE);
//
//
//            canvas.drawRect(left, up, right, bottom, paint);
//
//            surfaceHolder.unlockCanvasAndPost(canvas);
//        }
//    }
//
//    public void draw(int color){
//
//        isColored = true;
//
//        paint.setColor(color);
//        paint.setStyle(Paint.Style.FILL);
//
//        canvas.drawRect(left,up,right,bottom, paint);
//
//        paint.setColor(Constants.STROKE_COLOR);
//        paint.setStrokeWidth(Constants.STROKE_WIDTH);
//        paint.setStyle(Paint.Style.STROKE);
//
//
//        canvas.drawRect(left,up,right,bottom, paint);
//
//    }



}

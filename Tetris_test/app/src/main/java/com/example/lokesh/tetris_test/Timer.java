package com.example.lokesh.tetris_test;

/**
 * Created by lokesh on 17/9/17.
 */

public class Timer  {

    public long runTime;
    public boolean isRuning =false;
    public long remainingTime;
    public boolean TimeOver = true;
    public long startedTime = 0 ;


    public Timer(long runTime){
        this.runTime = runTime;
    }

    public void start(){
        this.isRuning = true;
        this.startedTime = System.currentTimeMillis();
    }

    public long getRemainingTime(){
        long time = System.currentTimeMillis() - startedTime ;
        return this.runTime - time;
    }

}

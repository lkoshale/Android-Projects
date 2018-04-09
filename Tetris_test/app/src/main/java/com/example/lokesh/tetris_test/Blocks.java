package com.example.lokesh.tetris_test;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by lokesh on 9/9/17.
 */



//TODO there are bug in rotation mainly (unsolved is change of start and end row col as its rotated ) and lag in S rotation

public class Blocks {


    public State state;

    public List<Box> set = new ArrayList<>();
    public Box MID;
    public boolean setAvailable ;

    public int color;
    public int startRow;
    public int endRow;
    public int startCol;
    public int endCol;
    public int id;

    public boolean reached = false;

    public long timer = 500;


    public HashMap<Integer,Integer>map = new HashMap<>();

    private Thread gameThread ;


    public Blocks(State st,int colr,Thread th) {
        setAvailable = true;
        state =st;
        color = colr;
        this.gameThread = th;
       // oldtime = System.currentTimeMillis();
       DefaultRel();

    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void moveDown() {

     //   newtime = System.currentTimeMillis();

        if(endRow >= Constants.ROW -1 || reached ){
            Constants.setInComing(false);
            //pointCheck();
            try {
                gameThread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           int score = pointCheck();

            Constants.SCORE += score;

            return;
        }


        {

            List<Box> setDup = new ArrayList<>();

            for(Box bx : set){
                int r = bx.getPosR();
                int c = bx.getPosC();

//                if(r+1 >= Constants.ROW-2) {
//                    for(Box v : setDup)
//                        v.invalidate();
//
//                    return;
//                }

                Box next =  state.getBox(r+1,c);

                if(next==null) {
                    reached = true;
                    return;
                }

                if (set.contains(next)) {
                    next.isRep = true;
                }else if(next.isColored()){
                    reached = true;
                    return;
                }

            }

            for( int i=0;i<set.size();i++){
                Box b = set.get(i);
                int r = b.getPosR();
                int c = b.getPosC();

                Box b2 = state.getBox(r+1,c);

                b2.setColor(color);
                setDup.add(b2);

                if (b.isRep ==false){
                    b.invalidate();
                }

            }

            for(Box bs : setDup){
                bs.isRep = false;
            }

            startRow++;
            endRow++;

            MID = state.getBox(MID.getPosR()+1,MID.getPosC());

            set.clear();
            set = setDup;

            Log.e("In down ",startRow+" "+endRow+" "+color);

            //oldtime = newtime;

        }

    }

    public void rotate() {

        if(id == Constants.OBLOCK){

        }else if (id == Constants.IBLOCK){

            int i1 = this.MID.getPosR();
            int j1 = this.MID.getPosC();

            if(this.startCol==this.endCol) {

                if (j1 - 2 < 0 || j1 + 1 > Constants.COL - 1)
                    return;

                for (Box bx : set) {
                    bx.invalidate();
                }


                    Box b1 = state.getBox(i1, j1 - 2);
                    b1.setColor(color);
                    Box b2 = state.getBox(i1, j1 - 1);
                    b2.setColor(color);
                    Box b3 = state.getBox(i1, j1);
                    b3.setColor(color);
                    this.MID = b3;
                    Box b4 = state.getBox(i1, j1 + 1);
                    b4.setColor(color);

                    set.clear();
                    set.add(b1);
                    set.add(b2);
                    set.add(b3);
                    set.add(b4);


                this.startCol = j1 - 2;
                this.endCol = j1 + 1;
                this.startRow = i1;
                this.endRow = i1;

            }
            else if(this.startRow == this.endRow){
                if (i1+ 2 > Constants.ROW -1 || i1 - 1 < 0)
                    return;

                for (Box bx : set) {
                    bx.invalidate();
                }



                    Box b1 = state.getBox(i1-1, j1);
                    b1.setColor(color);
                    Box b2 = state.getBox(i1+1, j1);
                    b2.setColor(color);
                    Box b3 = state.getBox(i1, j1);
                    b3.setColor(color);
                    this.MID = b3;
                    Box b4 = state.getBox(i1+2, j1);
                    b4.setColor(color);

                    set.clear();
                    set.add(b1);
                    set.add(b2);
                    set.add(b3);
                    set.add(b4);



                this.startCol = j1;
                this.endCol = j1;
                this.startRow = i1-2;
                this.endRow = i1+1;


            }

        }
        else{

            List<Box> setDup = new ArrayList<>();
            HashMap<Integer,Box> hashMap = makeMat(MID);

            if(hashMap.size() < 9 || hashMap.isEmpty()){
                return;
            }

            for(int i=1;i<=9;i++){

                Box b1 = hashMap.get(i);

                if(i==5) {
                    setDup.add(b1);
                    continue;
                }

                for(Box bs : set){
                    bs.isRep = false;
                }

                if (set.contains(b1)==true) {

                    int r = map.get(i);
                    Log.e("Roatate ", i + " " + r);

                    Box b2 = hashMap.get(r);

                    if (set.contains(b2)) {
                        Log.e("Conatins ",b2.getPosR()+" "+b2.getPosC());
                        b2.isRep = true;
                        setDup.add(b2);

                    } else if (b2.isColored()) {
                        Log.e("Overide ",b2.getPosR()+" "+b2.getPosC());
                        return;
                    } else {
                        Log.e("new ",b2.getPosR()+" "+b2.getPosC());
                        b2.setColor(color);
                        setDup.add(b2);
                    }

                    if (!b1.isRep)
                         b1.invalidate();
                }
            }

            for(Box bs : setDup){
                bs.isRep = false;
            }


            set.clear();
            set = setDup;


            int mR = 1000;
            int maxR =-1;
            int mC = 1000;
            int maxC =-1;

            for(Box bx : set){

                if(maxC<bx.getPosC()) maxC = bx.getPosC();

                if(mC > bx.getPosC()) mC = bx.getPosC();

                if (mR > bx.getPosR()) mR = bx.getPosR();

                if (maxR < bx.getPosR()) maxR = bx.getPosR();

            }

            this.startRow = mR;
            this.endRow = maxR;
            this.startCol = mC;
            this.endCol = maxC;


        }


    }



    public void moveLR(int dir) {

        int shift = 0;
        if(dir == Constants.LEFT)
            shift = -1;
        else if(dir == Constants.RIGHT)
            shift = 1;

        if( (shift<0 && startCol > 0) || (shift>0 && endCol < Constants.COL -1) ){

            int s = set.size();
            List<Box> setDup = new ArrayList<>();

            for(Box bx : set){
                int r = bx.getPosR();
                int c = bx.getPosC();

                Box next =  state.getBox(r,c+shift);

                if (set.contains(next)) {
                    next.isRep = true;
                }else if(next.isColored()){
                    return;
                }

            }

            for( int i=0;i<set.size();i++){
                Box b = set.get(i);
                int r = b.getPosR();
                int c = b.getPosC();

                Box b2 = state.getBox(r,c+shift);

                b2.setColor(color);
                setDup.add(b2);

                if (b.isRep ==false){
                    b.invalidate();
                }

            }

            for(Box bs : setDup){
                bs.isRep = false;
            }

            MID = state.getBox(MID.getPosR(),MID.getPosC()+shift);

            startCol+=shift;
            endCol+=shift;

            set.clear();
            set = setDup;

            Log.e("In leftRight ",startCol+" "+endCol+" "+color);

        }

    }


    public static class Iblock extends Blocks {

        public Iblock(State st,int colr,Thread th){
            super(st,colr,th);
            setupBlock();
            id = Constants.IBLOCK;
        }

        public void setupBlock(){

            startRow = 0;
            endRow = 3;

            startCol = Constants.COL/2;
            endCol = Constants.COL/2;

            Box b1 = state.getBox(0,Constants.COL/2 );
            Box b2 = state.getBox(1,Constants.COL/2 );
            Box b3 = state.getBox(2,Constants.COL/2 );
            Box b4 = state.getBox(3,Constants.COL/2 );

            MID = b2;

            if( b1.isColored() || b2.isColored() || b3.isColored() || b4.isColored()){
                Constants.gameOver = true;
                return;
            }

            b1.setColor(color); b2.setColor(color);
            b3.setColor(color); b4.setColor(color);

            set.add(b1);set.add(b2);set.add(b3);set.add(b4);
            Log.e("Setup called:",set.size()+" ");
        }

    }

    public static class Oblock extends Blocks {
        public Oblock(State st,int colr,Thread th){
            super(st,colr,th);
            id = Constants.OBLOCK;
            setupBlock();

        }

        public void setupBlock(){

            startRow = 0;
            endRow = 1;

            startCol = Constants.COL/2 - 1;
            endCol = Constants.COL/2 ;

            Box b1 = state.getBox(0, (Constants.COL/2) - 1 );
            Box b2 = state.getBox(0, Constants.COL/2 );
            Box b3 = state.getBox(1,(Constants.COL/2) - 1 );
            Box b4 = state.getBox(1, Constants.COL/2 );

            if( b1.isColored() || b2.isColored() || b3.isColored() || b4.isColored()){
                Constants.gameOver = true;
                return;
            }

            b1.setColor(color); b2.setColor(color);
            b3.setColor(color); b4.setColor(color);

            MID = b1;

            set.add(b1);set.add(b2);set.add(b3);set.add(b4);
            Log.e("Setup called:",set.size()+" ");

        }

    }

    public static class Jblock extends Blocks {
        public Jblock(State st,int colr,Thread th){
            super(st,colr,th);
            id = Constants.JBLOCK;
            setupBlock();
        }

        public void setupBlock(){

            startRow = 0;
            endRow = 2;

            startCol = Constants.COL/2 -1;
            endCol = Constants.COL/2;

            Box b1 = state.getBox(0,Constants.COL/2 );
            Box b2 = state.getBox(1,Constants.COL/2 );
            Box b3 = state.getBox(2,Constants.COL/2 );
            Box b4 = state.getBox(2,Constants.COL/2 - 1);

            MID = b2;

            if( b1.isColored() || b2.isColored() || b3.isColored() || b4.isColored()){
                Constants.gameOver = true;
                return;
            }

            b1.setColor(color); b2.setColor(color);
            b3.setColor(color); b4.setColor(color);

            set.add(b1);set.add(b2);set.add(b3);set.add(b4);
            Log.e("Setup called:",set.size()+" ");
        }


    }

    public static class Lblock extends Blocks {
        public Lblock(State st,int colr,Thread th){
            super(st,colr,th);
            id = Constants.LBLOCK;
            setupBlock();
        }

        public void setupBlock(){

            startRow = 0;
            endRow = 2;

            startCol = Constants.COL/2;
            endCol = Constants.COL/2 +1;

            Box b1 = state.getBox(0,Constants.COL/2 );
            Box b2 = state.getBox(1,Constants.COL/2 );
            Box b3 = state.getBox(2,Constants.COL/2 );
            Box b4 = state.getBox(2,Constants.COL/2 +1 );

            MID = b2;

            if( b1.isColored() || b2.isColored() || b3.isColored() || b4.isColored()){
                Constants.gameOver = true;
                return;
            }

            b1.setColor(color); b2.setColor(color);
            b3.setColor(color); b4.setColor(color);

            set.add(b1);set.add(b2);set.add(b3);set.add(b4);
            Log.e("Setup called:",set.size()+" ");
        }

    }

    public static class Sblock extends Blocks {
        public Sblock(State st,int colr,Thread th){
            super(st,colr,th);
            id = Constants.SBLOCK;
            setupBlock();
        }

        public void setupBlock(){

            startRow = 0;
            endRow = 1;

            startCol = Constants.COL/2 - 1;
            endCol = Constants.COL/2 + 1 ;

            Box b1 = state.getBox(0,Constants.COL/2 );
            Box b2 = state.getBox(0,Constants.COL/2+1 );
            Box b3 = state.getBox(1,Constants.COL/2 );
            Box b4 = state.getBox(1,Constants.COL/2 - 1 );

            MID = b3;

            if( b1.isColored() || b2.isColored() || b3.isColored() || b4.isColored()){
                Constants.gameOver = true;
                return;
            }

            b1.setColor(color); b2.setColor(color);
            b3.setColor(color); b4.setColor(color);

            set.add(b1);set.add(b2);set.add(b3);set.add(b4);
            Log.e("Setup called:",set.size()+" ");
        }

    }

    public static class Zblock extends Blocks {
        public Zblock(State st,int colr,Thread th){
            super(st,colr,th);
            id = Constants.ZBLOCK;
            setupBlock();
        }

        public void setupBlock(){

            startRow = 0;
            endRow = 1;

            startCol = Constants.COL/2 - 1;
            endCol = Constants.COL/2 + 1 ;

            Box b1 = state.getBox(0,Constants.COL/2 - 1 );
            Box b2 = state.getBox(0,Constants.COL/2);
            Box b3 = state.getBox(1,Constants.COL/2 );
            Box b4 = state.getBox(1,Constants.COL/2 + 1 );

            MID = b3;

            if( b1.isColored() || b2.isColored() || b3.isColored() || b4.isColored()){
                Constants.gameOver = true;
                return;
            }

            b1.setColor(color); b2.setColor(color);
            b3.setColor(color); b4.setColor(color);

            set.add(b1);set.add(b2);set.add(b3);set.add(b4);
            Log.e("Setup called:",set.size()+" ");
        }

    }


    public void DefaultRel(){
        map.put(1,3);map.put(3,9);map.put(9,7);map.put(7,1);
        map.put(2,6);map.put(6,8);map.put(8,4);map.put(4,2);
        map.put(5,5);
    }


    public HashMap<Integer,Box> makeMat(Box mid){

        HashMap<Integer,Box> hashMap = new HashMap<>();

        int i = mid.getPosR();
        int j = mid.getPosC();

        if(i-1 < 0 || i+1 > Constants.ROW-1)
            return hashMap;

        if(j-1 < 0 || j+1 > Constants.COL-1)
            return hashMap;


        for(int k=i-1;k<=i+1;k++){
            for(int l =j-1;l<=j+1;l++) {
                Box b = state.getBox(k, l);
                if(b.isColored() && !set.contains(b)){
                    return hashMap;
                }
            }
        }

        int count = 1;
        for(int k=i-1;k<=i+1;k++){
            for(int l =j-1;l<=j+1;l++) {
                Box b = state.getBox(k, l);
                hashMap.put(count,b);
                count++;

            }

        }

        return hashMap;
    }


    public int pointCheck(){

        Stack<Integer> full = new Stack<>() ;

        for(int i=0;i<Constants.ROW;i++){

                boolean isColored = true;

                for( int j=0;j<Constants.COL;j++){

                    Box b = state.getBox(i,j);

                    if(!b.isColored()){
                        isColored = false;
                        break;
                    }

                }

                if (!isColored){
                    continue;
                }
                else{
                    full.push(i);
                }
        }

        int size = full.size();
        getDown(full);

        return size;
    }


    public void getDown( Stack<Integer> S){

        if(!S.empty()) {
            int R = S.pop();

            int curr = R ;
            int cpy = R-1;

            while( cpy >= 0) {

                if( S.contains(cpy) ){
                    cpy--;
                    continue;
                }

                for (int j = 0; j < Constants.COL; j++) {
                    Box b1 = state.getBox(cpy, j);
                    Box b2 = state.getBox(curr, j);

                    b2.setColor(b1.getColor());
                    b1.invalidate();
                }

                curr--;
                cpy--;
            }

        }

    }

}


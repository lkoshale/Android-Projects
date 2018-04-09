package com.example.lokesh.tetris_test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lokesh on 9/9/17.
 */

public class Fall {

    private State state;
    private List<Box> adj = new ArrayList<>();

    public Fall(State s){
        state = s;
    }

    public void addBox(Box box){
        adj.add(box);
    }



    public void moveDown(){

        Box below = null;
        for(Box b : adj){
            below = b;
            int i = b.getPosR();
            int j = b.getPosC();

            if(i<Constants.ROW-1) {
                int color = b.getColor();
                b.setColor(Constants.DEFAULT_COLOR);
                below = state.getBox(i + 1, j);
                below.setColor(color);

            }
        }
        adj.clear();
        adj.add(below);
    }

}

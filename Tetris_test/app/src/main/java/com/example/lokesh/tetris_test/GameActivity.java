package com.example.lokesh.tetris_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {


    private TetrisGame game ;
    Button Left, Right, Rotate;
    Intent intent;
    boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();

        String extra = intent.getStringExtra("FullScreen");

        if(extra.compareTo("true")==0){
            fullscreen = true;
        }else{
            fullscreen = false;
        }


        Display display = getWindowManager().getDefaultDisplay();

        //Getting the screen resolution into point object
        Point size = new Point();
        display.getSize(size);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Constants.HIGHSCORE = sharedPref.getInt(Constants.HIGH_SCORE,0);


        if(!fullscreen) {
            setContentView(R.layout.activity_game);
            game = (TetrisGame) findViewById(R.id.surface);
            game.intialize(GameActivity.this, 750, 1500,false);   //1080 1920


            Left = (Button)findViewById(R.id.butoonLeft);
            Right = (Button)findViewById(R.id.buttonRight);
            Rotate = (Button)findViewById(R.id.butoonRot);



            Left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    game.swipedLeft();
                }
            });

            Right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    game.swipedRight();
                }
            });

            Rotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    game.singleTap();
                }
            });

        }
        else{

            setContentView(R.layout.full_screen);
            game = (TetrisGame) findViewById(R.id.surface_full);
            game.intialize(GameActivity.this,size.x,size.y,true);

        }

        game.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this){

            public void onSwipeRight() {
                game.swipedRight();
                //   Toast.makeText(GameActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                game.swipedLeft();
                // Toast.makeText(GameActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                game.swipedBottom();
                //   Toast.makeText(GameActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            public void singleTap(){
                game.singleTap();
                //  Toast.makeText(GameActivity.this, "Tap", Toast.LENGTH_SHORT).show();
            }

        });



        Log.e("GAME : size ",size.x+" "+size.y);


    }


    @Override
    protected void onPause() {
        super.onPause();
        game.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.resume();
    }



}

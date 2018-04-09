package com.example.lokesh.tetris_test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private Button Play ,FullScreenPlay;
    private ImageButton Iplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Play = (Button)findViewById(R.id.buttonPlay);
        FullScreenPlay = (Button)findViewById(R.id.play_full_screen);
        Iplay = (ImageButton)findViewById(R.id.imageButtonPlay);



        Iplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.gameOver = false;
                Constants.SCORE = 0;
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                intent.putExtra("FullScreen","false");
                startActivity(intent);
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.gameOver = false;
                Constants.SCORE = 0;
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                intent.putExtra("FullScreen","false");
                startActivity(intent);
            }
        });

        FullScreenPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.gameOver = false;
                Constants.SCORE = 0;
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                intent.putExtra("FullScreen","true");
                startActivity(intent);
            }
        });

    }



}

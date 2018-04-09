package com.example.lokesh.tetris_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    Button mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        mainMenu = (Button)findViewById(R.id.mainMenu);
        TextView gameOver = (TextView)findViewById(R.id.text_game_over);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int HS = sharedPref.getInt(Constants.HIGH_SCORE,0);

        if(Constants.SCORE > HS) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.HIGH_SCORE, Constants.SCORE);
            editor.commit();
        }

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOver.this,MainActivity.class);
                startActivity(intent);
            }
        });

        gameOver.setText("YOUR SCORE : "+Constants.SCORE+"\n"+"\nGAME OVER");

    }
}

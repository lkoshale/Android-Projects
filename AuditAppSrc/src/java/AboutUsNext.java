package com.machadalo.audit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutUsNext extends AppCompatActivity {
    int value=0;
    TextView show;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_next);
        Intent mIntent = getIntent();
        Bundle extras = getIntent().getExtras();
        value = extras.getInt("Value");
        show=(TextView)findViewById(R.id.textView);
        if(value==0)
        {
           /* engquotes = getResources().getStringArray(R.array.EngLove);
            intValue = mIntent.getIntExtra("EngLove", 25);*/
            show.setText(R.string.terms_conditions);
            getSupportActionBar().setTitle("Terms & Conditions");
        }
        else if(value==1)
        {
          /*  engquotes = getResources().getStringArray(R.array.EngFriend);
            intValue = mIntent.getIntExtra("EngFriend", 25);*/
            show.setText(R.string.privacy_policy);
            getSupportActionBar().setTitle("Privacy Policy");
        }
        else if(value==2)
        {
          /*  engquotes = getResources().getStringArray(R.array.EngFamous);
            intValue = mIntent.getIntExtra("EngFamous", 25);*/
            show.setText("Machadalo");
        }
    }
}

package com.machadalo.audit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AboutUs extends ActionBarActivity {
    String[] itemname = {
            "Terms & Conditions",
            "Privacy Policy",
            "Machadalo.com"
    };
    ListView list;
    ArrayAdapter<String> adapter;
    Toolbar toolbar;
    public int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        adapter = new ArrayAdapter<String>(this, R.layout.mylist, R.id.Itemname, itemname);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                pos = position;
                if (position == pos) {
                    int a = position;
                    Intent intent = new Intent("com.machadalo.audit.AboutUsNext");
                    intent.putExtra("Value", a);
                    startActivity(intent);
                }

            }
        });

    }
}

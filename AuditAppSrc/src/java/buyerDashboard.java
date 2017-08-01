package com.machadalo.audit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class buyerDashboard extends ActionBarActivity implements AdapterView.OnItemClickListener {

    //create the custom toolbar

    private Toolbar toolbar;
    String []menu;
    //variables to get the extra intent data
    String Name="";
    String email;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);
        //get data from previous activity
        Bundle name = getIntent().getExtras();
        Name = name.getString("name");
        email=name.getString("email");
        role=name.getString("role");
        //set the custom toolbar
        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("myDashboard(buyer)");
        ListView listView = (ListView) findViewById(R.id.drawerList1);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawer f = (NavigationDrawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        menu = getResources().getStringArray(R.array.menu_buyer);
        f.setUp(R.id.fragment_navigation_drawer,(DrawerLayout)findViewById(R.id.drawerLayout),toolbar,Name);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu));
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(menu[position].equals("profile")){
            Intent profileIntent = new Intent(this,profile.class);
            profileIntent.putExtra("name",Name);
            profileIntent.putExtra("email",email);
            profileIntent.putExtra("role",role);
            startActivity(profileIntent);
        }
        if(menu[position].equals("logout")){
            String[] prefvalue = {"null","null","null","false"};
            String[] prefname={"name","email","role","status"};
            manageScreens.saveToPrefrence(buyerDashboard.this,prefname,prefvalue);
            Intent logout = new Intent(this,LoginActivity.class);
            startActivity(logout);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seller_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

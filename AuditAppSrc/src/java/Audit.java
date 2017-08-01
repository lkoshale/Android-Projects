package com.machadalo.audit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Audit extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
    }
    public void assigned(View view){
        Intent i1= new Intent(Audit.this, AssignedActivity.class);
        i1.putExtra("STRING_I_NEED","To Reased Act");
        startActivity(i1);
    }
    public void released(View view){
        Intent i1= new Intent(Audit.this, NextAssignedAct.class);
        i1.putExtra("STRING_I_NEED","Released Act");
        startActivity(i1);
    }
    public void submitted(View view){
        Intent i1= new Intent(Audit.this, SubmittedAuditor.class);
        i1.putExtra("STRING_I_NEED","Submitted Act");
        startActivity(i1);
    }
    public void pending(View view){
        Intent i1= new Intent(Audit.this, NextAssignedAct.class);
        i1.putExtra("STRING_I_NEED","Pending Act");
        startActivity(i1);
    } 
}

package com.machadalo.audit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DemoBarcode extends AppCompatActivity {
    TextView  txtbarcode;
    String ID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_barcode);

        final Bundle idRec = getIntent().getExtras();
        ID = idRec.getString("barcode");

        txtbarcode = (TextView) findViewById(R.id.brcodeResult);
        txtbarcode.setText(ID);

        Button btnBarcode = (Button) findViewById(R.id.btnBarcode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(DemoBarcode.this);
                scanIntegrator.initiateScan();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        // call the parent
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            default:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanResult != null) {

                    if (scanResult.getContents().equals(ID)) {
                        Toast.makeText(DemoBarcode.this, "Code Scanned Successfully", Toast.LENGTH_SHORT).show();
                        Toast.makeText(DemoBarcode.this, "Code Matches with Ad inventory id", Toast.LENGTH_SHORT).show();
                        txtbarcode.setText("Barcode Result SUccessfull: " + scanResult.getContents());
                        txtbarcode.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        Toast.makeText(DemoBarcode.this, "Does not match", Toast.LENGTH_SHORT).show();
                        txtbarcode.setText("Scan Again");

                    }
                } else {
                    Toast.makeText(DemoBarcode.this, "Barcode does not exist: " + scanResult, Toast.LENGTH_SHORT).show();

                }


        }
    }
}

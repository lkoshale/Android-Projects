package com.machadalo.audit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class BarcodeScan extends AppCompatActivity {
    TextView txtbarcode;
    String scannedbarcode ;
    String URL_SUBMIT="http://machadalo.com/android/audit/media/submitBarcode.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        txtbarcode = (TextView) findViewById(R.id.brcodeResult);
        txtbarcode.setText("Scan Barcode");

        Button btnBarcode = (Button) findViewById(R.id.btnBarcode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(BarcodeScan.this);
                scanIntegrator.initiateScan();
            }
        });

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitBarcode(scannedbarcode);
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
                    scannedbarcode=  scanResult.getContents().toString();
                    txtbarcode.setText(scannedbarcode);

                } else {
                    Toast.makeText(BarcodeScan.this, "Error", Toast.LENGTH_SHORT).show();

                }


        }
    }

    private void submitBarcode(String... arg) {

        final String scanBarcode = arg[0];


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(BarcodeScan.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error;", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("scanBarcode", scanBarcode);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(BarcodeScan.this);
        requestQueue.add(stringRequest);

    }
}

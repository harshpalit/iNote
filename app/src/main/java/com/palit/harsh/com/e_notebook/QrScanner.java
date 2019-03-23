package com.palit.harsh.com.e_notebook;


import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrScanner extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        AlertDialog.Builder builder = new AlertDialog.Builder(QrScanner.this);
        builder.setTitle("Notebook Detail");

        final View view1 = getLayoutInflater().inflate(R.layout.dialog_book_name,null);

        builder.setView(view1);
        final Button b = view1.findViewById(R.id.submit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ed = view1.findViewById(R.id.notebook_name);


            }
        });

        builder.setCancelable(true);
        AlertDialog ad = builder.create();
        ad.show();

    }


}

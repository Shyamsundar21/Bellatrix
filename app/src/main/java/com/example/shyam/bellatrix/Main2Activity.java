package com.example.shyam.bellatrix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {

    Button buttonuser,buttonmunicipal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        buttonuser=findViewById(R.id.buttonuser);
        buttonmunicipal=findViewById(R.id.buttonmunicipal);
        buttonuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main2Activity.this,UserLogin.class);
                startActivity(intent);

            }
        });

        buttonmunicipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main2Activity.this,MunicipalLogin.class);
                startActivity(intent);

            }
        });


    }
}

package com.example.leonn.comunicate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AppFalse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_false);
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(AppFalse.this, LoginWindow.class);
        startActivity(i);
        finish();
    }



}

package com.example.cta_map;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class userLoginActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.user_login_activity);
        super.onCreate(savedInstanceState);


        TextView newUser = (TextView) findViewById(R.id.newUser);
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLoginActivity.this, newUserActivity.class);
                startActivity(intent);
            }
        });



    }
}

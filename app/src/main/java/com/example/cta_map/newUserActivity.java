package com.example.cta_map;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class newUserActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_user_activity);
        super.onCreate(savedInstanceState);

        EditText new_username = (EditText) findViewById(R.id.new_user_name);
        EditText new_email = (EditText) findViewById(R.id.new_email);
        EditText new_phone = (EditText) findViewById(R.id.new_phone);

        String username = new_username.getText().toString();
        String email = new_email.getText().toString();
        String phone = new_phone.getText().toString();












    }
}

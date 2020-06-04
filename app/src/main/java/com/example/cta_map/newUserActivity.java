package com.example.cta_map;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class newUserActivity extends AppCompatActivity {
    @SuppressLint({"WrongConstant", "ShowToast"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_user_activity);
        super.onCreate(savedInstanceState);

        final EditText new_username = (EditText) findViewById(R.id.new_user_name);
        final EditText new_email = (EditText) findViewById(R.id.new_email);
        final EditText new_phone = (EditText) findViewById(R.id.new_phone);
        final EditText new_bday = (EditText) findViewById(R.id.new_bday);
        Button create_account = (Button) findViewById(R.id.create_account_button);

        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());





        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = new_username.getText().toString();
                String email = new_email.getText().toString();
                String bday = new_bday.getText().toString();
                String phone = new_phone.getText().toString();

                Profile new_profile = new Profile(username, email, phone, bday);

                sqlite.add_user(new_profile);
                Toast.makeText(getApplicationContext(), "Added User!", 1).show();

            }
        });
















    }
}

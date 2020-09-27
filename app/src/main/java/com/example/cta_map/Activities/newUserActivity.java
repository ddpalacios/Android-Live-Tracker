package com.example.cta_map.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cta_map.DataBase.Profile;
import com.example.cta_map.R;

public class newUserActivity extends AppCompatActivity {
    @SuppressLint({"WrongConstant", "ShowToast"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_user_activity);
        super.onCreate(savedInstanceState);
        final EditText new_first= (EditText) findViewById(R.id.first_name);
        final EditText new_last = (EditText) findViewById(R.id.last_name);

        final EditText new_username = (EditText) findViewById(R.id.new_user_name);
        final EditText new_email = (EditText) findViewById(R.id.new_email);
        final EditText new_phone = (EditText) findViewById(R.id.new_phone);
        final EditText new_bday = (EditText) findViewById(R.id.new_bday);
        final EditText new_password = (EditText) findViewById(R.id.password_input);

        Button create_account = (Button) findViewById(R.id.create_account_button);

//        final DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());





        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first_name = new_first.getText().toString();
                String last_name = new_last.getText().toString();
                String username = new_username.getText().toString();
                String email = new_email.getText().toString();
                String bday = new_bday.getText().toString();
                String phone = new_phone.getText().toString();
                String new_pass = new_password.getText().toString();

                Profile new_profile = new Profile(first_name,last_name, username,email);
                new_profile.setBday(bday);
                new_profile.setPhone(phone);
                new_profile.setPassword(new_pass);




//                sqlite.add_user(new_profile);
                Toast.makeText(getApplicationContext(), "Added User!", 1).show();
                Intent intent = new Intent(newUserActivity.this, userLoginActivity.class);
                startActivity(intent);

            }
        });
















    }
}

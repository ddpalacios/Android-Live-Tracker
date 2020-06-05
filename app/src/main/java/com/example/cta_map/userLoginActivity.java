package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class userLoginActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.user_login_activity);
        super.onCreate(savedInstanceState);

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.UserPassword);

        Button login = (Button) findViewById(R.id.loginbutton);






        login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ShowToast", "WrongConstant"})
            @Override
            public void onClick(View v) {
                Intent ToStations = new Intent(userLoginActivity.this, mainactivity.class);

                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                String user_name = username.getText().toString();
                String pass = password.getText().toString();
                if (sqlite.find_profile(user_name, pass)){


                    ArrayList<String> record = sqlite.getRecord(user_name, pass);

                    Toast.makeText(getApplicationContext(), "Welcome " + record.get(1), 1).show();

                    Integer id = Integer.parseInt(record.get(0));

                    SharedPreferences.Editor editor = getSharedPreferences("User_Record", MODE_PRIVATE).edit();
                    editor.putInt("ProfileID", id);
                    editor.putString("first_name", record.get(1));
                    editor.putString("last_name", record.get(2));
                    editor.putString("user_name", record.get(3));
                    editor.putString("email", record.get(4));
                    editor.putString("bday", record.get(5));
                    editor.putString("phone", record.get(6));
                    editor.putString("pass", record.get(7));



                    editor.commit();


                    startActivity(ToStations);

                }else{
                    Toast.makeText(getApplicationContext(), "No Profile Found", 1).show();

                }

            }
        });


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

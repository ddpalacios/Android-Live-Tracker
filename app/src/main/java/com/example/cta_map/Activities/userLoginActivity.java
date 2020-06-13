package com.example.cta_map.Activities;

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

import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.DataBase.DatabaseHelper;
import com.example.cta_map.R;

import java.io.BufferedReader;
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
                Chicago_Transits chicago_transits = new Chicago_Transits();
                BufferedReader r = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_stations);
                BufferedReader r2 = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.train_line_stops);
                BufferedReader r3 = chicago_transits.setup_file_reader(getApplicationContext(), R.raw.main_stations);
                create_tables(r, r2, r3,false);
                DatabaseHelper sqlite = new DatabaseHelper(getApplicationContext());
                String user_name = username.getText().toString();
                String pass = password.getText().toString();
                if (sqlite.find_profile(user_name, pass)){
                    ArrayList<String> record = sqlite.get_table_record("User_info", "WHERE user_name = '"+user_name+"' AND password = '"+toHex(pass)+"'");
                    Toast.makeText(getApplicationContext(), "Welcome " + record.get(1), 1).show();
                    ToStations.putExtra("profile_id", record.get(0));

                    sqlite.close();
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

    public void create_tables(BufferedReader file, BufferedReader file2,BufferedReader file3, boolean create){
        Chicago_Transits chicago_transits = new Chicago_Transits();
        if (create) {
            chicago_transits.Create_TrainInfo_table(file, getApplicationContext());
            chicago_transits.create_line_stops_table(file2, getApplicationContext());
            chicago_transits.create_main_station_table(file3, getApplicationContext());
        }
    }


    private String toHex(String s){
        char[] ch = s.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char c : ch) {
            String hexString = Integer.toHexString(c);
            sb.append(hexString);
        }
        return sb.toString();
    }

}

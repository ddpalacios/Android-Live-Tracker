package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.opencsv.CSVReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

class Debugger{
    void ShowToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

    }
}






@SuppressLint("Registered")
public class mainactivity extends AppCompatActivity {

    public final String url = "https://lapi.transitchicago.com/api/1.0/ttpositions.aspx?key=94202b724e284d4eb8db9c5c5d074dcd&rt=red";
    private Button getData, closeConn, toMap, csv_reader;
    private TextView result;
    private Boolean openConnection = true;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);


        final Context context = getApplicationContext();
        super.onCreate(savedInstanceState);
        final Debugger debug = new Debugger();
        final WebScrapper webScrapper = new WebScrapper();



        result = (TextView) findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        getData = (Button) findViewById(R.id.getData);
        closeConn = (Button) findViewById(R.id.closeData);
        toMap = (Button) findViewById(R.id.ToMaps);
        csv_reader = (Button) findViewById(R.id.dataReader);


        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainactivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });

        final String[] tags = {"lat","lon","isApp", "nextStaNm"};
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnection = true;
                debug.ShowToast(context, "Extracting Content...");
//                webScrapper.Connect(url, true, tags);
                extract_train_content(url, debug);
            }
        });


        csv_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debug.ShowToast(context, "READING CSV...");
                InputStream is = getResources().openRawResource(R.raw.train_stations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                int cnt=0;
                while (true){
                    try {
                        if ((line = reader.readLine()) != null){
                            String[] tokens = line.split(",");
                             String station_name = tokens[3].toLowerCase();


                             if (station_name.equals("chicago")){
                                 cnt++;

                                 // coordinates parse
                                 String station_coord = tokens[16] +tokens[17];
                                 station_coord = station_coord.replace("(", "").replace(")", "");
                                 String[] split_cord = station_coord.split(" ");
                                 String lat = split_cord[0];
                                 String lon = split_cord[1];
                                Log.e("COORD", "LAT: "+lat+ " LON: "+ lon);





                             }


                        }else{
                            debug.ShowToast(context, cnt+" STATIONS FOUND.");
                            break;
                        }

                    }catch (IOException e){
                        e.printStackTrace();

                    }


                }



            }
        });


//        InputStream is = getResources().openRawResource(R.raw.train_stations);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//        String line;
//        int cnt=0;
//        while (true){
//            try {
//                if ((line = reader.readLine()) != null){
//                    String[] tokens = line.split(",");
//                    String station_name = tokens[3].toLowerCase();
//
//
//                }
//
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                break;
//            }
//
//
//        }




//        final ReadCSV csv_stations = new ReadCSV("train_stations.csv");
//        String station_cordinates = csv_stations.getStationCoord("Chicago", "red");
//        Log.d("Station Location", "Station located at: "+ csv_stations);





    }


    private void extract_train_content(final String url, final Debugger debugger){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (openConnection) {

                    try {
                        Document content = Jsoup.connect(url).get();
                        final Elements lat = content.select("lat");
                        final Elements lon = content.select("lon");
                        final Elements isApp = content.select("isApp");
                        final Elements dest = content.select("nextStaNm");

                        String latitude = lat.text();
                        String longtitude = lon.text();
                        String destination = dest.text();
                        String isApproaching = isApp.text();


                        final String[] lat_list = latitude.split(" ");
                        final String[] lon_list = longtitude.split(" ");
                        final String[] app_list = isApproaching.split(" ");
                        final String[] destiniation_station = destination.split(" ");

                        Log.d("Latititudes", Arrays.toString(lat_list));
                        Log.d("Longtitude", Arrays.toString(lon_list));
                        Log.d("Longtitude", Arrays.toString(app_list));


                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                if (result.getText().toString().isEmpty()){
                                    for (int i=0; i< lat_list.length; i++){

                                        String isApp = app_list[i];
                                        if (isApp.equals("1")){
                                            result.append("\n\nTrain# "+i+"  "+lat_list[i]+"   "+ lon_list[i]+"\t\tApproaching: "+ destiniation_station[i]);

                                        }
                                        else{
                                            result.append("\n\nTrain# "+i+"  "+lat_list[i]+"   "+ lon_list[i]+"\t\tNext Stop: "+ destiniation_station[i]);

                                        }

                                    }
                                }
                                else {
                                    result.setText(null);
                                    run();

                                }

                            }
                        });

                    } catch (IOException e) {
                        Log.d("Error", "Error in extracting");
                    }


                }
            }
        }).start();

        closeConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnection = false;


                debugger.ShowToast(getApplicationContext(), "Connection Closed!");
                Log.d("Connection Status", "Connection Closed");
            }
        });

    }




}


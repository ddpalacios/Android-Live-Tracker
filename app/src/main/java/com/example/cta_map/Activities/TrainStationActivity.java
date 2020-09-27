package com.example.cta_map.Activities;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.R;

public class TrainStationActivity  extends AppCompatActivity {
    Bundle bb; // Retrieve data from main screen
    ImageView main_image;
    RecyclerView recyclerView;
    String[] s1 = new String[]{"testing", "testin3", "testin3", "testin3", "testin3", "testin3", "testin3", "testin3" ,"testin3"};
    int myImage;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_station_activity);
        recyclerView = findViewById(R.id.recycler_view2);
//        MyAdapter2 myAdapter2 = new MyAdapter2(this, s1);
//        recyclerView.setAdapter(myAdapter2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));









    }
}
//        recyclerView = findViewById(R.id.recycler_view2);
//        getData();
//        setData();

//        MyAdapter2 myAdapter = new MyAdapter2(this, s1);
//        recyclerView.setAdapter(myAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


//        final ListView list = (ListView) findViewById(R.id.train_stops);
//       Database2 sqlite = new Database2(getApplicationContext());
//        bb = getIntent().getExtras();
//        assert bb != null;
//        final Integer position1 = bb.getInt("position");
//        final String station_type = bb.getString("target_station_type");
//        final String station_direction = bb.getString("train_direction");
//        final String main_station = bb.getString("main_station");
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view =super.getView(position, convertView, parent);
//                TextView textView=(TextView) view.findViewById(android.R.id.text1);
//                textView.setTextColor(Color.BLACK);
//
//                // Set the item text style to bold
//                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//
//                // Change the item text size
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
//                return view;
//            }
//        };
//        list.setAdapter(adapter);
//
//        ArrayList<String> line_stops = sqlite.get_column_values("line_stops_table", station_type);
//        if (station_type.equals("purple")){
//            line_stops.subList(9,18).clear();
//        }
//        for (String each_stop: line_stops){
//            arrayList.add(each_stop);
//            adapter.notifyDataSetChanged();
//        }
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                if (position1 == 0) {
//                    Intent intent = new Intent(TrainStationActivity.this, mainactivity.class);
//                    Database2 sqlite = new Database2(getApplicationContext());
//                    String target_station = String.valueOf(list.getItemAtPosition(position));
//                    String query = "SELECT station_id FROM cta_stops WHERE station_name = '"+target_station+"' AND "+station_type+" = 'true'";
//                    ArrayList<HashMap> record = sqlite.search_query(query);
//                    String station_id= (String) record.get(0).get("station_id");
//                    sqlite.addNewStation(target_station, station_type, Integer.parseInt(station_direction), Integer.parseInt(station_id));
//                    sqlite.close();
//                    startActivity(intent);
//
//                }else{
//                    Intent intent = new Intent(TrainStationActivity.this, TrainTrackingActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        sqlite.close();
//    }

//    private void getData(){
//        if (getIntent().hasExtra("myImage") && getIntent().hasExtra("data1")){
////            myImage = getIntent().getIntExtra("myImage", 1);
//
//
//
//        }else {
//            Log.e("No Data", "none");
//        }
//    }

//    private void setData(){


//        main_image.setImageResource(myImage);
//    }
//}

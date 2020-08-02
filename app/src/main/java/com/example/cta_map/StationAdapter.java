package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.cta_map.Activities.MapsActivity;
import com.example.cta_map.Activities.TrainTrackingActivity;
import com.example.cta_map.Activities.mainactivity;
import com.example.cta_map.DataBase.Database2;
import com.example.cta_map.Displayers.Chicago_Transits;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MyViewHolder> {
    Context context;
    ArrayList<Stations> stations;
    public StationAdapter(Context context, ArrayList<Stations> stations){
        this.stations = stations;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.row, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Stations station = this.stations.get(position);
        holder.title.setText(station.getName());
        final Database2 sqlite = new Database2(this.context);
        HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();
        TrainLineKeyCodes.put("red",R.drawable.red );
        TrainLineKeyCodes.put("blue", R.drawable.blue);
        TrainLineKeyCodes.put("brown", R.drawable.brown);
        TrainLineKeyCodes.put("green", R.drawable.green);
        TrainLineKeyCodes.put("orange", R.drawable.orange);
        TrainLineKeyCodes.put("pink", R.drawable.pink);
        TrainLineKeyCodes.put("purple", R.drawable.purple);
        TrainLineKeyCodes.put("yellow", R.drawable.yellow);
        Integer type = TrainLineKeyCodes.get(station.getType().replaceAll(" ", "").toLowerCase());
        final Context ctx = this.context;
//        Log.e("type", station.getType()+"");
        holder.image.setImageResource(type);

        if (station.getDir().equals("1")){
            holder.station_dir_btn.setText("N");
        }else{
            holder.station_dir_btn.setText("S");
        }

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, mainactivity.class);
                Log.e("TRACK", "Track clicked "+" Data: "+station.getName());
                String station_id = StringUtils.substringBetween(station.getName(), "#", ".");
                sqlite.DeleteRecentStation("id = ?", new String[]{station_id});
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

        holder.track_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, TrainTrackingActivity.class);
                intent.putExtra("position", 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String station_name = StringUtils.substringBetween(station.getName(), ". ", ".");
                String query1 = "SELECT station_id FROM cta_stops WHERE station_name = '" + station_name + "'" + " AND " + station.getType() + " = 'true'";
                String station_id = sqlite.getValue(query1);
                String main_query;
                if (station.getDir().equals("1")){
                    main_query = "SELECT northbound FROM main_stations WHERE main_station_type = '"+station.getType().toUpperCase().replaceAll(" ", "")+"'";

                }else{
                    main_query = "SELECT southbound1 FROM main_stations WHERE main_station_type = '"+station.getType().toUpperCase().replaceAll(" ", "")+"'";

                }




                Chicago_Transits chicago_transits = new Chicago_Transits();
                String[] station_coord = chicago_transits.retrieve_station_coordinates(sqlite, station_id);
                String main_station = sqlite.getValue(main_query);



                sqlite.add_tracking_station(station_name, station.getType(), station.getDir(), main_station, station_coord, station_id);


                ctx.startActivity(intent);

//                Log.e("TRACK", "Track clicked "+" Data: "+station.getName());
//                String station_id = StringUtils.substringBetween(station.getName(), "#", ".");
//                sqlite.DeleteRecentStation("WHERE id = ?", new String[]{station_id});



            }
        });
        holder.station_dir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DIR", "DIR clicked "+" Data: "+station.getName());
                if (station.getDir().equals("1")){
                    holder.station_dir_btn.setText("S");
                    station.setDir("5");
                    String station_id = StringUtils.substringBetween(station.getName(), "#", ".");
                    sqlite.update_fav_dir(station_id, "5");
                }else{
                    holder.station_dir_btn.setText("N");
                    station.setDir("1");
                    String station_id = StringUtils.substringBetween(station.getName(), "#", ".");
                    sqlite.update_fav_dir(station_id, "1");
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.stations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        Button track_btn, delete_btn;
        Button station_dir_btn;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.imageView3);
            station_dir_btn = (Button) itemView.findViewById(R.id.dir);
            track_btn = (Button) itemView.findViewById(R.id.track);
            delete_btn = (Button)itemView.findViewById(R.id.delete_btn);




        }

    }
}

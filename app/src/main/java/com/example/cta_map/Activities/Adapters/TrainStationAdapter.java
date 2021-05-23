package com.example.cta_map.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.ChooseStationActivity;
import com.example.cta_map.Activities.ChooseTrainLineActivity;
import com.example.cta_map.Activities.Classes.FavoriteStation;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.MainActivity;
import com.example.cta_map.Activities.NewAlarmSetUp;
import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.ListItem;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TrainStationAdapter extends RecyclerView.Adapter<TrainStationAdapter.ItemHolder>  {
    ArrayList<Station> contactsList;
    Context context;
    HashMap<String, String> tracking_station;
    public TrainStationAdapter(Context context, ArrayList<Station> contactsList, HashMap<String, String> tracking_station){
        this.contactsList = contactsList;
        this.context = context;
        this.tracking_station = tracking_station;
    }

    @NonNull
    @Override
    public TrainStationAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tracking_station_card_view, parent, false);

        return new ItemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull TrainStationAdapter.ItemHolder holder, int position) {
        final Station station = this.contactsList.get(position);
        Chicago_Transits chicago_transits = new  Chicago_Transits();
//        final float scale =this.context.getResources().getDisplayMetrics().density;
//        int pixels = (int) (150 * scale + 0.5f);
//        holder.t1.setWidth(pixels);
        holder.transfer_lines.setVisibility(View.VISIBLE);


        holder.t1.setText(station.getStation_name());
        holder.imageView.setImageResource(chicago_transits.getTrainImage(station.getStation_type()));
//        initializeTransferLines(station);
        ArrayList<String> transfer_lines = station.getStation_type_list();
        if (transfer_lines!= null){
            for (String line : transfer_lines){
                line = line.toLowerCase().trim();
//                if (line.equals(station.getStation_type().toLowerCase())){
//                    continue;
//                }
                if (line.equals("brown")){
                    holder.brn.setText("brown");
                    holder.brn.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("purple")){
                    holder.p.setText("purple");

                    holder.p.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("yellow")){
                    holder.y.setText("yellow");

                    holder.y.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("green")){
                    holder.g.setText("green");

                    holder.g.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("orange")){
                    holder.org.setText("orange");

                    holder.org.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("blue")){
                    holder.blue.setText("blue");
                    holder.blue.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("red")){
                    holder.red.setText("red");

                    holder.red.setTextColor(Color.parseColor(getColor(line)));
                }
                if (line.equals("pink")){
                    holder.pink.setText("pink");
                    holder.pink.setTextColor(Color.parseColor(getColor(line)));
                }

            }
            if (holder.org.getText().toString() == null || holder.org.getText().toString().equals("") ){
                holder.org.setVisibility(View.GONE);

            }
            if (holder.p.getText().toString() == null || holder.p.getText().toString().equals("") ){
                holder.p.setVisibility(View.GONE);

            }

            if (holder.y.getText().toString() == null || holder.y.getText().toString().equals("") ){
                holder.y.setVisibility(View.GONE);

            }

            if (holder.g.getText().toString() == null || holder.g.getText().toString().equals("") ){
                holder.g.setVisibility(View.GONE);

            }

            if (holder.pink.getText().toString() == null || holder.pink.getText().toString().equals("") ){
                holder.pink.setVisibility(View.GONE);

            }

            if (holder.brn.getText().toString() == null || holder.brn.getText().toString().equals("") ){
                holder.brn.setVisibility(View.GONE);

            }

            if (holder.blue.getText().toString() == null || holder.blue.getText().toString().equals("") ){
                holder.blue.setVisibility(View.GONE);

            }

            if (holder.red.getText().toString() == null || holder.red.getText().toString().equals("") ){
                holder.red.setVisibility(View.GONE);

            }

        }










        holder.item.setOnClickListener(v -> {
            this.tracking_station.put("target_station_name", station.getStation_name());
            this.tracking_station.put("target_map_id", station.getMap_id());


            if (ChooseTrainLineActivity.isNewAlarm){
                        Intent intent = new Intent(this.context, NewAlarmSetUp.class);
                        intent.putExtra("tracking_station", tracking_station);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.context.startActivity(intent);
                        return;

                    }


                    CTA_DataBase cta_dataBase = new CTA_DataBase(this.context);
                    FavoriteStation favoriteStation = new FavoriteStation(station.getMap_id(), station.getStation_name(), tracking_station.get("train_line").toLowerCase());
                    favoriteStation.setStation_dir_label(station.getDirection_id());
                    favoriteStation.setStation_dir(this.tracking_station.get("train_dir"));
                    favoriteStation.setTracking("0");
                    ArrayList<Object> userStation = cta_dataBase.excecuteQuery("*", "USER_FAVORITES", "FAVORITE_MAP_ID = '"+station.getMap_id()+"' AND STATION_DIR_LABEL = '"+station.getDirection_id()+"' AND STATION_TYPE = '"+this.tracking_station.get("train_line").toLowerCase()+"'",null,null);
                    Intent intent = new Intent(this.context, MainActivity.class);

                    if (userStation!=null){
                        HashMap<String, String> fav_station = (HashMap<String, String>) userStation.get(0);
                        if (Objects.equals(fav_station.get("STATION_DIR_LABEL"), station.getDirection_id())){
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.context.startActivity(intent);
                        }else{
                            cta_dataBase.commit(favoriteStation, "USER_FAVORITES");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.context.startActivity(intent);
                        }

                    }else{
                        cta_dataBase.commit(favoriteStation, "USER_FAVORITES");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.context.startActivity(intent);

                    }

        });
    }

    private void initializeTransferLines(Station station) {






    }


    private String getColor(String train_line){
        HashMap<String, String> TrainLineKeyCodes = new HashMap<>();
        TrainLineKeyCodes.put("red","#F44336");
        TrainLineKeyCodes.put("blue","#384cff");
        TrainLineKeyCodes.put("brown", "#a34700");
        TrainLineKeyCodes.put("green", "#0B8043");
        TrainLineKeyCodes.put("orange", "#ffad33");
        TrainLineKeyCodes.put("yellow", "#b4ba0b");
        TrainLineKeyCodes.put("pink","#ff66ed");
        TrainLineKeyCodes.put("purple","#673AB7");
        return TrainLineKeyCodes.get(train_line.toLowerCase().trim());

    }


    @Override
    public int getItemCount() {
        return this.contactsList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView t1, red, blue, g,y,brn,p,org,pink;
        LinearLayout transfer_lines;
        CardView item;
        ImageView imageView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
           p = itemView.findViewById(R.id.p);
           transfer_lines = itemView.findViewById(R.id.transfer_lines);
            pink = itemView.findViewById(R.id.pink);
            red= itemView.findViewById(R.id.red);
            blue = itemView.findViewById(R.id.blue);
            g = itemView.findViewById(R.id.g);
            y = itemView.findViewById(R.id.y);
            brn = itemView.findViewById(R.id.brn);
            org= itemView.findViewById(R.id.org);

            item = itemView.findViewById(R.id.list_item);
            t1 = (TextView) itemView.findViewById(R.id.title_item);
            imageView = (ImageView) itemView.findViewById(R.id.train_image);
        }
    }
}


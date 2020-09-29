package com.example.cta_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Backend.Threading.AllTrainsTable;
import com.example.cta_map.DataBase.CTA_DataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder> {
    ArrayList<Object> list;
    Context ctx;
    public TrackingAdapter(Context ctx, ArrayList<Object> list){
        this.ctx = ctx;
        this.list = list;
    }


    @NonNull
    @Override
    public TrackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.ctx);
        View view = inflater.inflate(R.layout.tracking_row, parent, false);

        return new TrackingAdapter.TrackingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull TrackingViewHolder holder, int position) {
        HashMap<String, String> train = (HashMap<String, String>) this.list.get(position);

        try {


        holder.min.setText(train.get("to_target_eta")+"m");
        CTA_DataBase cta_dataBase = new CTA_DataBase(this.ctx);
        ArrayList<Object> cta_record = cta_dataBase.excecuteQuery("*", "cta_stops", "MAP_ID = '"+ train.get("next_stop_id").trim()+"'", null);
        HashMap<String, String> next_stop_station_record = (HashMap<String, String>) cta_record.get(0);
        holder.title.setText(next_stop_station_record.get("station_name"));
//        Log.e("fff", train.getName()+"fff");
//        Log.e("fff", train.getEta()+"fff");

//        holder.title.setText(train.getName()+"");
//        holder.min.setText(train.getEta()+"m");
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class TrackingViewHolder extends RecyclerView.ViewHolder {
    TextView title, min;

    public TrackingViewHolder(@NonNull View itemView){
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.target_station_name);
        min = (TextView) itemView.findViewById(R.id.target_station_eta);





    }

}
}

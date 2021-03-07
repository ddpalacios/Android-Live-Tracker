package com.example.cta_map.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void onBindViewHolder(@NonNull final TrackingViewHolder holder, int position) {
        final Train train = (Train) this.list.get(position);

        try {



            HashMap<String, Integer> TrainLineKeyCodes  = new HashMap<>();
            TrainLineKeyCodes.put("red",R.drawable.red );
            TrainLineKeyCodes.put("blue", R.drawable.blue);
            TrainLineKeyCodes.put("brown", R.drawable.brown);
            TrainLineKeyCodes.put("green", R.drawable.green);
            TrainLineKeyCodes.put("orange", R.drawable.orange);
            TrainLineKeyCodes.put("pink", R.drawable.pink);
            TrainLineKeyCodes.put("purple", R.drawable.purple);
            TrainLineKeyCodes.put("yellow", R.drawable.yellow);

//        holder.title.setTextColor(Color.RED);
            holder.image.setImageResource(TrainLineKeyCodes.get(train.getTrain_type()));
            holder.id.setText("Train #"+train.getRn());
        holder.title.setText(train.getNextStaNm()+"");
        holder.min.setText(train.getTarget_eta()+"m");
        final Context finalContext[] = {this.ctx};
        holder.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("SELECTION RV", train.getRn()+" RN "+ train.getStatus());
                Toast.makeText(finalContext[0], train.getRn()+ " Status: "+ train.getStatus(), Toast.LENGTH_SHORT).show();
            }
        });




//            notifyDataSetChanged();

//
//            holder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    index=position;
//                    notifyDataSetChanged();
//                }
//            });
//            if(index==position){
//                holder.linearlayout.setBackgroundColor(Color.parseColor("#FFEB3B"));
//                holder.tv.setTextColor(Color.parseColor("#ffffff"));
//            }
//            else
//            {
//                holder.linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
//                holder.tv.setTextColor(Color.parseColor("#6200EA"));
//            }




        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class TrackingViewHolder extends RecyclerView.ViewHolder {
    TextView title, min, id;
    ImageView image;
    ConstraintLayout layout;

    public TrackingViewHolder(@NonNull View itemView){
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.trainImage);
        id = (TextView) itemView.findViewById(R.id.target_station_id);
        title = (TextView) itemView.findViewById(R.id.target_station_name);
        min = (TextView) itemView.findViewById(R.id.target_station_eta);
        layout = (ConstraintLayout) itemView.findViewById(R.id.tracking_row_layout);





    }

}
}

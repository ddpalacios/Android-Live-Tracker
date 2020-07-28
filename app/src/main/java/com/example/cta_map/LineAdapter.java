package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineViewHolder>{
    ArrayList<StationLines> lines;
    Context context;
    public LineAdapter(Context context, ArrayList<StationLines> lines){
        this.lines = lines;
        this.context = context;
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.linelist, parent, false);

        return new LineAdapter.LineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
            final StationLines lines = this.lines.get(position);
            holder.t1.setText(lines.getLine());
            holder.img1.setImageResource(lines.getColor());
            final Context ctx = this.context;
            holder.t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, ChooseDir.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("color", lines.getColor());
                    intent.putExtra("color_name", lines.getLine());

                    ctx.startActivity(intent);

                }
            });

    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class LineViewHolder extends RecyclerView.ViewHolder {
        TextView t1;
        ImageView img1;
        public LineViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.train_line);
            img1 = (ImageView) itemView.findViewById(R.id.train_image);


        }
    }
}

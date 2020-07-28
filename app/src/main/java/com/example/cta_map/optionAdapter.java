package com.example.cta_map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.MapsActivity;

public class optionAdapter extends RecyclerView.Adapter<optionAdapter.OptionViewHolder> {
    Context context;
    String[] options;

    public optionAdapter(Context context, String[] options){
        this.context = context;
        this.options = options;
    }


    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.top_row, parent, false);

        return new optionAdapter.OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, final int position) {
        final String option = this.options[position];
        holder.t1.setText(option);
        final Context context = this.context;
            holder.t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if (position == 0 || position == 1 ){
                        intent = new Intent(context, ChooseLine.class);

                    }else {
                        intent = new Intent(context, MapsActivity.class);

                    }


                    intent.putExtra("position", position);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.e("Clicked", position+" is clicked");
                    context.startActivity(intent);
                }
            });








    }

    @Override
    public int getItemCount() {
        return this.options.length;
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView t1;
        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.option_txt);
        }
    }
}

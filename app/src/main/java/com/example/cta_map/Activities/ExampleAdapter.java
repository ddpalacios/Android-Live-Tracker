package com.example.cta_map.Activities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExampleAdapter extends CursorAdapter {
    private TextView text;
    public ExampleAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.searchview_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String body = cursor.getString(cursor.getColumnIndexOrThrow("STOP_NAME"));
        String priority = cursor.getString(cursor.getColumnIndexOrThrow("STOP_ID"));
        // Populate fields with extracted properties


//        int pos = cursor.getPosition();
        text = (TextView) view.findViewById(R.id.item);
//        if (items != null && pos < items.size()){
//            HashMap<String, String> txt = items.get(pos);
//        "#"+txt.get("STOP_ID")+". "+txt.get("STOP_NAME"
            text.setText("#"+priority + ". "+body);
//
//        }

    }
}

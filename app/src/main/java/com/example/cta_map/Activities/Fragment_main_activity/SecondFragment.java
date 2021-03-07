package com.example.cta_map.Activities.Fragment_main_activity;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cta_map.R;

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.second_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.textView1);
        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static SecondFragment newInstance(String text) {

        SecondFragment  f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
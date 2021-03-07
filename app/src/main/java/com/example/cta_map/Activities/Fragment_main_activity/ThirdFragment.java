package com.example.cta_map.Activities.Fragment_main_activity;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cta_map.R;

public class ThirdFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.third_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.textView1);
        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static ThirdFragment newInstance(String text) {

        ThirdFragment  f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
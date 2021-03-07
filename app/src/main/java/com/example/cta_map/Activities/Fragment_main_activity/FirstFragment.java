package com.example.cta_map.Activities.Fragment_main_activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cta_map.R;

public class FirstFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.first_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.textView1);
        tv.setText(getArguments().getString("msg"));
        return v;

    }

    public static FirstFragment newInstance(String text){
        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}

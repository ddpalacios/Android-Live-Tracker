package com.example.cta_map;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public interface TrainDirection {
    public void setup_train_direction(HashMap<String, String> current_train_info, ArrayList<String> stops, int start, int end, int dir, Context context);

}

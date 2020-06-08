package com.example.cta_map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
public class ExampleHandler extends Handler {

    public static final String TAG = "ExampleHandler";
    public static final int TASK_A = 1;
    public static final int TASK_B = 2;
    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();

        String[] train_list = bundle.getStringArray("raw_train_content");
        for (String f : train_list) {

            Log.e(TAG, f + " Recieved!");
        }
    }
}
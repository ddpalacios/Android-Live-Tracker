package com.example.cta_map.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.cta_map.R;

public class PopUp  extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_menu);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));





    }

}

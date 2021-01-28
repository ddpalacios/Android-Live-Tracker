package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cta_map.DataBase.CTA_DataBase;
import com.example.cta_map.Displayers.Chicago_Transits;
import com.example.cta_map.Displayers.Train;
import com.example.cta_map.R;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Content_Parser_ThreadTest extends TestCase {


    public void testChoose_trains() throws InterruptedException, ParseException {
        String north_dir = "1";
        String south_dir = "5";
        String target_name = "Belmont";
        String target_type = "red";
        Chicago_Transits chicago_transits = new Chicago_Transits();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Content_Parser_Thread content_parser_thread = new Content_Parser_Thread(context, null, null);
        API_Caller_Thread api_caller_thread = new API_Caller_Thread(null, target_type);
        CTA_DataBase cta_dataBase = new CTA_DataBase(context);
        HashMap<String, String> TARGET_RECORD  = (HashMap<String, String>) cta_dataBase.excecuteQuery("*", "CTA_STOPS", "STATION_NAME = '"+target_name+"' AND "+chicago_transits.TrainLineKeys(target_type).toUpperCase() +" = '1'", null).get(0);
        String MAP_ID = TARGET_RECORD.get("MAP_ID");


        for (int i = 0; i < 100; i++) {
            ArrayList<Train> incoming_trains = api_caller_thread.call_cta_rest(target_type); // All Trains
            ArrayList<Train> chosen_trains_north = content_parser_thread.choose_trains(context, incoming_trains, north_dir, target_name, target_type); // Valid North
            ArrayList<Train> chosen_trains_south = content_parser_thread.choose_trains(context, incoming_trains, south_dir, target_name, target_type); // Valid South

            BufferedReader file3Buffer = chicago_transits.setup_file_reader(context, R.raw.line_stops);
            ArrayList<String> ordered_stops = chicago_transits.create_line_stops_table(file3Buffer, context, target_type);
            int target_idx = ordered_stops.indexOf(target_name);

            List<String> left_sublist = ordered_stops.subList(target_idx, ordered_stops.size());
            for (Train train : chosen_trains_north) {
                assertEquals(north_dir, train.getTrDr());
                assertTrue(left_sublist.contains(train.getNextStaNm()));
            } // Assertion


            List<String> right_sublist = ordered_stops.subList(0, target_idx + 1);

            for (Train train : chosen_trains_south) {
                assertEquals(south_dir, train.getTrDr());
                assertTrue(right_sublist.contains(train.getNextStaNm()));
            } // Assertion


            Train north_train  = chosen_trains_north.get(0);
            Log.e("NORTH", north_train.getTarget_eta()+" "+ north_train.getNextStaNm()+" . (North)");
            if (north_train.getIsApp().equals("1") && north_train.getNextStaId().equals(MAP_ID)){
                Log.e("NORTH STATUS", "RED");
                Log.e("NORTH", " IS APP. "+ north_train.getNextStaNm());
            }
            int next_stop_idx = ordered_stops.indexOf(north_train.getNextStaNm());
            List<String> remaining_stops_north = ordered_stops.subList(target_idx, next_stop_idx+1);

            if (remaining_stops_north.size() <=1){
                Log.e("NORTH STATUS", "RED");
            }
            if (remaining_stops_north.size() == 2){
                Log.e("NORTH STATUS", "YELLOW");

            }else if (remaining_stops_north.size() >= 3) {
                Log.e("NORTH STATUS", "GREEN");
            }

            Train south_train  = chosen_trains_south.get(0);
            Log.e("SOUTH", south_train.getTarget_eta()+" "  +south_train.getNextStaNm()+" . (South)");
            if (south_train.getIsApp().equals("1") && south_train.getNextStaId().equals(MAP_ID)){
                Log.e("SOUTH STATUS", "RED");
                Log.e("Remaining Stops", " IS APP. "+ south_train.getNextStaNm());
                continue;
            }
            int next_stop_idx_south = ordered_stops.indexOf(south_train.getNextStaNm());
            List<String> remaining_stops_south = ordered_stops.subList(next_stop_idx_south,target_idx+1);

            if (remaining_stops_south.size() <=1){
                Log.e("SOUTH STATUS", "RED");

            }
            if (remaining_stops_south.size() ==2){
                Log.e("SOUTH STATUS", "YELLOW");

            }else if (remaining_stops_south.size() >= 3) {
                Log.e("SOUTH STATUS", "GREEN");
            }

            Log.e("DONE", "\nDONE\n");


            Thread.sleep(10000);

        }
    }
}

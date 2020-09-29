package com.example.cta_map.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

import com.example.cta_map.Backend.Threading.AllTrainsTable;

import java.util.ArrayList;
import java.util.HashMap;

public class CTA_DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CTA_DATABASE";
    private static final int DATABASE_VERSION = 5;

    public static final String ALL_TRAINS_TABLE = "all_trains_table";
    public static final String TRAIN_ID = "train_id";
    public static final String IS_NOTIFIED = "isNotified";
    public static final String PRED_ARRIVAL_TIME = "pred_arrival_time";
    public static final String NEXT_STOP_ID = "next_stop_id";
    public static final String NEXT_STOP_ETA = "next_stop_eta";
    public static final String NEXT_STOP_DISTANCE = "next_stop_distance";
    public static final String IS_DELAYED = "isdelayed";
    public static final String IS_APPROACHING = "isApproaching";
    public static final String DISTANCE_TO_TARGET = "distance_to_target";
    public static final String TRAIN_LAT = "train_lat";
    public static final String TRAIN_LON = "train_lon";
    public static final String TO_TARGET_TRAIN_ETA = "to_target_eta";
    public static final String TRACKING_TYPE = "tracking_type";
    public static final String TARGET_ID = "target_id";
    public static final String TRAIN_DIR = "train_dir";

    public static final String LINE_STOPS_TABLE = "line_stops_table";
    public static final String STOP_ID = "STOPID"; // auto
    public static final String GREEN_LINE_COL = "green";
    public static final String RED_LINE_COL = "red";
    public static final String BLUE_LINE_COL = "blue";
    public static final String YELLOW_LINE_COL = "yellow";
    public static final String PINK_LINE_COL = "pink";
    public static final String ORANGE_LINE_COL = "orange";
    public static final String BROWN_LINE_COL = "brown";
    public static final String PURPLE_LINE_COL = "purple";

    public static final String MAIN_STATIONS = "main_stations";
    public static final String MAIN_STATION_TYPE = "main_station_type";
    public static final String NORTHBOUND = "northbound";
    public static final String SOUTHBOUND = "southbound";


    public static final String CTA_STOPS_TABLE = "cta_stops";
    public static final String MAP_ID = "MAP_ID";
    public static final String STATION_NAME = "station_name";
    public static final String RED_COL = "red";
    public static final String BLUE_COL = "blue";
    public static final String GREEN_COL = "green";
    public static final String BROWN_COL = "brown";
    public static final String YELLOW_COL = "yellow";
    public static final String PURPLE_COL = "purple";
    public static final String PINK_COL = "pink";
    public static final String ORANGE_COL = "orange";
    public static final String LOCATION = "location";


    public CTA_DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create_main_stations(db);
        create_cta_stops(db);
        create_all_trains_table(db);
        create_line_stops_table(db);
        Log.e(Thread.currentThread().getName(), "CREATED TABLES");
    }

    public void testfunc(){
        Log.e(Thread.currentThread().getName(), "Testing on create");
    }
   public void  create_line_stops_table(SQLiteDatabase db){
        String line_stops_table = "CREATE TABLE IF NOT EXISTS " +LINE_STOPS_TABLE + " ( "
                + STOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GREEN_LINE_COL + " TEXT, "
                + RED_LINE_COL + " TEXT, "
                + BLUE_LINE_COL + " TEXT, "
                + YELLOW_LINE_COL + " TEXT, "
                + PINK_LINE_COL +" TEXT, "
                + ORANGE_LINE_COL + " TEXT, "
                + BROWN_LINE_COL + " TEXT, "
                + PURPLE_LINE_COL + " TEXT)";


        db.execSQL(line_stops_table);
    }
   public void create_all_trains_table(SQLiteDatabase db){
       String all_trains_table = "CREATE TABLE IF NOT EXISTS "+ALL_TRAINS_TABLE+" ( "
               + TRAIN_ID + " TEXT PRIMARY KEY, "
               + IS_NOTIFIED + " INTEGER, "
               + PRED_ARRIVAL_TIME + " TEXT, "
               + NEXT_STOP_ID + " TEXT, "
               + NEXT_STOP_ETA + " TEXT, "
               + NEXT_STOP_DISTANCE + " TEXT, "
               + IS_DELAYED + " TEXT, "
               + IS_APPROACHING + " TEXT, "
               + DISTANCE_TO_TARGET + " TEXT, "
               + TO_TARGET_TRAIN_ETA + " INTEGER, "
               + TRACKING_TYPE + " TEXT, "
               + TRAIN_LAT + " REAL, "
               + TRAIN_LON + " REAL,"
               + TARGET_ID+ " INTEGER, "
               +TRAIN_DIR+" INTEGER)";

       db.execSQL(all_trains_table);
   }
    public void create_cta_stops(SQLiteDatabase db){
       String cta_stops = "CREATE TABLE IF NOT EXISTS "+CTA_STOPS_TABLE +
               "("+ MAP_ID +" TEXT PRIMARY KEY, " +
               STATION_NAME+" TEXT, " +
               RED_COL+" TEXT, " +
               BLUE_COL+" TEXT, " +
               GREEN_COL+" TEXT, " +
               BROWN_COL+" TEXT, " +
               PURPLE_COL+" TEXT, " +
               YELLOW_COL+" TEXT, " +
               PINK_COL+" TEXT, " +
               ORANGE_COL+" TEXT, " +
               LOCATION+" TEXT)";
        Log.e(Thread.currentThread().getName(), "Done");
       db.execSQL(cta_stops);

   }
    public void add_cta_stations_to_cta_table(CTA_Stations cta_data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATION_NAME, cta_data.getStation_name());
        values.put(MAP_ID, cta_data.getID());
        values.put(RED_COL, cta_data.getRed());
        values.put(BLUE_COL, cta_data.getBlue());
        values.put(GREEN_COL, cta_data.getGreen());
        values.put(BROWN_COL, cta_data.getBrown());
        values.put(PURPLE_COL, cta_data.getPurple());
        values.put(YELLOW_COL, cta_data.getYellow());
        values.put(PINK_COL, cta_data.getPink());
        values.put(ORANGE_COL, cta_data.getOrange());
        values.put(LOCATION, cta_data.getCoordinates().get("lat")+","+cta_data.getCoordinates().get("lon"));

        db.insert(CTA_STOPS_TABLE, null, values);
        db.close();

    }
    public void addMainStations_to_mainStationTable(MainStation mainStation){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MAIN_STATION_TYPE, mainStation.getTrainLine());
        values.put(NORTHBOUND, mainStation.getNorthBound());
        values.put(SOUTHBOUND, mainStation.getSouthBound());
        db.insert(MAIN_STATIONS, null, values);
        db.close();


    }
    public void add_station_lines_to_line_stops_table(CTA_Stations cta_data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RED_COL, cta_data.getRed());
        values.put(BLUE_COL, cta_data.getBlue());
        values.put(GREEN_COL, cta_data.getGreen());
        values.put(BROWN_COL, cta_data.getBrown());
        values.put(PURPLE_COL, cta_data.getPurple());
        values.put(YELLOW_COL, cta_data.getYellow());
        values.put(PINK_COL, cta_data.getPink());
        values.put(ORANGE_COL, cta_data.getOrange());
        db.insert(LINE_STOPS_TABLE, null,values);

        db.close();
    }
    public void CommitRecordToAllTrainsTable(AllTrainsTable new_train){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TRAIN_ID, new_train.getTrain_id());
        cv.put(IS_NOTIFIED, new_train.isNotified()+"");
        cv.put(PRED_ARRIVAL_TIME, new_train.getPred_arrival_time());
        cv.put(NEXT_STOP_ID, new_train.getNext_stop());
        cv.put(NEXT_STOP_ETA, new_train.getNext_stop_eta()+"");
        cv.put(NEXT_STOP_DISTANCE, new_train.getNext_stop_distance()+"");
        cv.put(IS_DELAYED, new_train.isDelayed()+"");
        cv.put(IS_APPROACHING, new_train.isApproaching()+"");
        cv.put(DISTANCE_TO_TARGET, new_train.getDistance_to_target());
        cv.put(TO_TARGET_TRAIN_ETA, new_train.getTo_target_eta()+"");
        cv.put(TRAIN_LAT, new_train.getTrain_lat()+"");
        cv.put(TRAIN_LON, new_train.getTrain_lon()+"");
        cv.put(TRACKING_TYPE, new_train.getTracking_type());
        cv.put(TARGET_ID, new_train.getTarget_id());
        cv.put(TRAIN_DIR, new_train.getTrain_dir()+"");

        db.insert(ALL_TRAINS_TABLE, null, cv);
        db.close();
    }
    public void create_main_stations(SQLiteDatabase db){

        String main_stations = "CREATE TABLE IF NOT EXISTS "+MAIN_STATIONS+
                "("+MAIN_STATION_TYPE +" TEXT PRIMARY KEY, "+
                NORTHBOUND+" INTEGER," +
                SOUTHBOUND+" INTEGER)";


        Log.e(Thread.currentThread().getName(), "DONE Created");


        db.execSQL(main_stations);
    }

    public void deleteTable(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from " + table_name);
        }catch (Exception e){e.printStackTrace();}

    }

    public int retrieve_and_delete_all_records(String table_name){
        ArrayList<Object> all_trains_table = excecuteQuery("*", table_name, null, null);
        if (all_trains_table != null){
            deleteTable(table_name);
            Log.e("SQLITE", "ALL TRAINS TABLE RECORDS WAS DELETED");
            return 1;
        }else{
            Log.e("SQLITE", "NO RECORDS AVAILABLE");
            return -1;

        }
    }





    private ArrayList<Object> getRecord(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Object> result = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        HashMap<String, String> record = new HashMap<>();
                        try {
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                record.put(cursor.getColumnNames()[i], cursor.getString(i));
                            }
                            result.add(record);
                            cursor.moveToNext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                return result;
            }
        return null;
    }
    public ArrayList<Object> excecuteQuery(String cols,String table_name, String condition, String col_orderBy){
            String query;
        ArrayList<Object> record = null;
            if (condition == null){
                query = "SELECT "+cols+" FROM "+table_name;
            }else{
                query = "SELECT "+cols+" FROM "+table_name +" WHERE "+condition;
            }
            if (col_orderBy == null){
                record = getRecord(query);

            }else{
                query = query +" ORDER BY "+ col_orderBy + " ASC";
                record = getRecord(query);
            }




        return record;

}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

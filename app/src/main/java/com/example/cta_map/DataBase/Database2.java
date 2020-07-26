package com.example.cta_map.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Database2 extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "CTA_DataBase.db";



    public static final String MAIN_STATIONS = "main_stations";
    public static final String MAIN_STATION_TYPE = "main_station_type";
    public static final String NORTHBOUND = "northbound";
    public static final String SOUTHBOUND1 = "southbound1";
    public static final String SOUTHBOUND2 = "southbound2";


    public static final String LINE_STOPS_TABLE = "line_stops_table";
    public static final String STOP_ID = "STOPID";
    public static final String GREEN_LINE_COL = "green";
    public static final String RED_LINE_COL = "red";
    public static final String BLUE_LINE_COL = "blue";
    public static final String YELLOW_LINE_COL = "yellow";
    public static final String PINK_LINE_COL = "pink";
    public static final String ORANGE_LINE_COL = "orange";
    public static final String BROWN_LINE_COL = "brown";
    public static final String PURPLE_LINE_COL = "purple";




    public static final String TRAIN_STATION_TABLE = "cta_stops";
    public static final String STATION_NAME = "STATION_NAME".toLowerCase();
    public static final String RED_COL = "red";
    public static final String BLUE_COL = "blue";
    public static final String GREEN_COL = "green";
    public static final String BROWN_COL = "brown";
    public static final String YELLOW_COL = "yellow";
    public static final String PURPLE_COL = "purple";
    public static final String PINK_COL = "pink";
    public static final String ORANGE_COL = "orange";
    public static final String LATITUDE_COL = "latitude";
    public static final String LONGITUDE_COL = "longitude";


    public static final String FAVORITE_STATIONS = "favorite_stations";
    public static final String ID = "id";
    public static final String FAV_STATION_NAME = "FAV_STATION_NAME".toLowerCase();
    public static final String FAV_STATION_TYPE = "FAV_STATION_TYPE".toLowerCase();
    public static final String CHOSEN_DIRECTION = "CHOSEN_DIRECTION".toLowerCase();
    public static final String STATION_ID = "STATION_ID".toLowerCase();

    public static final String TRACKING_TABLE = "tracking_table";
    public static final String TRACKING_ID = "TRACKING_ID";
    public static final String TRACKING_TYPE_COL = "station_type";
    public static final String TRACKING_NAME_COL = "station_name";
    public static final String TRACKING_LAT_COL = "station_lat";
    public static final String TRACKING_LON_COL = "station_lon";
    public static final String TRACKING_DIR_COL = "station_dir";
    public static final String TRACKING_STATION_ID = "station_id";
    public static final String TRACKING_MAIN_COL = "main_station_name";

    public static final String USERLOCATION  = "userLocation_table";
    public static final String LOCATION_ID = "location_id";
    public static final String USERLAT = "user_lat";
    public static final String USERLOT = "user_lon";


    public Database2(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String userLocationTable = "CREATE TABLE IF NOT EXISTS "+USERLOCATION+" ( "
                +LOCATION_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +USERLAT + " REAL, "
                +USERLOT + " REAL" +
                ")";


        db.execSQL(userLocationTable);

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
        String main_stations = "CREATE TABLE IF NOT EXISTS "+MAIN_STATIONS+
                                "("+MAIN_STATION_TYPE +" TEXT PRIMARY KEY, "+
                                NORTHBOUND+" TEXT," +
                                SOUTHBOUND1+" TEXT," +
                                SOUTHBOUND2+" TEXT)";
        String cta_stops = "CREATE TABLE IF NOT EXISTS "+TRAIN_STATION_TABLE +
                "(station_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STATION_NAME+" TEXT, " +
                RED_COL+" TEXT, " +
                BLUE_COL+" TEXT, " +
                GREEN_COL+" TEXT, " +
                BROWN_COL+" TEXT, " +
                PURPLE_COL+" TEXT, " +
                YELLOW_COL+" TEXT, " +
                PINK_COL+" TEXT, " +
                ORANGE_COL+" TEXT, " +
                LATITUDE_COL+" REAL, " +
                LONGITUDE_COL+" REAL)";
        String favorite_station = "CREATE TABLE IF NOT EXISTS "+FAVORITE_STATIONS+
                                    "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    FAV_STATION_NAME+" TEXT, "+
                                    FAV_STATION_TYPE+" TEXT, "+
                                    CHOSEN_DIRECTION+" INTEGER, "+
                                    STATION_ID+" INTEGER, " +
                                    " FOREIGN KEY ("+STATION_ID+") REFERENCES cta_stops ("+STATION_ID+")," +
                                    " FOREIGN KEY ("+FAV_STATION_TYPE+") REFERENCES main_stations ("+MAIN_STATION_TYPE+"))";
        String tracking_table = "CREATE TABLE IF NOT EXISTS " + TRACKING_TABLE + " ( "
                + TRACKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TRACKING_NAME_COL + " TEXT, "
                + TRACKING_STATION_ID+ " INTEGER,"
                + TRACKING_TYPE_COL + " TEXT, "
                + TRACKING_LAT_COL + " REAL, "
                + TRACKING_LON_COL + " REAL, "
                + TRACKING_DIR_COL +" INTEGER, "
                + TRACKING_MAIN_COL+ " TEXT)";

        try{
        db.execSQL(line_stops_table);
        db.execSQL(tracking_table);
        db.execSQL(cta_stops);
        db.execSQL(main_stations);
        db.execSQL(favorite_station);
        Log.e("DATABASE SUCCESS", "TABLES CREATED SUCCESSFULLY");
    }catch (Exception e){
        Log.e("DATABASE ERROR", "Error in Creating Table(s)");
    }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void update_location(String id, String table_name,Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("location_id",id);
        cv.put("user_lat",lat);
        cv.put("user_lon",lon);

        db.update(table_name, cv, "location_id = ?", new String[]{id});
        db.close();
    }

    public void addLocation(Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_lat", lat);
        cv.put("user_lon", lon);

        String query = "SELECT * FROM " + USERLOCATION;
        db.insert(USERLOCATION, null, cv);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        db.close();
    }

    public void add_station_lines(CTA_Stations cta_data){
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
        db.insert(LINE_STOPS_TABLE, null, values);
        String query = "SELECT * FROM " + LINE_STOPS_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        db.close();
    }


    public void DeleteRecentStation(String where ,String[] id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("ddd", id[0]+" fffsd");
        db.delete("favorite_stations", where, id);
        Log.e("deleted", "deleted");
    }

    public String getValue(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();


        return cursor.getString(0);
    }
    public ArrayList<String> get_table_record(String table_name, String condition){
        ArrayList<String> userRecord = new ArrayList<>();

        String query = "SELECT * FROM "
                + table_name
                + " "+condition;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if( cursor != null && cursor.moveToFirst() ) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                userRecord.add(cursor.getString(i));
            }
        }else{
            Log.e("NULL CURSOR FROM ", query);
        }
//        Log.e("cursor", cursor.getString(1));
        return userRecord;

    }



    public ArrayList<HashMap> search_query(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HashMap<String, String> record = new HashMap<>();
        ArrayList<HashMap> result = new ArrayList<>();
        cursor.moveToFirst();

        while(cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                record.put(cursor.getColumnName(i), cursor.getString(i));
            }
            result.add(record);
        }

        return result;
    }

    public void add_tracking_station(String name, String type, String dir, String main, String[] coordinates, String station_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRACKING_NAME_COL,name );
        values.put(TRACKING_STATION_ID, station_id);
        values.put(TRACKING_TYPE_COL,type );
        values.put(TRACKING_DIR_COL,dir );
        values.put(TRACKING_MAIN_COL,main );
        values.put(TRACKING_LAT_COL,coordinates[0] );
        values.put(TRACKING_LON_COL,coordinates[1] );

        try {
            db.insert(TRACKING_TABLE, null, values);
            Log.e("SUCCESS", "Station added successfully");
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void addNewStation(String name, String type, Integer dir,Integer station_id ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAV_STATION_NAME,name );
        values.put(FAV_STATION_TYPE,type );
        values.put(STATION_ID, station_id );
        values.put(CHOSEN_DIRECTION,dir );
        try {
            db.insert(FAVORITE_STATIONS, null, values);
            Log.e("SUCCESS", "Station added successfully");
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public void addMainStations(MainStation mainStation){
//        HashMap<String, String> t = getAllRecord(MAIN_STATIONS);
//        if (t.isEmpty()) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(MAIN_STATION_TYPE, mainStation.getTrainLine());
            values.put(NORTHBOUND, mainStation.getNorthBound());
            values.put(SOUTHBOUND1, mainStation.getSouthBound());
            values.put(SOUTHBOUND2, mainStation.getSouthBound2());
            db.insert(MAIN_STATIONS, null, values);
            db.close();
//        }
    }
    public void add_stations(CTA_Stations cta_data){
//        HashMap<String, String> t = getAllRecord(MAIN_STATIONS);
//        if (!t.isEmpty()) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(STATION_NAME, cta_data.getName());
            values.put(RED_COL, cta_data.getRed());
            values.put(BLUE_COL, cta_data.getBlue());
            values.put(GREEN_COL, cta_data.getGreen());
            values.put(BROWN_COL, cta_data.getBrown());
            values.put(PURPLE_COL, cta_data.getPurple());
            values.put(YELLOW_COL, cta_data.getYellow());
            values.put(PINK_COL, cta_data.getPink());
            values.put(ORANGE_COL, cta_data.getOrange());
            values.put(LATITUDE_COL, cta_data.getLat());
            values.put(LONGITUDE_COL, cta_data.getLon());

            db.insert(TRAIN_STATION_TABLE, null, values);
//        }


    }

    public boolean isEmpty(String TableName){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TableName);

        if (NoOfRows == 0){
            return true;
        }else {
//            Log.e("num rows", NoOfRows+"");
            return false;
        }
    }


    public ArrayList<String> get_column_values(String table_name, String col){
        SQLiteDatabase db= this.getReadableDatabase();
        ArrayList<String> values = new ArrayList<>();
        String query = "SELECT "+col +" FROM "+table_name;
//        Log.e("que", query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if( cursor != null ) {
            if (cursor.getCount() >= 0) {
                while (cursor.moveToNext()) {
                    values.add(cursor.getString(0));
                }
            }
        }else{
            Log.e("NULL CURSOR FROM ", query);
        }
        if (values.contains("null")){
            values.removeAll(Collections.singleton("null"));
        }
        return values;

    }
    public Boolean clear_table(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+table_name;
        db.execSQL(query);
        return isEmpty(table_name);


    }

    public void update_value(String id, String table_name,String col, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("tracking_id", id);
        cv.put(col, value);
        try {
            db.update(table_name, cv, "tracking_id = ?", new String[]{id});
        }catch (Exception e){
            Log.e("ERROR", "ERROR IN UPDATING: "+ value +" IN "+ table_name);
        }
        db.close();
        Log.e("Update", "Update success!");

        db.close();

    }

    public HashMap<String, String> get_tracking_record(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM tracking_table";
        Cursor cursor = db.rawQuery(query, null);
        HashMap<String, String> record = new HashMap<>();
        while (cursor.moveToNext()) {
            record.put("tracking_id", cursor.getString(0));
            record.put("station_name", cursor.getString(1));
            record.put("station_type", cursor.getString(3));
            record.put("station_dir", cursor.getString(6));
            record.put("main_station", cursor.getString(7));
            record.put("station_id", cursor.getString(2));
            record.put("station_lat", cursor.getString(4));
            record.put("station_lon", cursor.getString(5));


        }
        return record;
    }

    public ArrayList<HashMap> getAllRecord(String table_name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ table_name;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<HashMap> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, String> record = new HashMap<>();
            record.put("fav_station_name", cursor.getString(1));
            record.put("fav_station_type", cursor.getString(2));
            record.put("fav_station_dir", cursor.getString(3));
            record.put("fav_station_id", cursor.getString(4));
            result.add(record);

        }
        return result;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

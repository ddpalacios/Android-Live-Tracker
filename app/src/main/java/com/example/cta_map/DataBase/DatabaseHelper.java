package com.example.cta_map.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    //information of database

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "Profile.db";

    public static final String USER_INFO_TABLE = "User_info";
    public static final String PROFILE_ID_COL = "profile_id"; // primary
    public static final String PROFILE_FIRSTNAME_COL = "first_name";
    public static final String PROFILE_LASTNAME_COL = "last_name"; //foriegn key
    public static final String PROFILE_USERNAME_COL = "user_name"; //foriegn key
    public static final String PROFILE_EMAIL_COL = "email"; //foriegn key
    public static final String PROFILE_BIRTHDAY_COL = "birthday"; //foriegn key
    public static final String PROFILE_PHONE_COL = "phone"; //foriegn key
    public static final String PROFILE_PASS_COL = "password"; //foriegn key


    public static final String TRAIN_TABLE = "train_table";
    public static final String RECORD_ID = "RECORD_ID";
    public static final String STATION_TYPE_COL = "station_type";
    public static final String STATION_NAME_COL = "station_name";
    public static final String STATION_LAT_COL = "station_lat";
    public static final String STATION_LON_COL = "station_lon";
    public static final String STATION_DIR_COL = "station_dir";
    public static final String MAIN_STATION_COL = "main_station_name";


    public static final String TRAIN_STATION_TABLE = "train_station_table";
    public static final String RECORD_STATION_ID = "RECORD_STATION_ID";
    public static final String STATION_NAME = "STATION_NAME";
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

    public static final String MAIN_STATIONS = "main_stations_table";
    public static final String MAIN_STATION_ID = "Main_stationID";
    public static final String TRAIN_LINE = "train_line";
    public static final String NORTHBOUND_COL = "northbound";
    public static final String SOUTHBOUND_COL = "southbound";

    public static final String TRACKING_TABLE = "tracking_table";
    public static final String TRACKING_ID = "TRACKING_ID";
    public static final String TRACKING_TYPE_COL = "station_type";
    public static final String TRACKING_NAME_COL = "station_name";
    public static final String TRACKING_LAT_COL = "station_lat";
    public static final String TRACKING_LON_COL = "station_lon";
    public static final String TRACKING_DIR_COL = "station_dir";
    public static final String TRACKING_MAIN_COL = "main_station_name";






    //initialize the database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {

        String user_info_table = "CREATE TABLE IF NOT EXISTS " + USER_INFO_TABLE + " ( "
                + PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROFILE_FIRSTNAME_COL + " TEXT, "
                + PROFILE_LASTNAME_COL + " TEXT, "
                + PROFILE_USERNAME_COL + " TEXT, "
                + PROFILE_EMAIL_COL + " TEXT, "
                + PROFILE_BIRTHDAY_COL + " TEXT, "
                + PROFILE_PHONE_COL + " TEXT, "
                + PROFILE_PASS_COL + " TEXT, " +
                "FOREIGN KEY (" + PROFILE_ID_COL + ") REFERENCES " + TRAIN_TABLE + " (" + PROFILE_ID_COL + "))";

        db.execSQL(user_info_table);

        Log.e("Creating finished", "table");



        String train_table = "CREATE TABLE IF NOT EXISTS " + TRAIN_TABLE + " ( "
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROFILE_ID_COL + " INTEGER, "
                + STATION_NAME_COL + " TEXT, "
                + STATION_TYPE_COL + " TEXT, "
                + STATION_LAT_COL + " REAL, "
                + STATION_LON_COL + " REAL, "
                + STATION_DIR_COL +" INTEGER, "
                + MAIN_STATION_COL+ " TEXT)";

        db.execSQL(train_table);


        Log.e("Created", USER_INFO_TABLE);


        String train_station_table = "CREATE TABLE IF NOT EXISTS " + TRAIN_STATION_TABLE + " ( "
                + RECORD_STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STATION_NAME + " INTEGER, "
                + RED_COL + " TEXT, "
                + BLUE_COL + " TEXT, "
                + GREEN_COL + " TEXT, "
                + BROWN_COL + " TEXT, "
                + PURPLE_COL +" TEXT, "
                + YELLOW_COL + " TEXT, "
                + PINK_COL + " TEXT, "
                + ORANGE_COL + " TEXT, "
                + LATITUDE_COL + " TEXT, "
                + LONGITUDE_COL + " TEXT)";

        db.execSQL(train_station_table);
        Log.e("Created", TRAIN_STATION_TABLE);


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
        Log.e("Created", LINE_STOPS_TABLE);


        String main_stations_table = "CREATE TABLE IF NOT EXISTS "+ MAIN_STATIONS
                + " ( "+ MAIN_STATION_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT,"
                + TRAIN_LINE + " TEXT, "
                + NORTHBOUND_COL+ " TEXT, "
                + SOUTHBOUND_COL+ " TEXT)";



        db.execSQL(main_stations_table);
        Log.e("Created", main_stations_table);


        String tracking_table = "CREATE TABLE IF NOT EXISTS " + TRACKING_TABLE + " ( "
                + TRACKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROFILE_ID_COL+ " INTEGER, "
                + TRACKING_NAME_COL + " TEXT, "
                + TRACKING_TYPE_COL + " TEXT, "
                + TRACKING_LAT_COL + " REAL, "
                + TRACKING_LON_COL + " REAL, "
                + TRACKING_DIR_COL +" INTEGER, "
                + TRACKING_MAIN_COL+ " TEXT)";

        db.execSQL(tracking_table);
        Log.e("Created",tracking_table);



    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void update_tracking_record(String id, String table_name,String name, String type, String lat, String lon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("profile_id",id);
        cv.put("station_name", name);
        cv.put("station_type",type);
        cv.put("station_lat",lat);
        cv.put("station_lon",lon);

        db.update(table_name, cv, "profile_id = ?", new String[]{id});
        db.close();


    }

    public void update_value(String id, String table_name,String col, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("profile_id", id);
        cv.put(col, value);
        try {
            db.update(table_name, cv, "profile_id = ?", new String[]{id});
        }catch (Exception e){
            Log.e("ERROR", "ERROR IN UPDATING: "+ value +" IN "+ table_name);
        }
        db.close();
        Log.e("Update", "Update success!");


    }







    public void add_user(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues profile_values = new ContentValues();
        profile_values.put(PROFILE_FIRSTNAME_COL, profile.getFirst());
        Log.e("added", profile.getFirst());
        profile_values.put(PROFILE_LASTNAME_COL, profile.getLast());
        Log.e("added", profile.getLast());

        profile_values.put(PROFILE_USERNAME_COL, profile.getUsername());
        profile_values.put(PROFILE_EMAIL_COL, profile.getEmail());
        profile_values.put(PROFILE_BIRTHDAY_COL, profile.getBday());
        profile_values.put(PROFILE_PHONE_COL, profile.getPhone());
        profile_values.put(PROFILE_PASS_COL, profile.getPassword());

        db.insert(USER_INFO_TABLE, null, profile_values);
        String query = "SELECT * FROM " + USER_INFO_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToLast();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        profile.setID(auto_id);

    }

    public void add_stations(CTA_Stations cta_data){
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
        String query = "SELECT * FROM " + TRAIN_STATION_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));


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
        Log.e("APPEND", "To TABLE");
        db.insert(LINE_STOPS_TABLE, null, values);
        String query = "SELECT * FROM " + LINE_STOPS_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        db.close();



    }

    public void addMainStations(MainStation mainStation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TRAIN_LINE, mainStation.getTrainLine());
        values.put(NORTHBOUND_COL, mainStation.getNorthBound());
        values.put(SOUTHBOUND_COL, mainStation.getSouthBound());
        db.insert(MAIN_STATIONS, null, values);
        String query = "SELECT * FROM " + MAIN_STATIONS;
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        db.close();
    }

    public void addUserStation(UserStation userStation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues profile_values = new ContentValues();
        profile_values.put(PROFILE_ID_COL, userStation.getID());
        profile_values.put(STATION_NAME_COL, userStation.getStation_name());
        profile_values.put(STATION_TYPE_COL, userStation.getStation_type());
        profile_values.put(STATION_LAT_COL, userStation.getStationLat());
        profile_values.put(STATION_LON_COL, userStation.getStationLon());
        profile_values.put(STATION_DIR_COL, userStation.getDirection());
        profile_values.put(MAIN_STATION_COL, userStation.get_main());
        db.insert(TRAIN_TABLE, null, profile_values);
        String query = "SELECT * FROM " + TRAIN_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        db.close();


    }
    public ArrayList<HashMap> GetTableRecordByID(Integer id, String table_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap> userRecord = new ArrayList<>();
        String query = "SELECT * FROM "
                + table_name
                + " WHERE " + PROFILE_ID_COL + " = '" + id+"'";

        Cursor cursor = db.rawQuery(query, null);
        if( cursor != null ){

            if (cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    HashMap<String, String> record = new HashMap<>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        record.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                    userRecord.add(record);
                }
            }

            cursor.close();
        }else{
            Log.e("NULL CURSOR FROM ", query);

        }


    return userRecord;
    }

    public HashMap<String, String> getAllRecord(String table_name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ table_name;
        Cursor cursor = db.rawQuery(query, null);
        HashMap<String, String> record = new HashMap<>();

        if( cursor != null ){
            if (cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        record.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                }
            }

            cursor.close();



        }else{
            Log.e("NULL CURSOR FROM ", query);
            return null;
        }

        return record;

    }

  public void add_train_tracker(ArrayList<String> record){
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      if (record.isEmpty()){
          Log.e("INDEX ERROR", "RECORD IS EMPTY");
          return;
      }
      values.put(PROFILE_ID_COL, record.get(0));
      values.put(TRACKING_NAME_COL, record.get(1));

      values.put(TRACKING_TYPE_COL, record.get(2));
      values.put(TRACKING_LAT_COL, record.get(3));
      values.put(TRACKING_LON_COL, record.get(4));
      values.put(TRACKING_DIR_COL, record.get(5));
      values.put(TRACKING_MAIN_COL, record.get(6));
      db.insert(TRACKING_TABLE , null, values);
      db.close();

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


    public boolean isEmpty(String TableName){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TableName);

        if (NoOfRows == 0){
            return true;
        }else {
            Log.e("num rows", NoOfRows+"");
            return false;
        }
    }






    public ArrayList<String> GetProfileRecord(String username, String password) {
        ArrayList<String> userRecord = new ArrayList<>();
        String query = "SELECT * FROM "
                + USER_INFO_TABLE
                + " WHERE " + PROFILE_USERNAME_COL + " = '" + username + "' AND " +
                PROFILE_PASS_COL + " = '" + toHex(password) + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if( cursor != null && cursor.moveToFirst() ) {
            for (int i = 0; i < 8; i++) {
                userRecord.add(cursor.getString(i));

            }
            Log.e("cursor", cursor.getString(1));
        }else{
            Log.e("NULL CURSOR FROM ", query);

        }




        return userRecord;
    }


    public Boolean find_profile(String username, String password){
        String query = "SELECT * FROM "
                +USER_INFO_TABLE
                + " WHERE " +  PROFILE_USERNAME_COL + " = '"+username +"' AND "+
                PROFILE_PASS_COL +" = '"+toHex(password)+"'";



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            return true;
        }
        else {
            return false;
        }

    }


    public boolean deleteAll(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ table_name);

        return isEmpty(table_name);
    }


    public void deleteRecord(String from_table, String where, String[] args){

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("delete", args[1]+" "+ args[0] + " "+args[2]);
        int ds = db.delete(from_table, where, args);
    }



    public ArrayList<String> get_column_values(String table_name, String col){
    SQLiteDatabase db= this.getReadableDatabase();
    ArrayList<String> values = new ArrayList<>();
        String query = "SELECT "+col +" FROM "+table_name;
        Cursor cursor = db.rawQuery(query, null);
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



    public Double[] FindStationValues(String station_name, String station_type){
        SQLiteDatabase db= this.getReadableDatabase();
        Log.e("ddd", station_name.replaceAll("’", "\\'")+" "+ station_type);
        String query = "SELECT * FROM "+TRAIN_STATION_TABLE + " WHERE STATION_NAME ='"+station_name.replaceAll("’", "\\'")+"' AND "+station_type.toLowerCase()+" ='true'";
        Cursor cursor = db.rawQuery(query, null);
        if( cursor != null && cursor.moveToFirst() ) {
            if (cursor.moveToFirst()) {
                Double[] values = new Double[]{
                        Double.parseDouble(cursor.getString(cursor.getColumnCount() - 2)),
                        Double.parseDouble(cursor.getString(cursor.getColumnCount() - 1))
                };
                return values;
            }
        }else{
            Log.e("NULL CURSOR FROM ", query);

        }
return null;

    }




    private String toHex(String s){
        char[] ch = s.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char c : ch) {
            String hexString = Integer.toHexString(c);
            sb.append(hexString);
        }
        return sb.toString();
    }

    private String toString(String hex) {
        StringBuilder result = new StringBuilder();
        char[] charArray = hex.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            char ch = (char) Integer.parseInt(st, 16);
            result.append(ch);
        }

        return result.toString();

    }




}
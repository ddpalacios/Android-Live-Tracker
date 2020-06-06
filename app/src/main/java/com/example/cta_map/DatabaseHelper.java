package com.example.cta_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
                + STATION_DIR_COL +" INTEGER)";

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







    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
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


    public void addUserStation(UserStation userStation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues profile_values = new ContentValues();
        profile_values.put(PROFILE_ID_COL, userStation.getID());
        profile_values.put(STATION_NAME_COL, userStation.getStation_name());
        profile_values.put(STATION_TYPE_COL, userStation.getStation_type());
        profile_values.put(STATION_LAT_COL, userStation.getStationLat());
        profile_values.put(STATION_LON_COL, userStation.getStationLon());
        profile_values.put(STATION_DIR_COL, userStation.getDirection());
        db.insert(TRAIN_TABLE, null, profile_values);
        String query = "SELECT * FROM " + TRAIN_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));
        db.close();


    }

    public ArrayList<HashMap> GetTableRecord(Integer id, String table_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap> userRecord = new ArrayList<>();
        String query = "SELECT * FROM "
                + table_name
                + " WHERE " + PROFILE_ID_COL + " = '" + id+"'";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                HashMap<String, String> record = new HashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    record.put(cursor.getColumnName(i), cursor.getString(i));
                }
                userRecord.add(record);
            }
        }

    return userRecord;
    }




    public ArrayList<String> GetProfileRecord(String username, String password) {
        ArrayList<String> userRecord = new ArrayList<>();
        String query = "SELECT * FROM "
                + USER_INFO_TABLE
                + " WHERE " + PROFILE_USERNAME_COL + " = '" + username + "' AND " +
                PROFILE_PASS_COL + " = '" + toHex(password) + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        for (int i=0;i<8;i++ ){
            userRecord.add(cursor.getString(i));

        }
        Log.e("curdor", cursor.getString(1));
        return userRecord;
    }


    public Boolean find_profile(String username, String password){
        String query = "SELECT * FROM "
                +USER_INFO_TABLE
                + " WHERE " +  PROFILE_USERNAME_COL + " = '"+username +"' AND "+
                PROFILE_PASS_COL +" = '"+toHex(password)+"'";



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.e("FOUND STATUS", cursor.moveToFirst()+"");

        if (cursor.moveToFirst()){
            return true;
        }
        else {
            return false;
        }

    }


    public void deleteRecord(String from_table, String where, String[] args){

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("delete", args[1]+" "+ args[0] + " "+args[2]);
        int ds = db.delete(from_table, where, args);
    }



    public ArrayList<String> getValues(String table_name, String col){
    SQLiteDatabase db= this.getReadableDatabase();
    ArrayList<String> values = new ArrayList<>();
        String query = "SELECT "+col +" FROM "+table_name;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            if (cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                        values.add(cursor.getString(0));

                }
            }

        }
        if (values.contains("null")){
            values.removeAll(Collections.singleton("null"));
        }

return values;

    }



    public Double[] FindStationValues(String station_name, String station_type){
        SQLiteDatabase db= this.getReadableDatabase();
        String query = "SELECT * FROM "+TRAIN_STATION_TABLE + " WHERE STATION_NAME ='"+station_name+"' AND "+station_type.toLowerCase()+" ='true'";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            Double[] values = new Double[]{
                    Double.parseDouble(cursor.getString(cursor.getColumnCount()-2)),
                    Double.parseDouble(cursor.getString(cursor.getColumnCount()-1))
            };
            return values;
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
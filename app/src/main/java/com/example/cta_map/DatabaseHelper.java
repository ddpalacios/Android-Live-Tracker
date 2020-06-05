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

public class DatabaseHelper extends SQLiteOpenHelper {
    //information of database

    private static final int DATABASE_VERSION = 2;

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
                + PROFILE_PASS_COL + " TEXT)";

        db.execSQL(train_table);


        Log.e("Created", USER_INFO_TABLE);

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

    public void addUserStation(UserStation userStation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues profile_values = new ContentValues();
        profile_values.put(PROFILE_ID_COL, userStation.getID());
        profile_values.put(STATION_NAME_COL, userStation.getStation_name());
        profile_values.put(STATION_TYPE_COL, userStation.getStation_type());
        profile_values.put(STATION_LAT_COL, userStation.getStationLat());
        profile_values.put(STATION_LON_COL, userStation.getStationLon());
        db.insert(TRAIN_TABLE, null, profile_values);
        String query = "SELECT * FROM " + TRAIN_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer auto_id = Integer.parseInt(cursor.getString(0));


    }


    public ArrayList<String> getRecord(String username, String password) {
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
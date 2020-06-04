package com.example.cta_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    //information of database

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "Profile.db";

    public static final String IDENTIFICATION_TABLE = "identification_table";
    public static final String PROFILE_ID_COL = "profile_id"; // primary
    public static final String PROFILE_BDAY_COL = "profile_bday";
    public static final String PROFILE_NAME_COL = "profile_name"; //foriegn key


    public static final String CONTACT_TABLE = "contact_table";
    public static final String PHONE_NUMBER_COL = "phone_number_col";
    public static final String EMAIL_COL = "email_col";


    //initialize the database

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override

    public void onCreate(SQLiteDatabase db) {


        String identification_table = "CREATE TABLE IF NOT EXISTS " + IDENTIFICATION_TABLE + " ( "
                + PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROFILE_BDAY_COL + " TEXT,"
                + PROFILE_NAME_COL + " TEXT," +
                "FOREIGN KEY (" + PROFILE_NAME_COL + ") REFERENCES " + CONTACT_TABLE + " (" + PROFILE_ID_COL + "))";


        String contact_table = "CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE + " ( "
                + PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PHONE_NUMBER_COL + " TEXT,"
                + EMAIL_COL + " TEXT)";
        db.execSQL(contact_table);
        Log.e("Created", contact_table);


        db.execSQL(identification_table);
        Log.e("Created", identification_table);






    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public void add_user(Profile profile){
        ContentValues profile_values = new ContentValues();
        profile_values.put(PROFILE_NAME_COL, profile.getName());
        profile_values.put(PROFILE_BDAY_COL, profile.getBday());

        ContentValues contact_values = new ContentValues();
        contact_values.put(PHONE_NUMBER_COL, profile.getPhone());
        contact_values.put(EMAIL_COL, profile.getEmail());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(IDENTIFICATION_TABLE,null,profile_values);
        db.insert(CONTACT_TABLE, null, contact_values);
        Log.e("SQLITE", "User: "+ profile.getName() +" was added to your database!");


    }






}
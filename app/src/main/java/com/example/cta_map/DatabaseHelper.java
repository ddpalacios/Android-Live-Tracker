package com.example.cta_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "trainPositionsDB.db";

    public static final String TABLE_NAME = "TrainCord";

    public static final String COLUMN_ID = "TrainID";

    public static final String COLUMN_NAME = "TrainCoordinates";


    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + "INTEGER PRIMARYKEY," + COLUMN_NAME + "TEXT )";
        db.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String loadHandler() {

        String result = "";

        String query = "Select*FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            int result_0 = cursor.getInt(0);

            String result_1 = cursor.getString(1);

            result += String.valueOf(result_0) + " " + result_1 +

                    System.getProperty("line.separator");

        }

        cursor.close();

        db.close();

        return result;

    }


    public void addHandler(Double coordinates, Integer train_num) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, train_num);

        values.put(COLUMN_NAME, coordinates);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);

        db.close();

    }



}

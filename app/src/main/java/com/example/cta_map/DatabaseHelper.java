package com.example.cta_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Train_Coordinates.db";
    public static final String TABLE_NAME = "coordinate_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "LATITUDE";
    public static final String COL_3 = "LONGITUDE";




    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME + "("+"ID INTEGER PRIMARY KEY AUTOINCREMENT,LATITUDE REAL,LONGITUDE REAL"+")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, lat);
        contentValues.put(COL_3, lon);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }


//    public boolean updateData(Double train_coord) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_1, train_coord);
//
//        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(train_coord)});
//        return true;
//    }

    public Integer deleteData(String train_coord) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{train_coord});
    }



}

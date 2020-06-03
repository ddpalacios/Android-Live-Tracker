package com.example.cta_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cta_user_profiles.db";
    public static final String TABLE_NAME = "profile";
    public static final String COL_1 = "USERNAME";
    public static final String COL_2 = "EMAIL";
    public static final String COL_3 = "PHONE_NUMBER";




    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + "("+"" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USERNAME TEXT," +
                "EMAIL TEXT," +
                "PHONE_NUMBER TEXT"+")");

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


    public boolean updateData(String id, Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, lat);
        contentValues.put(COL_3, lon);


        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{(id)});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }



}

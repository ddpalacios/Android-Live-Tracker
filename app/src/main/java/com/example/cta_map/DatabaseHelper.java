package com.example.cta_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "People.db";

    public static final String TABLE_NAME = "Student";

    public static final String COLUMN_ID = "StudentID";

    public static final String COLUMN_NAME = "StudentName";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                "INTEGER PRIMARY KEY," + COLUMN_NAME + "TEXT )";

        db.execSQL(CREATE_TABLE);

    }


    public boolean updateHandler(int ID, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();

        args.put(COLUMN_ID, ID);

        args.put(COLUMN_NAME, name);

        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + ID, null) > 0;

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addHandler(Student student) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, student.getStudent_id());

        values.put(COLUMN_NAME, student.getStudent_name());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);

        db.close();

    }


    public String loadHandler() {

        String result = "";

        String query = "Select * FROM " + TABLE_NAME;

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

}

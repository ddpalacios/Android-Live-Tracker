package com.example.cta_map.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

import com.example.cta_map.Activities.FavoriteStation;

import java.util.ArrayList;
import java.util.HashMap;


public class CTA_DataBase extends SQLiteOpenHelper {

//    public static final String ALL_TRAINS_TABLE = "all_trains_table";
//    public static final String TRAIN_ID = "train_id";
//    public static final String IS_NOTIFIED = "isNotified";
//    public static final String PRED_ARRIVAL_TIME = "pred_arrival_time";
//    public static final String NEXT_STOP_ID = "next_stop_id";
//    public static final String NEXT_STOP_ETA = "next_stop_eta";
//    public static final String NEXT_STOP_DISTANCE = "next_stop_distance";
//    public static final String IS_DELAYED = "isdelayed";
//    public static final String IS_APPROACHING = "isApproaching";
//    public static final String DISTANCE_TO_TARGET = "distance_to_target";
//    public static final String TRAIN_LAT = "train_lat";
//    public static final String TRAIN_LON = "train_lon";
//    public static final String TO_TARGET_TRAIN_ETA = "to_target_eta";
//    public static final String TRACKING_TYPE = "tracking_type";
//    public static final String TARGET_ID = "target_id";
//    public static final String TRAIN_DIR = "train_dir";
//
    public static final String Markers = "MARKERS";
    public static final String marker_id = "marker_id";
    public static final String marker_lat = "marker_lat";
    public static final String marker_lon = "marker_lon";
    public static final String marker_name = "marker_name";
    public static final String marker_type = "marker_type";

    public static final String L_STOPS = "L_STOPS";
    public static final String STOPID = "STOPID"; // auto
    public static final String GREEN_LINE = "GREEN";
    public static final String RED_LINE = "RED";
    public static final String BLUE_LINE = "BLUE";
    public static final String YELLOW_LINE = "YELLOW";
    public static final String PINK_LINE = "PINK";
    public static final String ORANGE_LINE = "ORANGE";
    public static final String BROWN_LINE = "BROWN";
    public static final String PURPLE_LINE = "PURPLE";
//
    public static final String MAIN_STATIONS = "MAIN_STATIONS";
    public static final String MAIN_STATION_TYPE = "STATION_TYPE";
    public static final String NORTHBOUND = "NORTHBOUND";
    public static final String SOUTHBOUND = "SOUTHBOUND";
    public static final String EXPRESS = "EXPRESS";


    public  final String CTA_STOPS = "CTA_STOPS";
    public  final String ID = "ID";
    public  final String STOP_ID = "STOP_ID";
    public  final String DIRECTION_ID = "DIRECTION_ID";
    public  final String STOP_NAME = "STOP_NAME";
    public final String STATION_NAME = "STATION_NAME";
    public  final String MAP_ID = "MAP_ID";
    public  final String ADA = "ADA";
    public  final String RED = "RED";
    public  final String BLUE = "BLUE";
    public  final String G = "G";
    public  final String BRN = "BRN";
    public final String Y = "Y";
    public final String P = "P";
    public final String PEXP = "PEXP";
    public  final String PINK = "PINK";
    public  final String ORG = "ORG";
    public final String LAT = "LAT";
    public final String LON = "LON";


    public final String USER_FAVORITES = "USER_FAVORITES";
    public  final String FAVORITE_STOP_ID = "STOP_ID";
    public  final String FAVORITE_STATION_TYPE = "STATION_TYPE";
    public  final String FAVORITE_STATION_NAME = "STATION_NAME";


    public CTA_DataBase(@Nullable Context context) {
        super(context, "CTA_DATABASE", null, 10);
        SQLiteDatabase db = this.getWritableDatabase();
        createMarkersTable(db);



    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        create_cta_stops(db);
        createUserFavorites(db);
        create_main_stations(db);
        create_L_stops_table(db);

        Log.e("SQLITE", "CREATED TABLES");
    }


    public void createMarkersTable(SQLiteDatabase db){
        String marker_table = "CREATE TABLE IF NOT EXISTS "+ Markers+ "("
                + marker_id+ " INTEGER PRIMARY KEY,"
                + marker_name + " TEXT,"
                + marker_type + " TEXT,"
                + marker_lat + " TEXT,"
                + marker_lon + " TEXT)";
        db.execSQL(marker_table);
    }

   public void  create_L_stops_table(SQLiteDatabase db){
        String line_stops_table = "CREATE TABLE IF NOT EXISTS " +L_STOPS + " ( "
                + STOPID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GREEN_LINE + " TEXT, "
                + RED_LINE + " TEXT, "
                + BLUE_LINE + " TEXT, "
                + YELLOW_LINE + " TEXT, "
                + PINK_LINE +" TEXT, "
                + ORANGE_LINE + " TEXT, "
                + BROWN_LINE + " TEXT, "
                + PURPLE_LINE + " TEXT)";

        db.execSQL(line_stops_table);
    }
//   public void create_all_trains_table(SQLiteDatabase db){
//       String all_trains_table = "CREATE TABLE IF NOT EXISTS "+ALL_TRAINS_TABLE+" ( "
//               + TRAIN_ID + " TEXT PRIMARY KEY, "
//               + IS_NOTIFIED + " INTEGER, "
//               + PRED_ARRIVAL_TIME + " TEXT, "
//               + NEXT_STOP_ID + " TEXT, "
//               + NEXT_STOP_ETA + " TEXT, "
//               + NEXT_STOP_DISTANCE + " TEXT, "
//               + IS_DELAYED + " TEXT, "
//               + IS_APPROACHING + " TEXT, "
//               + DISTANCE_TO_TARGET + " TEXT, "
//               + TO_TARGET_TRAIN_ETA + " INTEGER, "
//               + TRACKING_TYPE + " TEXT, "
//               + TRAIN_LAT + " REAL, "
//               + TRAIN_LON + " REAL,"
//               + TARGET_ID+ " INTEGER, "
//               +TRAIN_DIR+" INTEGER)";
//
//       db.execSQL(all_trains_table);
//   }



    public void create_main_stations(SQLiteDatabase db){
        String main_stations = "CREATE TABLE IF NOT EXISTS "+ MAIN_STATIONS+
                "(" + MAIN_STATION_TYPE + " TEXT PRIMARY KEY, "+
                NORTHBOUND + " TEXT, " +
                SOUTHBOUND + " TEXT, " +
                EXPRESS + " TEXT)";

        Log.e("SQLITE", "Done");
        db.execSQL(main_stations);
    }

    public void create_cta_stops(SQLiteDatabase db) {
        String cta_stops = "CREATE TABLE IF NOT EXISTS " + CTA_STOPS +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DIRECTION_ID + " TEXT, " +
                STOP_NAME + " TEXT, " +
                STATION_NAME + " TEXT, " +
                STOP_ID + " TEXT, " +
                MAP_ID + " TEXT, " +
                ADA + " INTEGER, " +
                RED + " INTEGER, " +
                BLUE + " INTEGER, " +
                G + " INTEGER, " +
                BRN + " INTEGER, " +
                P + " INTEGER, " +
                PEXP + " INTEGER, " +
                Y + " INTEGER, " +
                PINK + " INTEGER, " +
                ORG + " INTEGER, " +
                LAT + " REAL, " +
                LON + " REAL)";
        Log.e("SQLITE", "Done");
        db.execSQL(cta_stops);

    }

    public void createUserFavorites(SQLiteDatabase db){
        String user_favorites = "CREATE TABLE IF NOT EXISTS " + USER_FAVORITES +
                "(" + FAVORITE_STOP_ID + " TEXT PRIMARY KEY, " +
                FAVORITE_STATION_TYPE + " TEXT, " +
                FAVORITE_STATION_NAME + " TEXT)";


        Log.e("SQLITE", "Done");
        db.execSQL(user_favorites);
    }


    public void commit(Object item, String table_name){
        if (table_name.equals("cta_stops")){
            CTA_Stops station = (CTA_Stops) item;
            add_cta_stations_to_cta_table(station);

        }else if (table_name.equals("favorite_station")){
            FavoriteStation station = (FavoriteStation) item;
           add_user_favorites(station);

        }else if (table_name.equals("MAIN_STATIONS")){
            MainStation mainStation = (MainStation) item;
            add_main_station(mainStation);

        }else if (table_name.equals("L_STOPS")){
            L_stops station = (L_stops) item;
            add_L_stop(station);
        }else if (table_name.equals(Markers)){
            Markers marker = (Markers) item;
            addMarker(marker);
        }

    }


    private void addMarker(Markers markers){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(marker_id, markers.getMarker_id());
        values.put(marker_name, markers.getMarker_name());
        values.put(marker_type, markers.getMarker_type());
        values.put(marker_lat, markers.getMarker_lat());
        values.put(marker_lon, markers.getMarker_lon());
        db.insert(Markers, null, values);
        db.close();

    }

    public void add_main_station(MainStation main_station){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MAIN_STATION_TYPE, main_station.getStationType());
        values.put(NORTHBOUND, main_station.getNorthBound());
        values.put(SOUTHBOUND, main_station.getSouthBound());
        values.put(EXPRESS, main_station.getExpress());


        db.insert(MAIN_STATIONS, null, values);
        db.close();

    }


    public void add_L_stop(L_stops lstop){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GREEN_LINE, lstop.getGreen());
        values.put(YELLOW_LINE, lstop.getYellow());
        values.put(BLUE_LINE, lstop.getBlue());
        values.put(ORANGE_LINE, lstop.getOrange());
        values.put(RED_LINE, lstop.getRed());
        values.put(PURPLE_LINE, lstop.getPurple());
        values.put(BROWN_LINE , lstop.getBrown());
        values.put(PINK_LINE, lstop.getPink());
        db.insert(L_STOPS,null, values);

        db.close();
    }



    private void  add_user_favorites(FavoriteStation cta_data){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAVORITE_STOP_ID, cta_data.getStation_id());
        values.put(FAVORITE_STATION_NAME, cta_data.getStation_name());
        values.put(FAVORITE_STATION_TYPE, cta_data.getStation_type());
        db.insert(USER_FAVORITES, null, values);
        db.close();
    }

    public void add_cta_stations_to_cta_table(CTA_Stops cta_data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STOP_ID, cta_data.getSTOP_ID());
        values.put(DIRECTION_ID, cta_data.getDIRECTION_ID());
        values.put(STOP_NAME, cta_data.getSTOP_NAME());
        values.put(STATION_NAME, cta_data.getSTATION_NAME());
        values.put(MAP_ID, cta_data.getMAP_ID());
        values.put(ADA, cta_data.getADA());
        values.put(RED, cta_data.getRED());
        values.put(BLUE, cta_data.getBLUE());
        values.put(G, cta_data.getG());
        values.put(BRN, cta_data.getBRN());
        values.put(P, cta_data.getP());
        values.put(PEXP, cta_data.getPEXP());
        values.put(Y, cta_data.getY());
        values.put(PINK, cta_data.getPINK());
        values.put(ORG, cta_data.getORG());
        values.put(LAT, cta_data.getLAT());
        values.put(LON, cta_data.getLON());

        db.insert(CTA_STOPS, null, values);
        db.close();

    }

    private void deleteTable(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from " + table_name);
        }catch (Exception e){e.printStackTrace();}

    }
    public int delete_all_records(String table_name){
        ArrayList<Object> all_trains_table = excecuteQuery("*", table_name, null, null,null);
        if (all_trains_table != null){
            deleteTable(table_name);
            return 1;
        }else{
            return -1;

        }
    }

    public boolean delete_record(String table_name, String whereClause, String[] values){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(table_name,whereClause,values);

       return db.delete(table_name,whereClause,values) >0;
    }


    private ArrayList<Object> getRecord(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Object> result = new ArrayList<>();
        try{
            Cursor cursor = db.rawQuery(query, null);


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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<Object> excecuteQuery(String cols,String table_name, String condition, String contains, String col_orderBy){
        String query;
        ArrayList<Object> record = null;
        if (condition == null){
            query = "SELECT "+cols+" FROM "+table_name;
        }else{
            query = "SELECT "+cols+" FROM "+table_name +" WHERE "+condition;
            if (contains!=null){
                query = query + " LIKE '"+ contains+"%'";
            }
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

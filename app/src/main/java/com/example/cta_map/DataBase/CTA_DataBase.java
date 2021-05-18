package com.example.cta_map.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.cta_map.Activities.Classes.Alarm;
import com.example.cta_map.Activities.Classes.FavoriteStation;
import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.Classes.UserSettings;
import com.example.cta_map.Activities.UserLocation;
import com.example.cta_map.Activities.UserSettings_Form;
import com.example.cta_map.Displayers.Train;

import java.util.ArrayList;
import java.util.HashMap;


public class CTA_DataBase extends SQLiteOpenHelper {
    public static final String TRAIN_TRACKER = "TRAIN_TRACKER";
    public static final String TRAIN_ID = "TRAIN_ID";
    public static final String TRAIN_MAP_ID = "MAP_ID";
    public static final String TRAIN_DIR = "TRAIN_DIR";
    public static final String TRAIN_TYPE = "TRAIN_TYPE";
    public static final String TRAIN_ETA = "TRAIN_ETA";

    public static final String TRAIN_NOTIFIED_BY_ALARM = "NOTIFIED_BY_ALARM";

    public static final String USER_SETTINGS_ID = "USER_SETTINGS_ID";
    public static final String USER_SETTINGS = "USER_SETTINGS";
    public static final String IS_SHARING_LOC = "IS_SHARING_LOC";
    public static final String GREEN_LIMIT = "GREEN_LIMIT";
    public static final String YELLOW_LIMIT = "YELLOW_LIMIT";
    public static final String RED_LIMIT = "RED_LIMIT";
    public static final String AS_STATIONS = "AS_STATIONS";
    public static final String AS_MINUTES = "AS_MINUTES";
    public static final String SOUND = "SOUND";
    public static final String VIBRATE = "VIBRATE";
    public static final String NO_SOUND = "NO_SOUND";




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


    public static final String CTA_STOPS = "CTA_STOPS";
    public  final String ID = "ID";
    public  final String _ID = "_id";

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


    public static final  String USER_FAVORITES = "USER_FAVORITES";
    public static final String FAVORITE_STATION_ID = "FAVORITE_STATION_ID";
    public  static final String FAVORITE_STOP_ID = "STOP_ID";
    public static final String FAVORITE_MAP_ID = "FAVORITE_MAP_ID";
    public  static final String FAVORITE_STATION_TYPE = "STATION_TYPE";
    public  static final String FAVORITE_STATION_NAME = "STATION_NAME";
    public  static final String FAVORITE_STATION_DIRECTION = "STATION_DIR";
    public  static final String FAVORITE_STATION_DIRECTION_LABEL = "STATION_DIR_LABEL";
    public  static final String ISTRACKING = "ISTRACKING";


    public static final  String USER_LOCATION = "USER_LOCATION";
    public static final String USER_LOCATION_ID = "STOP_ID";
    public static  final String HAS_LOCATION = "HAS_LOCATION";
    public static final String USER_LAT = "USER_LAT";
    public static final String USER_LON = "USER_LON";


    public static final  String ALARMS = "ALARMS";
    public static final String ALARM_ID = "ALARM_ID";
    public static final String ALARM_STATION_TYPE = "STATION_TYPE";
    public static final String ALARM_STATION_NAME = "STATION_NAME";
    public  static final String ALARM_MAP_ID = "MAP_ID";
    public  static final String ALARM_IS_ON = "IS_ON";
    public static final String WILL_REPEAT = "WILL_REPEAT";
    public static final String TIME = "TIME";
    public static final String ALARM_DIRECTION = "DIRECTION";
    public static final String HOUR = "HOUR";
    public static final String MIN = "MIN";
    public final String MON = "MON";
    public final String TUES = "TUES";
    public final String WENS = "WENS";
    public final String THUR = "THUR";
    public final String FRI = "FRI";
    public final String SAT = "SAT";
    public final String SUN = "SUN";
    public static final String WEEK_LABEL = "WEEK_LABEL";









    public CTA_DataBase(@Nullable Context context) {
        super(context, "CTA_DATABASE", null, 95);
        SQLiteDatabase db = this.getWritableDatabase();
        createTrainTrackingTable(db);
        createUserSettingsTable(db);




    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        create_cta_stops(db);
        createUserFavorites(db);
        create_main_stations(db);
        create_L_stops_table(db);
        create_userLocation_table(db);
        create_alarm_table(db);
        createTrainTrackingTable(db);
        createUserSettingsTable(db);



        Log.e("SQLITE", "CREATED TABLES");
    }


    public void createTrainTrackingTable(SQLiteDatabase db){
        String tracker_table = "CREATE TABLE IF NOT EXISTS "+ TRAIN_TRACKER+ " ("
                + TRAIN_ID+ " TEXT PRIMARY KEY, "
                + TRAIN_MAP_ID + " TEXT, "
                + TRAIN_DIR + " TEXT, "
                + TRAIN_ETA + " TEXT, "
                + TRAIN_NOTIFIED_BY_ALARM + " INTEGER, "
                + TRAIN_TYPE + " TEXT)";
        db.execSQL(tracker_table);
    }



    public void createUserSettingsTable(SQLiteDatabase db){
        String tracker_table = "CREATE TABLE IF NOT EXISTS "+ USER_SETTINGS+ " ("
                + USER_SETTINGS_ID+ " INTEGER PRIMARY KEY, "
                + IS_SHARING_LOC + " TEXT, "
                + GREEN_LIMIT + " TEXT, "
                + YELLOW_LIMIT + " TEXT, "
                + RED_LIMIT + " TEXT, "
                + SOUND + " TEXT, "
                + AS_MINUTES + " TEXT, "
                + AS_STATIONS + " TEXT, "
                + VIBRATE + " TEXT, "
                + NO_SOUND + " TEXT)";


        db.execSQL(tracker_table);
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




    public void  create_alarm_table(SQLiteDatabase db){
        String alarm_table = "CREATE TABLE IF NOT EXISTS " +ALARMS + " ( "
                + ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ALARM_STATION_TYPE + " TEXT, "
                + ALARM_STATION_NAME + " TEXT, "
                + ALARM_DIRECTION + " TEXT, "
                + ALARM_IS_ON + " TEXT, "
                + TIME + " TEXT, "
                + HOUR + " TEXT, "
                + MIN + " TEXT, "
                + ALARM_MAP_ID + " TEXT, "
                + WILL_REPEAT + " INTEGER, "
                + MON +" INTEGER, "
                + TUES + " INTEGER, "
                + WENS + " INTEGER, "
                + THUR + " INTEGER, "
                + FRI + " INTEGER, "
                + SAT + " INTEGER, "
                + SUN + " INTEGER,"
                + WEEK_LABEL + " TEXT )";

        db.execSQL(alarm_table);
    }



    public void  create_userLocation_table(SQLiteDatabase db){
        String user_location_table = "CREATE TABLE IF NOT EXISTS " +USER_LOCATION + " ( "
                + USER_LOCATION_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_LAT+ " REAL, "
                + USER_LON + " REAL, "
                + HAS_LOCATION + " INTEGER)";

        db.execSQL(user_location_table);
    }



    public void create_main_stations(SQLiteDatabase db){
        String main_stations = "CREATE TABLE IF NOT EXISTS "+ MAIN_STATIONS+
                "(" + MAIN_STATION_TYPE + " TEXT PRIMARY KEY, "+
                NORTHBOUND + " TEXT, " +
                SOUTHBOUND + " TEXT, " +
                EXPRESS + " TEXT)";

        db.execSQL(main_stations);
    }

    public void create_cta_stops(SQLiteDatabase db) {
        String cta_stops = "CREATE TABLE IF NOT EXISTS " + CTA_STOPS +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DIRECTION_ID + " TEXT, " +
                _ID + " TEXT, " +
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
        db.execSQL(cta_stops);

    }

    public void createUserFavorites(SQLiteDatabase db){
        String user_favorites = "CREATE TABLE IF NOT EXISTS " + USER_FAVORITES +
                "(" + FAVORITE_STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FAVORITE_STOP_ID + " TEXT, " +
                FAVORITE_MAP_ID + " TEXT, " +
                FAVORITE_STATION_TYPE + " TEXT, " +
                FAVORITE_STATION_DIRECTION + " TEXT, " +
                FAVORITE_STATION_DIRECTION_LABEL + " TEXT, " +
                ISTRACKING+ " INTEGER, " +
                FAVORITE_STATION_NAME + " TEXT)";


        db.execSQL(user_favorites);
    }


    public void commit(Object item, String table_name){
        if (table_name.equals(CTA_STOPS)){
            CTA_Stops station = (CTA_Stops) item;
            add_cta_stations_to_cta_table(station);

        }else if (table_name.equals(USER_FAVORITES)){
            FavoriteStation station = (FavoriteStation) item;
           add_user_favorites(station);

        }else if (table_name.equals("MAIN_STATIONS")){
            MainStation mainStation = (MainStation) item;
            add_main_station(mainStation);

        }else if (table_name.equals("L_STOPS")){
            L_stops station = (L_stops) item;
            add_L_stop(station);
        }else if (table_name.equals(TRAIN_TRACKER)){
            Train train = (Train) item;
            addTrackingTrain(train);
        }else if (table_name.equals(USER_LOCATION)){
            UserLocation userLocation = (UserLocation) item;
            addUserLocation(userLocation);
        }else if (table_name.equals(ALARMS)){
            Alarm new_alarm = (Alarm) item;
            addAlarm(new_alarm);
        }else if (table_name.equals(USER_SETTINGS)){
            UserSettings new_user_settings = (UserSettings) item;
            addUserSettings(new_user_settings);
        }

    }

    private void addUserSettings(UserSettings new_user_settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_SHARING_LOC,new_user_settings.getIs_sharing_loc());
        values.put(GREEN_LIMIT, new_user_settings.getGreen_limit());
        values.put(RED_LIMIT, "0");
        values.put(AS_MINUTES, new_user_settings.getAsMinutes());
        values.put(AS_STATIONS, new_user_settings.getAsStations());
        values.put(YELLOW_LIMIT, new_user_settings.getYellow_limit());



        db.insert(USER_SETTINGS, null, values);
        db.close();
    }

    private void addAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, alarm.getTime());
        values.put(HOUR, alarm.getHour());
        values.put(MIN, alarm.getMin());
        values.put(ALARM_IS_ON, alarm.getIsOn());
        values.put(ALARM_DIRECTION, alarm.getDirection());
        values.put(ALARM_MAP_ID, alarm.getMap_id());
        values.put(ALARM_STATION_TYPE, alarm.getStationType());
        values.put(ALARM_STATION_NAME, alarm.getStationName());
        values.put(WILL_REPEAT, alarm.getIsRepeating());
        values.put(MON, alarm.getMon());
        values.put(TUES, alarm.getTues());
        values.put(WENS, alarm.getWens());
        values.put(THUR, alarm.getThur());
        values.put(FRI, alarm.getFri());
        values.put(SAT, alarm.getSat());
        values.put(SUN, alarm.getSun());
        values.put(WEEK_LABEL, alarm.getWeekLabel());


        db.insert(ALARMS, null, values);
        db.close();





    }


    private void addUserLocation(UserLocation userLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_LAT, userLocation.getLat());
        values.put(USER_LON, userLocation.getLon());
        values.put(HAS_LOCATION, userLocation.getHasLocation());
        db.insert(USER_LOCATION, null, values);
        db.close();
    }


    private void addTrackingTrain(Train train){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (train!=null) {
            values.put(TRAIN_ID, train.getRn());
            values.put(TRAIN_MAP_ID, train.getTarget_id());
            values.put(TRAIN_ETA, train.getTarget_eta());
            values.put(TRAIN_DIR, train.getTrDr());
            values.put(TRAIN_TYPE, train.getRt());
            values.put(TRAIN_NOTIFIED_BY_ALARM, train.getIsNotifiedByAlarm());
            db.insert(TRAIN_TRACKER, null, values);
        }
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
        values.put(FAVORITE_MAP_ID, cta_data.getStation_id());
        values.put(FAVORITE_STATION_NAME, cta_data.getStation_name());
        values.put(FAVORITE_STATION_TYPE, cta_data.getStation_type());
        values.put(FAVORITE_STATION_DIRECTION, cta_data.getStation_dir());
        values.put(ISTRACKING, cta_data.getTracking());
        values.put(FAVORITE_STATION_DIRECTION_LABEL, cta_data.getStation_dir_label());
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

    public void update(String table_name, String col, String val, String where){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE "+table_name+" SET "+col+"= '"+val+"' WHERE " +where;
        db.execSQL(query);
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
    public ArrayList<Object> excecuteQuery(String cols, String table_name, String condition, String contains, String col_orderBy){
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
        ArrayList<Object> new_record = new ArrayList<>();
        if (table_name.equals(CTA_STOPS)) {
            if (record!=null) {
                for (Object r : record) {
                    HashMap<String, String> station = (HashMap<String, String>) r;
                    Station new_station = new Station();
                    new_station.setIsTarget(false);
                    new_station.setLat(Double.parseDouble(station.get("LAT")));
                    new_station.setLon(Double.parseDouble(station.get("LON")));
                    new_station.setStation_name(station.get("STATION_NAME"));
                    new_station.setStop_name(station.get("STOP_NAME"));
                    new_station.setMap_id(station.get("MAP_ID"));
                    new_station.setStop_id(station.get("STOP_ID"));
                    new_station.setDirection_id(station.get("DIRECTION_ID"));
//                    ArrayList<String> list_of_station_types = findAllStationTypes(station);
//                    new_station.setStation_type_list(list_of_station_types);
//                    if (list_of_station_types.size() > 1) {
//                        new_station.setStation_type(null);
//                    } else {
//                        if (list_of_station_types.size() > 0) {
//                            new_station.setStation_type(list_of_station_types.get(0));
//                        } else {
//                            new_station.setStation_type(null);
//
//                        }
//                    }
                    new_record.add(new_station);
                }
                record = new_record;
            }else{
                Object r = record;
            }
        }else if (table_name.equals(USER_SETTINGS)){
            if (record!=null) {
                for (Object r : record) {
                    HashMap<String, String> settings = (HashMap<String, String>) r;
                    UserSettings userSettings = new UserSettings();
                    userSettings.setIs_sharing_loc(settings.get(IS_SHARING_LOC));
                    userSettings.setGreen_limit(settings.get(GREEN_LIMIT));
                    userSettings.setAsStations(settings.get(AS_STATIONS));
                    userSettings.setAsMinutes(settings.get(AS_MINUTES));
                    userSettings.setYellow_limit(settings.get(YELLOW_LIMIT));
                    new_record.add(userSettings);

                }
                record = new_record;
            }
        }
        return record;
    }

    private ArrayList<String> findAllStationTypes(HashMap<String, String> station) {
        ArrayList<String> list_of_station_types=  new ArrayList<>();
        if (station.get(RED).equals("1")){list_of_station_types.add("Red");}
        if (station.get(BLUE).equals("1")){list_of_station_types.add("Blue");}
        if (station.get(G).equals("1")){list_of_station_types.add("Green");}
        if (station.get(Y).equals("1")){list_of_station_types.add("Yellow");}
        if (station.get(BRN).equals("1")){list_of_station_types.add("Brown");}
        if (station.get(PINK).equals("1")){list_of_station_types.add("Pink");}
        if (station.get(ORG).equals("1")){list_of_station_types.add("Orange");}
        if (station.get(P).equals("1")){list_of_station_types.add("Purple");}

        return  list_of_station_types;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

package com.gl.simpleweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimpleWeatherDBHelper extends SQLiteOpenHelper {
    private final static String CREATE_TABLE_PROVINCE = "CREATE TABLE PROVINCE(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "PROVINCE_NAME TEXT NOT NULL)";
    private final static String CREATE_TABLE_CITY = "CREATE TABLE CITY(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "CITY_NAME TEXT NOT NULL,PROVINCE_ID INTEGER," +
            "FOREIGN KEY (PROVINCE_ID) REFERENCES PROVINCE(ID))";
    private final static String CREATE_TABLE_COUNTY = "CREATE TABLE COUNTY(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "COUNTY_NAME TEXT NOT NULL," +
            "INNER_CODE TEXT NOT NULL," +
            "CITY_ID INTEGER," +
            "FOREIGN KEY (CITY_ID) REFERENCES CITY(ID))";

    public SimpleWeatherDBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROVINCE);
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

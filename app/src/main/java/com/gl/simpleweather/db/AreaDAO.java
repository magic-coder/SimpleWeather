package com.gl.simpleweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gl.simpleweather.beans.County;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AreaDAO {
    private static AreaDAO areaDAO = null;
    private SQLiteDatabase db = null;

    public synchronized static AreaDAO getInstance(Context context) {
        if (areaDAO == null) {
            areaDAO = new AreaDAO(context);
        }
        return areaDAO;
    }

    public AreaDAO(Context context) {
        SimpleWeatherDBHelper dbHelper = new SimpleWeatherDBHelper(context,
                "simple_weather", 1);
        db = dbHelper.getWritableDatabase();
    }

    public void saveProvinces(Set<String> provinces) {
        db.execSQL("DELETE FROM PROVINCE");
        Iterator<String> iterator = provinces.iterator();
        while (iterator.hasNext()) {
            db.execSQL("INSERT INTO PROVINCE (PROVINCE_NAME) VALUES (?)", new String[]{
                    iterator.next()});
        }
    }

    public Set<String> getProvinces() {
        Set<String> provinces = new HashSet<String>();
        Cursor cursor = db.rawQuery("SELECT PROVINCE_NAME FROM PROVINCE", null);
        while (cursor.moveToNext()) {
            provinces.add(cursor.getString(0));
        }
        if (provinces.size() == 0) return null;
        return provinces;
    }

    public int getProvinceId(String provinceName) {
        Cursor cursor = db.rawQuery("SELECT ID FROM PROVINCE WHERE PROVINCE_NAME = ?", new String[]{provinceName});
        while (cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    public void saveCities(Set<String> cities, String provinceName) {
        Iterator<String> iterator = cities.iterator();
        while (iterator.hasNext()) {
            db.execSQL("INSERT INTO CITY(CITY_NAME,PROVINCE_ID) " +
                            "VALUES (?,(SELECT DISTINCT ID FROM PROVINCE WHERE PROVINCE_NAME=?))",
                    new String[]{iterator.next(), provinceName});
        }
    }

    public Set<String> getCities(String provinceName) {
        Set<String> cities = new HashSet<String>();
        Cursor cursor = db.rawQuery("SELECT CITY_NAME FROM CITY C,PROVINCE P " +
                        "WHERE C.PROVINCE_ID=P.ID AND P.PROVINCE_NAME=?",
                new String[]{provinceName});
        while (cursor.moveToNext()) {
            cities.add(cursor.getString(0));
        }
        if (cities.size() == 0) return null;
        return cities;
    }

    public void saveCounties(List<County> counties, String provinceName, String cityName) {
        for (County county : counties) {
            db.execSQL("INSERT INTO COUNTY(COUNTY_NAME,INNER_CODE,CITY_ID) " +
                    "VALUES (?,?,(SELECT DISTINCT C.ID FROM CITY C,PROVINCE P WHERE C.PROVINCE_ID=P.ID AND " +
                    "P.PROVINCE_NAME=? AND C.CITY_NAME=?))", new String[]{county.getCountyName(), county.getCountyId(), provinceName, cityName});
        }
    }

    public List<County> getCounties(String provinceName, String cityName) {
        List<County> counties = new ArrayList<County>();
        Cursor cursor = db.rawQuery("SELECT COUNTY_NAME,INNER_CODE FROM COUNTY,CITY,PROVINCE " +
                "WHERE PROVINCE.ID=CITY.PROVINCE_ID AND COUNTY.CITY_ID=CITY.ID " +
                "AND PROVINCE.PROVINCE_NAME=? AND CITY.CITY_NAME=?", new String[]{provinceName, cityName});
        while (cursor.moveToNext()) {
            County county = new County();
            county.setCountyName(cursor.getString(0));
            county.setCountyId(cursor.getString(1));
            counties.add(county);
        }
        if (counties.size() == 0) return null;
        return counties;
    }

    public String getCountyId(String provinceName, String cityName) {
        Cursor cursor = db.rawQuery("SELECT INNER_CODE FROM COUNTY,CITY,PROVINCE " +
                        "WHERE PROVINCE.ID=CITY.PROVINCE_ID AND COUNTY.CITY_ID=CITY.ID " +
                        "AND PROVINCE.PROVINCE_NAME='" + provinceName + "%' AND CITY.CITY_NAME='" + cityName + "%'",
                new String[]{provinceName, cityName});
        while (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        return null;
    }
}

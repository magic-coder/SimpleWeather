package com.gl.simpleweather.beans;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by gl on 2016/1/23.
 */
public class Configuration {
    public static final int AUTO_UPDATE_NONE = 0;
    public static final int AUTO_UPDATE_30 = 1;
    public static final int AUTO_UPDATE_60 = 2;
    public static final int AUTO_UPDATE_120 = 3;
    public static final int AUTO_UPDATE_180 = 4;
    private SharedPreferences sharedPreferences = null;

    public Integer getAutoUpdate() {
        return sharedPreferences.getInt("auto_update", Configuration.AUTO_UPDATE_NONE);
    }

    public void setAutoUpdate(Integer autoUpdate) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("auto_update", autoUpdate);
        editor.apply();
    }

    public long getLastNotify() {
        return sharedPreferences.getLong("last_notify", 0);
    }

    public void setLastNotify(long lastNotify) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_notify", lastNotify);
        editor.apply();
    }

    public String getCityId() {
        return sharedPreferences.getString("city_id", null);
    }

    public void setCityId(String cityId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city_id", cityId);
        editor.apply();
    }

    public Boolean getDailyNotification() {
        return sharedPreferences.getBoolean("notification", false);
    }

    public void setDailyNotification(Boolean dailyNotification) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notification", dailyNotification);
        editor.apply();
    }

    public Boolean getFahrenheit() {
        return sharedPreferences.getBoolean("fahrenheit", false);
    }

    public void setFahrenheit(Boolean fahrenheit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("fahrenheit", fahrenheit);
        editor.apply();
    }

    public Long getLastUpdate() {
        return sharedPreferences.getLong("last_update", 0);
    }

    public void setLastUpdate(Long lastUpdate) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_update", lastUpdate);
        editor.apply();
    }

    public Boolean getBlurBackground() {
        return sharedPreferences.getBoolean("blur_background", false);
    }

    public void setBlurBackground(Boolean showBackground) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("blur_background", showBackground);
        editor.apply();
    }

    private String cityId;
    private Boolean cachedCity;
    private Long lastUpdate;
    private Integer autoUpdate;
    private Boolean fahrenheit;
    private Boolean dailyNotification;
    private Boolean showBackground;


    private Context context;
    private static Configuration configuration = null;

    public synchronized static Configuration getInstance(Context context) {
        if (configuration == null) {
            configuration = new Configuration(context);
        }
        return configuration;
    }

    public Configuration(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}

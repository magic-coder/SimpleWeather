package com.gl.simpleweather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.gl.simpleweather.beans.Configuration;
import com.gl.simpleweather.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WeatherApplication extends Application {
    public static Configuration configuration = null;
    public static String httpsUrl = "https://api.heweather.com/x3/weather?key=c16123bb87f54d22a658ab48039ba991&cityid=";
    public final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(getClass().getSimpleName(), "AppRun");
        configuration = new Configuration(this);
    }
}

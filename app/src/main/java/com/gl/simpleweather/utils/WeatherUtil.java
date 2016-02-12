package com.gl.simpleweather.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.gl.simpleweather.beans.DailyForecastWeather;
import com.gl.simpleweather.beans.HourlyForecastWeather;
import com.gl.simpleweather.beans.TodayWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class WeatherUtil {
    public static TodayWeather getTodayWeather(String jsonData)
            throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonData);
        if (!jsonObj.has("HeWeather data service 3.0"))
            return null;
        JSONArray jsonArr = jsonObj.getJSONArray("HeWeather data service 3.0");
        jsonObj = jsonArr.getJSONObject(0);
        JSONObject aqiCityObj = null;
        // 只有城市才有aqi
        if (jsonObj.has("aqi"))
            aqiCityObj = jsonObj.getJSONObject("aqi").getJSONObject("city");
        JSONObject basicObj = jsonObj.getJSONObject("basic");
        JSONObject updateObj = basicObj.getJSONObject("update");
        JSONArray dailyForecastArr = jsonObj.getJSONArray("daily_forecast");
        JSONArray hourlyForecastArr = jsonObj.getJSONArray("hourly_forecast");
        JSONObject nowObj = jsonObj.getJSONObject("now");
        JSONObject windObj = nowObj.getJSONObject("wind");
        JSONObject condObj = nowObj.getJSONObject("cond");
        String statusStr = jsonObj.getString("status");
        JSONObject suggestionObj = jsonObj.getJSONObject("suggestion");
        JSONObject drsgObj = suggestionObj.getJSONObject("drsg");// 穿衣
        JSONObject uvObj = suggestionObj.getJSONObject("uv");// 紫外线
        if (!statusStr.equals("ok"))
            return null;
        TodayWeather todayWeather = new TodayWeather();
        if (aqiCityObj != null) {
            todayWeather.setAqi(aqiCityObj.getInt("aqi"));
            todayWeather.setQlty(aqiCityObj.getString("qlty"));
        }
        todayWeather.setCityName(basicObj.getString("city"));
        todayWeather.setCityId(basicObj.getString("id"));
        todayWeather.setReleaseTime(updateObj.getString("loc"));
        todayWeather.dailyForecastWeather = new ArrayList<DailyForecastWeather>();
        // 4天的天气预测
        for (int i = 0; i < 4; i++) {
            JSONObject dailyObj = dailyForecastArr.getJSONObject(i);
            JSONObject dailyCondObj = dailyObj.getJSONObject("cond");
            JSONObject dailyTmpObj = dailyObj.getJSONObject("tmp");

            DailyForecastWeather dailyForecastWeather = new DailyForecastWeather();
            dailyForecastWeather.setDate(dailyObj.getString("date"));
            dailyForecastWeather.setWeatherCode(dailyCondObj
                    .getString("code_d"));
            dailyForecastWeather.setWeatherTxt(dailyCondObj.getString("txt_d"));
            dailyForecastWeather.setMaxTemp(dailyTmpObj.getInt("max"));
            dailyForecastWeather.setMinTemp(dailyTmpObj.getInt("min"));
            todayWeather.dailyForecastWeather.add(dailyForecastWeather);
        }
        todayWeather.hourlyForecastWeather = new ArrayList<HourlyForecastWeather>();
        // 5次小时预测,可能不足5次
        for (int i = 0; i < hourlyForecastArr.length(); i++) {
            JSONObject hourlyObj = hourlyForecastArr.getJSONObject(i);
            HourlyForecastWeather hourlyForecastWeather = new HourlyForecastWeather();
            hourlyForecastWeather.setDate(hourlyObj.getString("date"));
            hourlyForecastWeather.setTemp(hourlyObj.getInt("tmp"));
            hourlyForecastWeather.setPop(hourlyObj.getInt("pop"));
            todayWeather.hourlyForecastWeather.add(hourlyForecastWeather);
        }
        LogUtil.i("WeatherUtil", "jsonData" + jsonData.length());
        todayWeather.setWeatherCode(condObj.getString("code"));
        todayWeather.setWeatherTxt(condObj.getString("txt"));
        if (nowObj.has("fl"))//偶尔不存在
            todayWeather.setFeltAirTemp(nowObj.getInt("fl"));
        todayWeather.setTemp(nowObj.getInt("tmp"));
        todayWeather.setHumidity(nowObj.getInt("hum"));
        todayWeather.setWindDegree(windObj.getString("sc"));
        todayWeather.setWindDir(windObj.getString("dir"));
        todayWeather.setUvBrf(uvObj.getString("brf"));
        todayWeather.setUvTxt(uvObj.getString("txt"));
        todayWeather.setDrsgBrf(drsgObj.getString("brf"));
        todayWeather.setDrsgTxt(drsgObj.getString("txt"));

        return todayWeather;
    }

    public static void cacheWeatherIcon(Context context, String code)
            throws IOException {
        String fileName = code + ".png";
        File file = new File(context.getFilesDir().getPath() + "/" + fileName);
        if (!file.exists()) {
            Bitmap bitmap = FileUtil
                    .getBitMap("http://files.heweather.com/cond_icon/"
                            + fileName);
            FileUtil.saveBitmap(context, fileName, bitmap);
            LogUtil.d("MainActivity", fileName + "不存在");
        } else {
            LogUtil.d("MainActivity", fileName + "已存在");
        }
    }

    public static String getTempCToF(Integer c, Boolean f) {
        if (f) {
            return (int) (c * 1.8 + 32) + "℉";
        } else {
            return c + "℃";
        }
    }

    public static String getTimeByCalendar(Calendar cal) {
        String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        if (hour.length() == 1) hour = "0" + hour;
        if (minute.length() == 1) minute = "0" + minute;
        return hour + ":" + minute;
    }
}

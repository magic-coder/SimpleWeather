package com.gl.simpleweather.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.gl.simpleweather.R;
import com.gl.simpleweather.WeatherApplication;
import com.gl.simpleweather.activities.WeatherActivity;
import com.gl.simpleweather.beans.Configuration;
import com.gl.simpleweather.beans.TodayWeather;
import com.gl.simpleweather.utils.FileUtil;
import com.gl.simpleweather.utils.LogUtil;
import com.gl.simpleweather.utils.NetworkUtil;
import com.gl.simpleweather.utils.WeatherUtil;

/**
 * Created by gl on 2016/1/24.
 */
public class WeatherService extends IntentService {
    private Configuration configuration;
    private final String TAG = getClass().getSimpleName();

    public WeatherService() {
        super("WeatherService");
        configuration = WeatherApplication.configuration;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                Thread.sleep(1000);
                //更新时间间隔
                long interval = Long.MAX_VALUE;
                long lastUpdate = configuration.getLastUpdate();//上次更新时间
                long now = System.currentTimeMillis();
                String cityId = configuration.getCityId();
                switch (configuration.getAutoUpdate()) {
                    case Configuration.AUTO_UPDATE_NONE:
                        break;
                    case Configuration.AUTO_UPDATE_30:
                        interval = 30 * 60 * 1000;
                        break;
                    case Configuration.AUTO_UPDATE_60:
                        interval = 60 * 60 * 1000;
                        break;
                    case Configuration.AUTO_UPDATE_120:
                        interval = 120 * 60 * 1000;
                        break;
                    case Configuration.AUTO_UPDATE_180:
                        interval = 180 * 60 * 1000;
                        break;
                }
                //两次更新时间间隔超过设定时间，进行更新
                if (now - lastUpdate > interval) {
                    LogUtil.i(TAG, "到了时间，从互联网下载JSON，CITYID：" + cityId);
                    configuration.setLastUpdate(now);//设定新的更新时间
                    //从互联网下载，保存json文件
                    String jsonResult = NetworkUtil.requestS(WeatherApplication.httpsUrl + cityId);
                    FileUtil.saveText(this, cityId + ".json", jsonResult);
                }

                if (configuration.getDailyNotification()) {
                    if (now - configuration.getLastNotify() > 1000 * 60 * 60 * 8) {
                        String city_id = configuration.getCityId();
                        if (city_id != null) {
                            String jsonResult = NetworkUtil.requestS(WeatherApplication.httpsUrl + configuration.getCityId());
                            TodayWeather todayWeather = WeatherUtil.getTodayWeather(jsonResult);
                            WeatherUtil.cacheWeatherIcon(this,
                                    todayWeather.getWeatherCode());
                            LogUtil.i(TAG, "每日天气通知");
                            Intent weatherIntent = new Intent(this, WeatherActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(this)
                                            .setTicker("今日天气")
                                            .setAutoCancel(true)
                                            .setSmallIcon(R.drawable.ic_launcher)
                                            .setContentTitle(todayWeather.getCityName())
                                            .setContentText(todayWeather.getWeatherTxt() + "\t" + WeatherUtil.getTempCToF(todayWeather.getTemp(), configuration.getFahrenheit()))
                                            .setDefaults(Notification.DEFAULT_ALL);
                            Intent resultIntent = new Intent(this, WeatherActivity.class);
                            PendingIntent resultPendingIntent =
                                    PendingIntent.getActivity(
                                            this,
                                            0,
                                            resultIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 001;
                            NotificationManager mNotifyMgr =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                            configuration.setLastNotify(now);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

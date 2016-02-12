package com.gl.simpleweather.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.gl.simpleweather.R;
import com.gl.simpleweather.WeatherApplication;
import com.gl.simpleweather.beans.Configuration;
import com.gl.simpleweather.utils.LogUtil;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

/**
 * Created by gl on 2016/1/22.
 */
public class SettingActivity extends BaseActivity implements Switch.OnCheckedChangeListener {
    private Configuration configuration = null;
    private Spinner spnUpdate;
    private Switch swFahrenheit;
    private Switch swWeatherNotification;
    private Switch swBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.init(this, 1, 0, null);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_setting)
                .setSwipeBackView(R.layout.swipeback_default);
        ThemeManager.getInstance().setCurrentTheme(0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.as_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spnUpdate = (Spinner) findViewById(R.id.as_spn_update);
        swFahrenheit = (Switch) findViewById(R.id.as_sw_fahrenheit);
        swWeatherNotification = (Switch) findViewById(R.id.as_sw_weather_notification);
        swBackground = (Switch) findViewById(R.id.as_sw_background);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_spn, getResources().getStringArray(R.array.update_interval));
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        spnUpdate.setAdapter(adapter);
        //初始化配置数据
        configuration = WeatherApplication.configuration;
        spnUpdate.setSelection(configuration.getAutoUpdate());
        swFahrenheit.setChecked(configuration.getFahrenheit());
        swWeatherNotification.setChecked(configuration.getDailyNotification());
        swBackground.setChecked(configuration.getBlurBackground());
        //设置监听器
        swFahrenheit.setOnCheckedChangeListener(this);
        swWeatherNotification.setOnCheckedChangeListener(this);
        swBackground.setOnCheckedChangeListener(this);
        spnUpdate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                configuration.setLastUpdate(0l);//重置上次更细时间
                configuration.setAutoUpdate(position);
            }
        });
    }

    @Override
    public void onCheckedChanged(Switch view, boolean checked) {
        switch (view.getId()) {
            case R.id.as_sw_fahrenheit:
                configuration.setFahrenheit(checked);
                break;
            case R.id.as_sw_weather_notification:
                configuration.setDailyNotification(checked);
                break;
            case R.id.as_sw_background:
                configuration.setBlurBackground(checked);
                break;
        }
        Snackbar.make(view, "设置成功，重启应用生效", Snackbar.LENGTH_SHORT).show();
    }
}

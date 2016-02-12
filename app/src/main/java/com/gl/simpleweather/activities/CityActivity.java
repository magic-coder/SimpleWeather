package com.gl.simpleweather.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gl.simpleweather.R;
import com.gl.simpleweather.WeatherApplication;
import com.gl.simpleweather.beans.Configuration;
import com.gl.simpleweather.beans.County;
import com.gl.simpleweather.db.AreaDAO;
import com.gl.simpleweather.utils.AreaUtil;
import com.gl.simpleweather.utils.LogUtil;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.rey.material.widget.ProgressView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CityActivity extends BaseActivity {
    private ListView lv = null;
    private ProgressView pgLoading;
    private AreaDAO areaDAO = null;
    private List<County> counties = null;
    private String selectedProvince = null;
    private String selectedCity = null;
    private static final int LEVEL_PROVINCE = 1;
    private static final int LEVEL_CITY = 2;
    private static final int LEVEL_COUNTY = 3;
    private int currentLevel = 0;
    private static final int REP_SET_CITY = 2;
    private final String TAG = getClass().getSimpleName();
    private String htmlCode;
    private Configuration configuration = WeatherApplication.configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_city)
                .setSwipeBackView(R.layout.swipeback_default);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ac_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backLevel();
            }
        });

        StringBuilder response = new StringBuilder();
        Resources resources = CityActivity.this.getResources();
        AssetManager assetManager = resources.getAssets();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("citylist.html")));
            String line = null;
            LogUtil.i(TAG, "读取HTML开始");
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            LogUtil.i(TAG, "读取HTML结束");
            htmlCode = response.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        areaDAO = AreaDAO.getInstance(this);

        pgLoading = (ProgressView) findViewById(R.id.ac_pg_loading);
        //pgLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        lv = (ListView) findViewById(R.id.ac_lv_city);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = (String) lv.getItemAtPosition(position);
                    new MyLoader().execute("fillCity", selectedProvince);
                    currentLevel = LEVEL_CITY;
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = (String) lv.getItemAtPosition(position);
                    new MyLoader().execute("fillCounty", selectedProvince, selectedCity);
                    currentLevel = LEVEL_COUNTY;
                } else if (currentLevel == LEVEL_COUNTY) {
                    if (counties != null) {
                        Intent intent = new Intent();
                        intent.putExtra("city_id", counties.get(position).getCountyId());
                        setResult(REP_SET_CITY, intent);
                        finish();
                    }
                }
            }
        });
        new MyLoader().execute("fillProvince");
        currentLevel = LEVEL_PROVINCE;
    }

    private void backLevel() {
        if (currentLevel == LEVEL_COUNTY) {
            new MyLoader().execute("fillCity", selectedProvince);
            currentLevel = LEVEL_CITY;
        } else if (currentLevel == LEVEL_CITY) {
            new MyLoader().execute("fillProvince");
            currentLevel = LEVEL_PROVINCE;
        } else if (currentLevel == LEVEL_PROVINCE) {
            //首次运行，未选择城市，直接关闭全部Activity
            if (configuration.getCityId() == null) {
                ActivityCollector.finishAll();
            } else {
                finish();
            }
            currentLevel = 0;
        }
    }

    @Override
    public void onBackPressed() {
        backLevel();
    }

    private class MyLoader extends AsyncTask<String, Integer, Boolean> {
        ArrayAdapter<String> adapter = null;

        @Override
        protected void onPreExecute() {
            lv.setEnabled(false);
            pgLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params[0].equals("fillProvince")) {
                Set<String> provinces = areaDAO.getProvinces();
                if (provinces == null) {
                    LogUtil.i(TAG, "fillProvince:解析HTML");
                    provinces = AreaUtil.getProvinces(htmlCode);
                    areaDAO.saveProvinces(provinces);
                } else {
                    LogUtil.i(TAG, "fillProvince:读取数据库");
                }
                adapter = new ArrayAdapter<String>(CityActivity.this,
                        android.R.layout.simple_list_item_1, new ArrayList<String>(provinces));
            } else if (params[0].equals("fillCity")) {
                String provinceName = params[1];
                Set<String> cities = areaDAO.getCities(provinceName);
                if (cities == null) {
                    LogUtil.i(TAG, "fillCity:解析HTML");
                    cities = AreaUtil.getCities(htmlCode, provinceName);
                    areaDAO.saveCities(cities, provinceName);
                } else {
                    LogUtil.i(TAG, "fillCity:读取数据库");
                }
                adapter = new ArrayAdapter<String>(CityActivity.this,
                        android.R.layout.simple_list_item_1, new ArrayList<String>(cities));
            } else if (params[0].equals("fillCounty")) {
                String province = params[1];
                String city = params[2];
                counties = areaDAO.getCounties(province, city);
                if (counties == null) {
                    counties = AreaUtil.getCounties(htmlCode, province, city);
                    areaDAO.saveCounties(counties, province, city);
                    LogUtil.i(TAG, "fillCounty:解析HTML");
                } else {
                    LogUtil.i(TAG, "fillCounty:读取数据库");
                }
                List<String> l = new ArrayList<String>();
                for (County county : counties) {
                    l.add(county.getCountyName());
                }
                adapter = new ArrayAdapter<String>(
                        CityActivity.this, android.R.layout.simple_list_item_1, l);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            lv.setAdapter(adapter);
            lv.setSelection(0);
            pgLoading.setVisibility(View.INVISIBLE);
            lv.setEnabled(true);
        }
    }

    ;
}

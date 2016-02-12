package com.gl.simpleweather.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gl.simpleweather.R;
import com.gl.simpleweather.WeatherApplication;
import com.gl.simpleweather.beans.Configuration;
import com.gl.simpleweather.beans.DailyForecastWeather;
import com.gl.simpleweather.beans.HourlyForecastWeather;
import com.gl.simpleweather.beans.TodayWeather;
import com.gl.simpleweather.db.AreaDAO;
import com.gl.simpleweather.services.WeatherService;
import com.gl.simpleweather.utils.AreaUtil;
import com.gl.simpleweather.utils.FastBlur;
import com.gl.simpleweather.utils.FileUtil;
import com.gl.simpleweather.utils.LogUtil;
import com.gl.simpleweather.utils.NetworkUtil;
import com.gl.simpleweather.utils.WeatherUtil;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class WeatherActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RelativeLayout rlFrame;
    private PullToRefreshView mPullToRefreshView;
    private ScrollView sv;
    private LinearLayout llAqi;
    private RelativeLayout rlCity;
    private TextView tvCity;// 城市
    private TextView tvRelease;// 发布时间
    private TextView tvNowWeather;// 当前天气文字描述
    private TextView tvTodayTemp;// 当天天气最高最低气温
    private TextView tvNowTemp;// 当前气温
    private TextView tvAqi;// 空气质量指数
    private TextView tvQuality;// 空气质量文字描述
    // 3，6，9，12，15小时预测的时间
    private TextView tvNextThree, tvNextSix, tvNextNine, tvNextTwelve,
            tvNextFifteen;
    // 3，6，9，12，15小时预测的江水几率
    private TextView tvNextThreePop, tvNextSixPop, tvNextNinePop,
            tvNextTwelvePop, tvNextFifteenPop;
    // 3，6，9，12，15小时预测的温度
    private TextView tvNextThreeTemp, tvNextSixTemp, tvNextNineTemp,
            tvNextTwelveTemp, tvNextFifteenTemp;
    // 今天以及未来三天的文字描述、最高气温、最低气温
    private TextView tvTodayTempMax, tvTodayTempMin;
    private TextView tvTomorrow, tvTomorrowTempMax, tvTomorrowTempMin;
    private TextView tvThirdDay, tvThirdDayTempMax, tvThirdDayTempMin;
    private TextView tvFourthDay, tvFourthDayTempMax, tvFourthDayTempMin;
    // 详细信息
    private TextView tvFeltAirTemp;// 体感温度
    private TextView tvHumidity;// 湿度
    private TextView tvWind;// 风力风向
    private TextView tvUvIndex;// 紫外线指数
    private TextView tvDressingIndex;// 穿衣建议
    private ImageView ivNowWeather;// 当前天气图标
    // 3，6，9，12，15小时预测的天气图标
    private ImageView ivNextThree, ivNextSix, ivNextNine, ivNextTwelve,
            ivNextFifteen;
    // 今天以及未来三天的天气图标
    private ImageView ivTodayWeather, ivTomorrowWeather, ivThirdWeather,
            ivFourthWeather;
    private Button btnSwitchCity;
    private final String TAG = getClass().getSimpleName();
    private String cityId;
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    private AreaDAO areaDAO;
    private Configuration configuration;
    private static final int REP_SET_CITY = 2;
    private final static int REQ_GET_CITY = 1;

    private void initViewObject() {
        rlFrame = (RelativeLayout) findViewById(R.id.aw_rl_frame);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.aw_pull_to_refresh);
        sv = (ScrollView) findViewById(R.id.aw_sv);
        llAqi = (LinearLayout) findViewById(R.id.aw_ll_aqi);
        rlCity = (RelativeLayout) findViewById(R.id.aw_rl_city);
        tvCity = (TextView) findViewById(R.id.aw_tv_city);
        tvRelease = (TextView) findViewById(R.id.aw_tv_release);
        tvNowWeather = (TextView) findViewById(R.id.aw_tv_now_weather);
        tvTodayTemp = (TextView) findViewById(R.id.aw_tv_today_temp);
        tvNowTemp = (TextView) findViewById(R.id.aw_tv_now_temp);
        tvAqi = (TextView) findViewById(R.id.aw_tv_aqi);
        tvQuality = (TextView) findViewById(R.id.aw_tv_quality);
        tvNextThree = (TextView) findViewById(R.id.aw_tv_next_three);
        tvNextSix = (TextView) findViewById(R.id.aw_tv_next_six);
        tvNextNine = (TextView) findViewById(R.id.aw_tv_next_nine);
        tvNextTwelve = (TextView) findViewById(R.id.aw_tv_next_twelve);
        tvNextFifteen = (TextView) findViewById(R.id.aw_tv_next_fifteen);
        tvNextThreePop = (TextView) findViewById(R.id.aw_tv_next_three_pop);
        tvNextSixPop = (TextView) findViewById(R.id.aw_tv_next_six_pop);
        tvNextNinePop = (TextView) findViewById(R.id.aw_tv_next_nine_pop);
        tvNextTwelvePop = (TextView) findViewById(R.id.aw_tv_next_twelve_pop);
        tvNextFifteenPop = (TextView) findViewById(R.id.aw_tv_next_fifteen_pop);
        tvNextThreeTemp = (TextView) findViewById(R.id.aw_tv_next_three_temp);
        tvNextSixTemp = (TextView) findViewById(R.id.aw_tv_next_six_temp);
        tvNextNineTemp = (TextView) findViewById(R.id.aw_tv_next_nine_temp);
        tvNextTwelveTemp = (TextView) findViewById(R.id.aw_tv_next_twelve_temp);
        tvNextFifteenTemp = (TextView) findViewById(R.id.aw_tv_next_fifteen_temp);
        tvTodayTempMax = (TextView) findViewById(R.id.aw_tv_today_temp_max);
        tvTodayTempMin = (TextView) findViewById(R.id.aw_tv_today_temp_min);
        tvTomorrow = (TextView) findViewById(R.id.aw_tv_tommorrow);
        tvTomorrowTempMax = (TextView) findViewById(R.id.aw_tv_tommorrow_temp_max);
        tvTomorrowTempMin = (TextView) findViewById(R.id.aw_tv_tommorrow_temp_min);
        tvThirdDay = (TextView) findViewById(R.id.aw_tv_third_day);
        tvThirdDayTempMax = (TextView) findViewById(R.id.aw_tv_third_day_temp_max);
        tvThirdDayTempMin = (TextView) findViewById(R.id.aw_tv_third_day_temp_min);
        tvFourthDay = (TextView) findViewById(R.id.aw_tv_fourth_day);
        tvFourthDayTempMax = (TextView) findViewById(R.id.aw_tv_fourth_day_temp_max);
        tvFourthDayTempMin = (TextView) findViewById(R.id.aw_tv_fourth_day_temp_min);
        tvFeltAirTemp = (TextView) findViewById(R.id.aw_tv_felt_air_temp);
        tvHumidity = (TextView) findViewById(R.id.aw_tv_humidity);
        tvWind = (TextView) findViewById(R.id.aw_tv_wind);
        tvUvIndex = (TextView) findViewById(R.id.aw_tv_uv_index);
        tvDressingIndex = (TextView) findViewById(R.id.aw_tv_dressing_index);
        ivNowWeather = (ImageView) findViewById(R.id.aw_iv_now_weather);
        ivNextThree = (ImageView) findViewById(R.id.aw_iv_next_three);
        ivNextSix = (ImageView) findViewById(R.id.aw_iv_next_six);
        ivNextNine = (ImageView) findViewById(R.id.aw_iv_next_nine);
        ivNextTwelve = (ImageView) findViewById(R.id.aw_iv_next_twelve);
        ivNextFifteen = (ImageView) findViewById(R.id.aw_iv_next_fifteen);
        ivTodayWeather = (ImageView) findViewById(R.id.aw_iv_today_weather);
        ivTomorrowWeather = (ImageView) findViewById(R.id.aw_iv_tommorrow_weather);
        ivThirdWeather = (ImageView) findViewById(R.id.aw_iv_third_day_weather);
        ivFourthWeather = (ImageView) findViewById(R.id.aw_iv_fourth_day_weather);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.aw_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        configuration = WeatherApplication.configuration;
        areaDAO = AreaDAO.getInstance(this);
        cityId = WeatherApplication.configuration.getCityId();
        //释放背景图片
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                //随机找一张背景图片
                File folderPath = new File(getFilesDir().getPath() + "/backgrounds");
                Random random = new Random();
                int index = random.nextInt(folderPath.listFiles().length);
                Bitmap background = BitmapFactory.decodeFile(folderPath.listFiles()[index].getPath());
                LogUtil.i(TAG, "Loading:" + folderPath.listFiles()[index].getPath());
                if (configuration.getBlurBackground()) {
                    rlFrame.setBackgroundDrawable(new BitmapDrawable(WeatherActivity.this.getResources(), FastBlur.doBlur(background, 0.1f
                            , 2)));
                } else {
                    rlFrame.setBackgroundDrawable(new BitmapDrawable(WeatherActivity.this.getResources(), background));
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                File folderPath = new File(getFilesDir().getPath() + "/backgrounds");
                if (!folderPath.exists()) {
                    folderPath.mkdir();
                }
                try {
                    Resources resources = WeatherActivity.this.getResources();
                    AssetManager assetManager = resources.getAssets();
                    String[] picsName = assetManager.list("backgrounds");
                    for (String fileName : picsName) {
                        File picFile = new File(folderPath + "/" + fileName);
                        if (!picFile.exists()) {
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(assetManager.open("backgrounds/" + fileName));
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(picFile));
                            byte[] buffer = new byte[4096];
                            while (bufferedInputStream.read(buffer) != -1) {
                                bufferedOutputStream.write(buffer);
                            }
                            bufferedOutputStream.close();
                            bufferedInputStream.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }.execute();
        // 找到控件对象,初始化控件数据
        initViewObject();
        initViewData();
        initLocation();
        initService();
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (cityId != null) {
                    new GetDataTask().execute(cityId);
                } else {
                    Snackbar.make(mPullToRefreshView, "您尚未选择城市", Snackbar.LENGTH_SHORT).show();
                    mPullToRefreshView.setRefreshing(false);
                }
            }
        });
        rlCity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,
                        CityActivity.class);
                startActivityForResult(intent, REQ_GET_CITY);
            }
        });
        //cityId为空，启动选择城市界面，否则刷新天气数据
        if (cityId == null) {
            Intent intent = new Intent(WeatherActivity.this, CityActivity.class);
            startActivityForResult(intent, REQ_GET_CITY);
        } else {
            new GetDataTask().execute(cityId);
        }
    }

    private void initService() {
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Address address = location.getAddress();
            int locType = location.getLocType();
            //取省的前两个字
            if (locType == 161) {
                LogUtil.d(TAG, "定位成功");
                StringBuilder response = new StringBuilder();
                Resources resources = WeatherActivity.this.getResources();
                AssetManager assetManager = resources.getAssets();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("citylist.html")));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String provinceName = address.province.substring(0, 2);
                String cityName = address.city.substring(0, 2);
                cityId = AreaUtil.getCountyId(response.toString(), provinceName, cityName);
                new GetDataTask().execute(cityId);
            } else {
                LogUtil.d(TAG, "定位失败 error:" + locType);
                Snackbar.make(mPullToRefreshView, "自动定位失败，请打开WI-FI或数据连接", Snackbar.LENGTH_SHORT).show();
            }
            mLocationClient.stop();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_change_location: {
                Intent intent = new Intent(WeatherActivity.this,
                        CityActivity.class);
                startActivityForResult(intent, REQ_GET_CITY);
                break;
            }
            case R.id.nav_current_location: {
                mLocationClient.start();
                break;
            }
            case R.id.nav_setting: {
                Intent intent = new Intent(WeatherActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_share:
                File file = new File(WeatherActivity.this.getPackageCodePath());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(intent);
                break;
            case R.id.nav_send_suggestions: {
                final com.rey.material.widget.EditText contentEdt = new com.rey.material.widget.EditText(this);
                contentEdt.setMinLines(5);
                final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("请输入")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(contentEdt)
                        .setPositiveButton("发送", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (contentEdt.getText().toString().trim().length() == 0) {
                                    Snackbar.make(mPullToRefreshView, "请输入内容", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                new AsyncTask<String, Integer, Boolean>() {
                                    @Override
                                    protected void onPostExecute(Boolean aBoolean) {
                                        Snackbar.make(mPullToRefreshView, "反馈成功，感谢您的参与", Snackbar.LENGTH_LONG).show();
                                    }

                                    @Override
                                    protected Boolean doInBackground(String... params) {
                                        try {
                                            Properties props = new Properties();
                                            props.setProperty("mail.debug", "true");
                                            props.setProperty("mail.smtp.auth", "true");
                                            props.setProperty("mail.host", "smtp.163.com");
                                            props.setProperty("mail.transport.protocol", "smtp");
                                            Session session = Session.getInstance(props);
                                            Message msg = new MimeMessage(session);
                                            msg.setSubject("简约天气 - 用户反馈");
                                            msg.setText(params[0]);
                                            msg.setFrom(new InternetAddress("pwrliang@163.com"));
                                            Transport transport = session.getTransport();
                                            transport.connect("smtp.163.com", "pwrliang@163.com", "ddgeng_93418");
                                            transport.sendMessage(msg, new javax.mail.Address[]{
                                                    new InternetAddress("pwrliang@163.com")});
                                            transport.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return true;
                                    }
                                }.execute(contentEdt.getText().toString());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case R.id.nav_about: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("关于")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(R.string.about);
                builder.setPositiveButton("关闭",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_GET_CITY:
                if (resultCode == REP_SET_CITY) {
                    cityId = data.getStringExtra("city_id");
                    WeatherApplication.configuration.setCityId(cityId);
                    new GetDataTask().execute(cityId);
                }
                break;
        }
    }

    private void initViewData() {
        llAqi.setVisibility(View.INVISIBLE);
        sv.smoothScrollTo(0, 0);
        tvCity.setText(R.string.default_text);
        tvRelease.setText(R.string.default_text);
        ivNowWeather.setImageResource(R.drawable.d999);
        tvNowWeather.setText(R.string.default_text);
        tvTodayTemp.setText(R.string.default_text);
        tvNowTemp.setText(R.string.default_text);
        tvAqi.setText(R.string.default_text);
        tvQuality.setText(R.string.default_text);
        tvNextFifteen.setText(R.string.default_text);
        ivNextFifteen.setImageResource(R.drawable.d999);
        tvNextFifteenPop.setText("");
        tvNextFifteenTemp.setText(R.string.default_text);
        tvNextTwelve.setText(R.string.default_text);
        ivNextTwelve.setImageResource(R.drawable.d999);
        tvNextTwelvePop.setText("");
        tvNextTwelveTemp.setText(R.string.default_text);
        tvNextNine.setText(R.string.default_text);
        ivNextNine.setImageResource(R.drawable.d999);
        tvNextNinePop.setText("");
        tvNextNineTemp.setText(R.string.default_text);
        tvNextSix.setText(R.string.default_text);
        ivNextSix.setImageResource(R.drawable.d999);
        tvNextSixPop.setText("");
        tvNextSixTemp.setText(R.string.default_text);
        tvNextThree.setText(R.string.default_text);
        ivNextThree.setImageResource(R.drawable.d999);
        tvNextThreePop.setText("");
        tvNextThreeTemp.setText(R.string.default_text);
        tvTodayTempMax.setText(R.string.default_text);
        tvTodayTempMin.setText(R.string.default_text);
        ivTodayWeather.setImageResource(R.drawable.d999);
        tvTomorrow.setText(R.string.default_text);
        tvTomorrowTempMax.setText(R.string.default_text);
        tvTomorrowTempMin.setText(R.string.default_text);
        ivTomorrowWeather.setImageResource(R.drawable.d999);
        tvThirdDay.setText(R.string.default_text);
        tvThirdDayTempMax.setText(R.string.default_text);
        tvThirdDayTempMin.setText(R.string.default_text);
        ivThirdWeather.setImageResource(R.drawable.d999);
        tvFourthDay.setText(R.string.default_text);
        tvFourthDayTempMax.setText(R.string.default_text);
        tvFourthDayTempMin.setText(R.string.default_text);
        ivFourthWeather.setImageResource(R.drawable.d999);
        tvFeltAirTemp.setText(R.string.default_text);
        tvHumidity.setText(R.string.default_text);
        tvWind.setText(R.string.default_text);
        tvUvIndex.setText(R.string.default_text);
        tvDressingIndex.setText(R.string.default_text);
    }

    private void setTodayWeather(TodayWeather todayWeather)
            throws ParseException, IOException {
        initViewData();// 控件数据恢复默认
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        tvCity.setText(todayWeather.getCityName());
        cal.setTime(sdf.parse(todayWeather.getReleaseTime()));
        tvRelease.setText(WeatherUtil.getTimeByCalendar(cal) + " 发布");
        ivNowWeather.setImageBitmap(FileUtil.loadBitmap(WeatherActivity.this,
                todayWeather.getWeatherCode() + ".png"));
        tvNowWeather.setText(todayWeather.getWeatherTxt());
        tvTodayTemp.setText("↑"
                + WeatherUtil.getTempCToF(todayWeather.dailyForecastWeather.get(0).getMaxTemp(), configuration.getFahrenheit())
                + "\t\t↓"
                + WeatherUtil.getTempCToF(todayWeather.dailyForecastWeather.get(0).getMinTemp(), configuration.getFahrenheit()));// 今日最高最低气温
        tvNowTemp.setText(WeatherUtil.getTempCToF(todayWeather.getTemp(), configuration.getFahrenheit()));
        // 县/区没有AQI
        if (todayWeather.getAqi() != null && todayWeather.getQlty() != null) {
            llAqi.setVisibility(View.VISIBLE);
            tvAqi.setText(String.valueOf(todayWeather.getAqi()));
            tvQuality.setText(String.valueOf(todayWeather.getQlty()));
        }
        //填充小时预测
        switch (todayWeather.hourlyForecastWeather.size()) {
            case 5:
                HourlyForecastWeather hfw5 = todayWeather.hourlyForecastWeather
                        .get(4);
                cal.setTime(sdf.parse(hfw5.getDate()));
                tvNextFifteen.setText(WeatherUtil.getTimeByCalendar(cal));//预报时间
                ivNextFifteen.setImageResource(R.drawable.d305);//降水概率图标
                tvNextFifteenPop.setText(hfw5.getPop() + "%");//降水概率
                tvNextFifteenTemp.setText(WeatherUtil.getTempCToF(hfw5.getTemp(), configuration.getFahrenheit()));//预报温度
            case 4:
                HourlyForecastWeather hfw4 = todayWeather.hourlyForecastWeather
                        .get(3);
                cal.setTime(sdf.parse(hfw4.getDate()));
                tvNextTwelve.setText(WeatherUtil.getTimeByCalendar(cal));
                ivNextTwelve.setImageResource(R.drawable.d305);
                tvNextTwelvePop.setText(hfw4.getPop() + "%");
                tvNextTwelveTemp.setText(WeatherUtil.getTempCToF(hfw4.getTemp(), configuration.getFahrenheit()));
            case 3:
                HourlyForecastWeather hfw3 = todayWeather.hourlyForecastWeather
                        .get(2);
                cal.setTime(sdf.parse(hfw3.getDate()));
                tvNextNine.setText(WeatherUtil.getTimeByCalendar(cal));
                ivNextNine.setImageResource(R.drawable.d305);
                tvNextNinePop.setText(hfw3.getPop() + "%");
                tvNextNineTemp.setText(WeatherUtil.getTempCToF(hfw3.getTemp(), configuration.getFahrenheit()));
            case 2:
                HourlyForecastWeather hfw2 = todayWeather.hourlyForecastWeather
                        .get(1);
                cal.setTime(sdf.parse(hfw2.getDate()));
                tvNextSix.setText(WeatherUtil.getTimeByCalendar(cal));
                ivNextSix.setImageResource(R.drawable.d305);
                tvNextSixPop.setText(hfw2.getPop() + "%");
                tvNextSixTemp.setText(WeatherUtil.getTempCToF(hfw2.getTemp(), configuration.getFahrenheit()));
            case 1:
                HourlyForecastWeather hfw1 = todayWeather.hourlyForecastWeather
                        .get(0);
                cal.setTime(sdf.parse(hfw1.getDate()));
                tvNextThree.setText(WeatherUtil.getTimeByCalendar(cal));
                ivNextThree.setImageResource(R.drawable.d305);
                tvNextThreePop.setText(hfw1.getPop() + "%");
                tvNextThreeTemp.setText(WeatherUtil.getTempCToF(hfw1.getTemp(), configuration.getFahrenheit()));
        }
        // 填充每日预测
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        DailyForecastWeather dfw = todayWeather.dailyForecastWeather.get(0);
        tvTodayTempMax.setText(WeatherUtil.getTempCToF(dfw.getMaxTemp(), configuration.getFahrenheit()));
        tvTodayTempMin.setText(WeatherUtil.getTempCToF(dfw.getMinTemp(), configuration.getFahrenheit()));
        ivTodayWeather.setImageBitmap(FileUtil.loadBitmap(
                getApplicationContext(), dfw.getWeatherCode() + ".png"));

        dfw = todayWeather.dailyForecastWeather.get(1);
        cal.setTime(sdf.parse(dfw.getDate()));
        tvTomorrow.setText(cal.get(Calendar.MONTH) + 1 + "月"
                + cal.get(Calendar.DAY_OF_MONTH) + "日");
        tvTomorrowTempMax.setText(WeatherUtil.getTempCToF(dfw.getMaxTemp(), configuration.getFahrenheit()));
        tvTomorrowTempMin.setText(WeatherUtil.getTempCToF(dfw.getMinTemp(), configuration.getFahrenheit()));
        ivTomorrowWeather.setImageBitmap(FileUtil.loadBitmap(
                getApplicationContext(), dfw.getWeatherCode() + ".png"));

        dfw = todayWeather.dailyForecastWeather.get(2);
        cal.setTime(sdf.parse(dfw.getDate()));
        tvThirdDay.setText(cal.get(Calendar.MONTH) + 1 + "月"
                + cal.get(Calendar.DAY_OF_MONTH) + "日");
        tvThirdDayTempMax.setText(WeatherUtil.getTempCToF(dfw.getMaxTemp(), configuration.getFahrenheit()));
        tvThirdDayTempMin.setText(WeatherUtil.getTempCToF(dfw.getMinTemp(), configuration.getFahrenheit()));
        ivThirdWeather.setImageBitmap(FileUtil.loadBitmap(
                getApplicationContext(), dfw.getWeatherCode() + ".png"));

        dfw = todayWeather.dailyForecastWeather.get(3);
        cal.setTime(sdf.parse(dfw.getDate()));
        tvFourthDay.setText(cal.get(Calendar.MONTH) + 1 + "月"
                + cal.get(Calendar.DAY_OF_MONTH) + "日");
        tvFourthDayTempMax.setText(WeatherUtil.getTempCToF(dfw.getMaxTemp(), configuration.getFahrenheit()));
        tvFourthDayTempMin.setText(WeatherUtil.getTempCToF(dfw.getMinTemp(), configuration.getFahrenheit()));
        ivFourthWeather.setImageBitmap(FileUtil.loadBitmap(
                getApplicationContext(), dfw.getWeatherCode() + ".png"));
        // 填充详细信息
        tvFeltAirTemp.setText(WeatherUtil.getTempCToF(todayWeather.getFeltAirTemp(), configuration.getFahrenheit()));
        tvHumidity.setText(String.valueOf(todayWeather.getHumidity()) + "%");
        tvWind.setText(todayWeather.getWindDir() + " "
                + todayWeather.getWindDegree() + "级");
        tvUvIndex.setText(todayWeather.getUvTxt());
        tvDressingIndex.setText(todayWeather.getDrsgTxt());
    }

    private class GetDataTask extends AsyncTask<String, Integer, Boolean> {
        TodayWeather todayWeather = null;
        long refreshingStart;

        @Override
        protected void onPreExecute() {
            mPullToRefreshView.setRefreshing(true);
            initViewData();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                refreshingStart = System.currentTimeMillis();
                String jsonResult = null;
                String fileName = cityId + ".json";
                //纯手动刷新，每次都从互联网获取
                //或者是，自动刷新，但缓存不存在，从互联网获取
                if (configuration.getAutoUpdate() == Configuration.AUTO_UPDATE_NONE) {
                    LogUtil.i(TAG, "手动刷新，从互联网获取");
                    jsonResult = NetworkUtil.requestS(WeatherApplication.httpsUrl + params[0]);
                    FileUtil.saveText(WeatherActivity.this, fileName, jsonResult);
                } else if (!FileUtil.isExists(WeatherActivity.this, fileName)) {
                    LogUtil.i(TAG, "自动刷新，但缓存文件不存在，从互联网获取");
                    jsonResult = NetworkUtil.requestS(WeatherApplication.httpsUrl + params[0]);
                    FileUtil.saveText(WeatherActivity.this, fileName, jsonResult);
                } else {
                    LogUtil.i(TAG, "自动刷新，从缓存获取");
                    jsonResult = FileUtil.loadText(WeatherActivity.this, fileName);
                }
                LogUtil.i(TAG, "URL:" + WeatherApplication.httpsUrl + params[0]);
                LogUtil.i(TAG, "JSON:" + jsonResult);
                todayWeather = WeatherUtil.getTodayWeather(jsonResult);
                WeatherUtil.cacheWeatherIcon(WeatherActivity.this,
                        todayWeather.getWeatherCode());
                List<DailyForecastWeather> dfws = todayWeather.dailyForecastWeather;
                for (DailyForecastWeather dfw : dfws) {
                    WeatherUtil.cacheWeatherIcon(WeatherActivity.this,
                            dfw.getWeatherCode());
                }
                //保证动画显示1秒
                long interval = System.currentTimeMillis() - refreshingStart;
                if (interval < 1000) {
                    Thread.sleep(1000 - interval);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {
                if (todayWeather != null)
                    setTodayWeather(todayWeather);
                else {
                    Snackbar.make(mPullToRefreshView, "刷新失败，请检查网络！", Snackbar.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPullToRefreshView.setRefreshing(false);
        }
    }

}

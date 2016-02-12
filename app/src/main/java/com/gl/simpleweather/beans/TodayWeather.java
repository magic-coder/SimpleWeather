package com.gl.simpleweather.beans;

import java.util.List;

public class TodayWeather {
    private String cityName;// 城市名称
    private String cityId;// 城市代码
    private String releaseTime;// 天气发布时间
    private String weatherCode;// 天气代码，用于找图标
    private String weatherTxt;// 天气描述

    private Integer feltAirTemp;// 体感温度
    private Integer temp;// 当前温度

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    private Integer aqi;// 空气质量

    public String getQlty() {
        return qlty;
    }

    public void setQlty(String qlty) {
        this.qlty = qlty;
    }

    private String qlty;// 空气质量描述

    public Integer getTemp() {
        return temp;
    }

    public void setTemp(Integer temp) {
        this.temp = temp;
    }

    private Integer humidity;// 湿度
    private String windDir;// 风向
    private String windDegree;// 风力
    // ---suggestion中
    private String uvBrf;// 紫外线等级
    private String uvTxt;// 紫外线详情
    private String drsgBrf;// 穿衣指数
    private String drsgTxt;// 穿衣详情
    public List<HourlyForecastWeather> hourlyForecastWeather;// 每小时预测
    public List<DailyForecastWeather> dailyForecastWeather;// 每日预测

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getWeatherTxt() {
        return weatherTxt;
    }

    public void setWeatherTxt(String weatherTxt) {
        this.weatherTxt = weatherTxt;
    }

    public Integer getFeltAirTemp() {
        return feltAirTemp;
    }

    public void setFeltAirTemp(Integer feltAirTemp) {
        this.feltAirTemp = feltAirTemp;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(String windDegree) {
        this.windDegree = windDegree;
    }

    public String getUvBrf() {
        return uvBrf;
    }

    public void setUvBrf(String uvBrf) {
        this.uvBrf = uvBrf;
    }

    public String getUvTxt() {
        return uvTxt;
    }

    public void setUvTxt(String uvTxt) {
        this.uvTxt = uvTxt;
    }

    public String getDrsgBrf() {
        return drsgBrf;
    }

    public void setDrsgBrf(String drsgBrf) {
        this.drsgBrf = drsgBrf;
    }

    public String getDrsgTxt() {
        return drsgTxt;
    }

    public void setDrsgTxt(String drsgTxt) {
        this.drsgTxt = drsgTxt;
    }

}

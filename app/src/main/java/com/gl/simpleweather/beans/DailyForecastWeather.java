package com.gl.simpleweather.beans;

public class DailyForecastWeather {
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public Integer getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(Integer maxTemp) {
		this.maxTemp = maxTemp;
	}

	public Integer getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(Integer minTemp) {
		this.minTemp = minTemp;
	}

	private String date;// 日期
	private String weatherCode;// 天气代码
	private String weatherTxt;// 天气描述
	private Integer maxTemp;// 最高气温
	private Integer minTemp;// 最低气温
	
}

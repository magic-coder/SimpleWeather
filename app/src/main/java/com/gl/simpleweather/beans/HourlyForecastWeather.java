package com.gl.simpleweather.beans;

public class HourlyForecastWeather {
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getTemp() {
		return temp;
	}

	public void setTemp(Integer temp) {
		this.temp = temp;
	}

	public Integer getPop() {
		return pop;
	}

	public void setPop(Integer pop) {
		this.pop = pop;
	}

	private String date;// 日期
	private Integer temp;// 温度
	private Integer pop;// 降水几率
}

package com.gl.simpleweather.test;

import com.gl.simpleweather.beans.TodayWeather;
import com.gl.simpleweather.utils.WeatherUtil;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
	public static String load(String fileName) {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			FileInputStream inputStream = new FileInputStream(
					new File(fileName));
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * @param args
	 * @throws JSONException
	 */
	public static void main(String[] args) throws JSONException {
		TodayWeather t = WeatherUtil.getTodayWeather(load("D:\\output.json"));
		System.out.println(t.getAqi());
		System.out.println(t.getQlty());
		System.out.println(t.getCityId());
		System.out.println(t.getCityName());
		System.out.println(t.getDrsgBrf());
		System.out.println(t.getDrsgTxt());
		System.out.println(t.getReleaseTime());
		System.out.println(t.getUvBrf());
		System.out.println(t.getUvTxt());
		System.out.println(t.getWeatherCode());
		System.out.println(t.getWeatherTxt());
		System.out.println(t.getWindDegree());
		System.out.println(t.getWindDir());
		System.out.println(t.getFeltAirTemp());
		System.out.println(t.getHumidity());
		System.out.println(t.dailyForecastWeather.get(0).getDate());
		System.out.println(t.dailyForecastWeather.get(0).getWeatherCode());
		System.out.println(t.dailyForecastWeather.get(0).getWeatherTxt());
		System.out.println(t.dailyForecastWeather.get(0).getMaxTemp());
		System.out.println(t.dailyForecastWeather.get(0).getMinTemp());
		System.out.println(t.hourlyForecastWeather.get(0).getDate());
		System.out.println(t.hourlyForecastWeather.get(0).getPop());
		System.out.println(t.hourlyForecastWeather.get(0).getTemp());
		
		
	}

}

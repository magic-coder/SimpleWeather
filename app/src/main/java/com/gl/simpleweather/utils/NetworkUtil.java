package com.gl.simpleweather.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtil {
    public static String request(String httpUrl) throws IOException {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
//        connection.setConnectTimeout(2000);
//        connection.setReadTimeout(2000);
        connection.connect();
        InputStream is = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String strRead = null;
        while ((strRead = reader.readLine()) != null) {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        result = sbf.toString();
        return result;
    }

    public static String requestS(String httpUrl) throws IOException {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        URL url = new URL(httpUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url
                .openConnection();
        connection.setRequestMethod("GET");
//        connection.setConnectTimeout(2000);
//        connection.setReadTimeout(2000);
        connection.connect();
        InputStream is = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String strRead = null;
        while ((strRead = reader.readLine()) != null) {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        result = sbf.toString();
        return result;
    }
}

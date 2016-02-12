package com.gl.simpleweather.utils;

import com.gl.simpleweather.beans.County;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AreaUtil {
    public static synchronized Set<String> getProvinces(String tableHTML) {
        Document document = Jsoup.parse(tableHTML);
        Elements table = document.getElementsByTag("table");

        Set<String> hashSet = new HashSet<String>();// 过滤重复行

        Elements rows = table.get(0).getElementsByTag("tr");
        for (Element row : rows) {// 遍历行
            Elements columns = row.getElementsByTag("td");
            // 0 ------1---------- 2------ 3
            // ID --市/县--------地区---省/直辖市
            if (columns.size() == 4) {
                String provinceName = columns.get(3).text();
                hashSet.add(provinceName);
            }
        }
        if (hashSet.size() == 0) {
            return null;
        }
        return hashSet;
    }

    public static synchronized Set<String> getCities(String tableHTML, String provinceName) {
        Document document = Jsoup.parse(tableHTML);
        Elements table = document.getElementsByTag("table");
        Set<String> hashSet = new HashSet<String>();// 过滤重复行

        Elements rows = table.get(0).getElementsByTag("tr");
        for (Element row : rows) {// 遍历行
            Elements columns = row.getElementsByTag("td");
            // 0 ------1---------- 2------ 3
            // ID --市/县--------地区---省/直辖市
            if (columns.size() == 4) {
                if (provinceName.equals(columns.get(3).text().trim())) {
                    hashSet.add(columns.get(2).text());
                }
            }
        }
        if (hashSet.size() == 0) {
            return null;
        }
        return hashSet;
    }

    public static synchronized List<County> getCounties(String tableHTML, String provinceName, String cityName) {
        Document document = Jsoup.parse(tableHTML);
        Elements table = document.getElementsByTag("table");
        List<County> counties = new ArrayList<County>();// 过滤重复行

        Elements rows = table.get(0).getElementsByTag("tr");
        for (Element row : rows) {// 遍历行
            Elements columns = row.getElementsByTag("td");
            // 0 ------1---------- 2------ 3
            // ID --市/县--------地区---省/直辖市
            if (columns.size() == 4) {
                if (provinceName.equals(columns.get(3).text().trim()) && cityName.equals(columns.get(2).text().trim())) {
                    County county = new County();
                    county.setCountyId(columns.get(0).text().trim());
                    county.setCountyName(columns.get(1).text().trim());
                    counties.add(county);
                }
            }
        }
        if (counties.size() == 0) {
            return null;
        }
        return counties;
    }

    public static synchronized String getCountyId(String tableHTML, String provinceName, String cityName) {
        Document document = Jsoup.parse(tableHTML);
        Elements table = document.getElementsByTag("table");

        Elements rows = table.get(0).getElementsByTag("tr");
        for (Element row : rows) {// 遍历行
            Elements columns = row.getElementsByTag("td");
            // 0 ------1---------- 2------ 3
            // ID --市/县--------地区---省/直辖市
            if (columns.size() == 4) {
                if (provinceName.equals(columns.get(3).text().trim()) && cityName.equals(columns.get(2).text().trim())) {
                    return columns.get(0).text().trim();
                }
            }
        }
        return null;
    }
}

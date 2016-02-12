package com.gl.simpleweather.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {
    public static void saveText(Context context, String fileName, String content)
            throws IOException {
        BufferedWriter writer = null;
        FileOutputStream outputStream = context.openFileOutput(fileName,
                Context.MODE_PRIVATE);
        writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(content);
        writer.close();
        outputStream.close();
    }

    public static boolean isExists(Context context, String fileName) {
        String path = context.getFilesDir().getPath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public static String loadText(Context context, String fileName)
            throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        FileInputStream inputStream = context.openFileInput(fileName);
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        if (reader != null) {
            reader.close();
        }
        return buffer.toString();
    }

    public static void saveBitmap(Context context, String fileName,
                                  Bitmap bitmap) throws IOException {
        FileOutputStream fileOutputStream = null;
        fileOutputStream = context.openFileOutput(fileName,
                Context.MODE_PRIVATE);
        bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static Bitmap loadBitmap(Context context, String fileName)
            throws IOException {
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        fileInputStream = context.openFileInput(fileName);
        bitmap = BitmapFactory.decodeStream(fileInputStream);
        fileInputStream.close();
        return bitmap;
    }

    public static Bitmap getBitMap(String url) throws IOException {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        myFileUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
        conn.setDoInput(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }
}

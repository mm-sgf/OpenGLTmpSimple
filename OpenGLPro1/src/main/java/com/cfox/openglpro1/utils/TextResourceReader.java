package com.cfox.openglpro1.utils;

import android.content.Context;

import com.cfox.openglpro1.log.GLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextResourceReader {

    public static String readTextFileFromResource(Context context , int resourceId) {
        StringBuilder body = new StringBuilder();

        InputStream inputStream;
        InputStreamReader inputStreamReader;

        inputStream = context.getResources().openRawResource(resourceId);
        inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }

        } catch (IOException e) {
            GLog.e("io e==>" + e);
        } finally {
            try {
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return body.toString();

    }
}

package com.mobilenow.cyrcadia_itbra.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsFileUtil {
    public static String readTextFile(Context context, String filePath) {
        try {
            InputStream is = context.getAssets().open(filePath);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = inputStreamReader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

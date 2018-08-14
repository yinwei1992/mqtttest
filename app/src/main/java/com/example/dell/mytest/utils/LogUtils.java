package com.example.dell.mytest.utils;

import android.text.TextUtils;
import android.util.Log;


/**
 * Created by sylar on 15/6/23.
 */
public class LogUtils {
    private static boolean debug = true;


    public static void i(String key, String value) {
        if (!debug) return;
        if (!TextUtils.isEmpty(value)) {
            Log.i(key, value);
        }
    }

    public static void out(String value) {
        if (!debug) return;
        Log.i("roki_rent", value);
    }

    public static void var(String value) {
        if (!debug) return;
        Log.i("var:", value);
    }
}

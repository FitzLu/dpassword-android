package com.dpass.android.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "Dpass-Log";
    private static Boolean OPEN_LOG = true;

    public static void i(String message){
        i(TAG, message);
    }

    public static void d(String message){
        d(TAG, message);
    }

    public static void w(String message){
        w(TAG, message);
    }

    public static void v(String message){
        v(TAG, message);
    }

    public static void e(String message){
        e(TAG, message);
    }

    public static void i(String tag, String message){
        if (OPEN_LOG) {
            Log.i(TAG + ": " + tag, message);
        }
    }

    public static void d(String tag, String message){
        if (OPEN_LOG) {
            Log.d(TAG + ": " + tag, message);
        }
    }

    public static void w(String tag, String message){
        if (OPEN_LOG) {
            Log.w(TAG + ": " + tag, message);
        }
    }

    public static void v(String tag, String message){
        if (OPEN_LOG) {
            Log.v(TAG + ": " + tag, message);
        }
    }

    public static void e(String tag, String message){
        if (OPEN_LOG) {
            Log.e(TAG + ": " + tag, message);
        }
    }

}

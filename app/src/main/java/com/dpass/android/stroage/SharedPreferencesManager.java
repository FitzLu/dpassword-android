package com.dpass.android.stroage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.dpass.android.utils.Logger;

public class SharedPreferencesManager {

    private static final String TAG = "DpassShaF";

    private static volatile boolean sIsInitialized = false;

    private static SharedPreferencesManager mInstance;

    private SharedPreferences        mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    private SharedPreferencesManager(Context context){
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static SharedPreferencesManager get(){
        return mInstance;
    }

    public static void initialize(Context context){
        if (sIsInitialized){
            Logger.e(TAG, "SharedPreferencesManager has already been initialized! `SharedPreferencesManager.initialize(...)` should only be called " +
                    "1 single time to avoid memory leaks!");
        }else{
            sIsInitialized = true;
        }
        mInstance = new SharedPreferencesManager(context);
    }

    private void putString(String key, String value){
        mEditor.putString(key, value).apply();
    }

    private String getString(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    private void putBoolean(String key, boolean value){
        mEditor.putBoolean(key, value).apply();
    }

    private boolean getBoolean(String key, boolean defaultValue){
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    //templates
    private static final String showTipPopWindowKey = "showTipPopWindow";
    private static final String showTipDialogKey    = "showTipDialog";

    public void setShowTipPopWindow(boolean value){
        putBoolean(showTipPopWindowKey, value);
    }

    public boolean getShowTipPopWindow(){
        return getBoolean(showTipPopWindowKey, true);
    }

    public void setShowTipDialog(boolean value){
        putBoolean(showTipDialogKey, value);
    }

    public boolean getShowTipDialog(){
        return getBoolean(showTipDialogKey, true);
    }

}

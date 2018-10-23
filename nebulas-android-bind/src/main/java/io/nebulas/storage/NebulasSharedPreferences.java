package io.nebulas.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class NebulasSharedPreferences {

    private static final String TAG = "NebulasSharedPrefs";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public NebulasSharedPreferences(Context context){
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void putString(String key, String value){
        mEditor.putString(key, value).apply();
    }

    public String getString(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value){
        mEditor.putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue){
        return mSharedPreferences.getInt(key, defaultValue);
    }

}

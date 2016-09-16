package com.harryio.orainteractive;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    public static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";
    public static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    public static final String KEY_USER_ID = "USER_ID";

    private static final String PREF_NAME = "OraChatPrefs";
    private static PrefUtils prefUtils;
    private SharedPreferences sharedPreferences;

    private PrefUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PrefUtils getInstance(Context context) {
        if (prefUtils == null) {
            prefUtils = new PrefUtils(context);
        }

        return prefUtils;
    }

    public void put(String key, boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    public void put(String key, String val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public void put(String key, int val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public boolean get(String key, boolean defVal) {
        return sharedPreferences.getBoolean(key, defVal);
    }

    public String get(String key, String defVal) {
        return sharedPreferences.getString(key, defVal);
    }

    public int get(String key, int defVal) {
        return sharedPreferences.getInt(key, defVal);
    }
}

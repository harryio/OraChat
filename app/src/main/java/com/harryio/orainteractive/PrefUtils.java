package com.harryio.orainteractive;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    public static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";
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

    public boolean get(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}

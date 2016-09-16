package com.harryio.orainteractive;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class to work with {@link SharedPreferences}
 */
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

    /**
     * Get single instance of PrefUtils
     *
     * @param context Context
     */
    public static PrefUtils getInstance(Context context) {
        if (prefUtils == null) {
            prefUtils = new PrefUtils(context);
        }

        return prefUtils;
    }

    /**
     * Save boolean in {@link SharedPreferences}
     * @param key key for the value
     * @param bool boolean value to be saved
     */
    public void put(String key, boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    /**
     * Save String in {@link SharedPreferences}
     * @param key key for the value
     * @param val String value to be saved
     */
    public void put(String key, String val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    /**
     * Save int in {@link SharedPreferences}
     * @param key key for the value
     * @param val int value to saved
     */
    public void put(String key, int val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    /**
     * Get boolean from {@link SharedPreferences}
     * @param key key for the value
     * @param defVal default boolean value to return if no value is found for the supplied key
     * @return boolean value corresponding to supplied key
     */
    public boolean get(String key, boolean defVal) {
        return sharedPreferences.getBoolean(key, defVal);
    }

    /**
     * Get String value from {@link SharedPreferences}
     * @param key key for the value
     * @param defVal default String to be returned if no value is found for the supplied key
     * @return String value corresponding to supplied key
     */
    public String get(String key, String defVal) {
        return sharedPreferences.getString(key, defVal);
    }

    /**
     * Get int value from {@link SharedPreferences}
     * @param key key for the value
     * @param defVal default int to be returned if no value is found for the supplied key
     * @return int value corresponding to supplied key
     */
    public int get(String key, int defVal) {
        return sharedPreferences.getInt(key, defVal);
    }
}

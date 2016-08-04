package com.frost.cloudalbums.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    private static final String USER_ID = "userId";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String IS_LOGGED_ID = "isUserLoggedIn";

    public static Boolean isUserLoggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("isUserLoggedIn", false);
    }

    public static void setUserLoggedIn(Context context, Boolean isLoggedIn) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_LOGGED_ID, isLoggedIn);
        editor.apply();
    }

    public static void saveAccessToken(Context context, String accessToken) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static String extractAccessToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(ACCESS_TOKEN, null);
    }

    public static void saveProfileId(Context context, int userId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(USER_ID, userId);
        editor.apply();
    }

    public static int extractProfileId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(USER_ID, 0);
    }
}

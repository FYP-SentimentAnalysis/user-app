package com.pritamdey.sentimentanalysis;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    private static final String PREF_NAME = "SentimentAnalysisPrefs";
    private static final String USER_EMAIL = "User Email";
    private static final String USER_PASSWORD = "User Password";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setUserEmail(Context context, String email){
        getPrefs(context).edit().putString(USER_EMAIL, email).apply();
    }

    public static String getUserEmail(Context context){
        return getPrefs(context).getString(USER_EMAIL, null);
    }

    public static void setUserPassword(Context context, String password){
        getPrefs(context).edit().putString(USER_PASSWORD, password).apply();
    }

    public static String getUserPassword(Context context){
        return getPrefs(context).getString(USER_PASSWORD, null);
    }
}

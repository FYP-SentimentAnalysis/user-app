package com.pritamdey.sentimentanalysis;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class App extends Application {
    public static String baseURL = "https://17ea-2405-201-8013-b157-ccf9-6ea-4fa9-ba66.ngrok-free.app/";
    public static User loggedInUser = null;

    public static void hideKeyBoard(Context context, View v){
        v.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}

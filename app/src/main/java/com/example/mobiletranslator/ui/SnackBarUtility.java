package com.example.mobiletranslator.ui;

import android.app.Activity;
import android.graphics.Color;

import com.example.mobiletranslator.AppException;
import com.example.mobiletranslator.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtility {
    public static int SUCCESS = 0;
    public static int ERROR = 1;
    public static int INFO = 2;

    public static void displayMessage(Activity activity, String message, int type){
        int bgColor, textColor;

        switch(type){
            case 0:
                bgColor = Color.GREEN;
                textColor = Color.BLACK;
                break;
            case 1:
                bgColor = Color.RED;
                textColor = Color.WHITE;
                break;
            case 2:
            default:
                bgColor = Color.BLUE;
                textColor = Color.WHITE;
        }

        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.mainCoordinatorLayout), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(bgColor);
        snackbar.setTextColor(textColor);
        snackbar.show();
    }

    public static void displayMessage(Activity activity, AppException e){
        displayMessage(activity, e.getMessage(), SnackBarUtility.ERROR);
    }

}

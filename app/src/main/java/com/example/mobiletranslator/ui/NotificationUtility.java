package com.example.mobiletranslator.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;

import com.example.mobiletranslator.AppException;
import com.example.mobiletranslator.R;
import com.google.android.material.snackbar.Snackbar;

public class NotificationUtility {
    public static int SUCCESS = 0;
    public static int ERROR = 1;

    public static void displayConfirmDialog(Activity activity, String message, DialogInterface.OnClickListener successEvent){
        Resources resources = activity.getApplicationContext().getResources();
        String title = resources.getString(R.string.dialog_title_generic);
        String confirmButtonText = resources.getString(R.string.dialog_action_confirm);
        String cancelButtonText = resources.getString(R.string.dialog_action_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(confirmButtonText, successEvent);
        builder.setNegativeButton(cancelButtonText, (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
        displayMessage(activity, e.getMessage(), NotificationUtility.ERROR);
    }

}

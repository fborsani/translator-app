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

    public enum snackBarStyle {
        SUCCESS,
        ERROR
    }

    public enum CauseCode {
        MISSING_API_KEY,
        IMAGE_PARSER_INIT_FAILED,
        IMAGE_CREATION_FAILED,
        TRANSLATION_FAILED,
        THREAD_INTERRUPTED,
        UNABLE_TO_WRITE,
        OTHER
    }

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

    public static void displayMessage(Activity activity, String message, snackBarStyle type){
        int bgColor, textColor;

        switch(type){
            case SUCCESS:
                bgColor = Color.GREEN;
                textColor = Color.BLACK;
                break;
            case ERROR:
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
        Resources resources = activity.getResources();
        String localizedMessage;

        switch (e.getCauseCode()){
            case MISSING_API_KEY:
                localizedMessage = resources.getString(R.string.error_missing_api_key);
                break;
            case IMAGE_PARSER_INIT_FAILED:
                localizedMessage =  resources.getString(R.string.error_image_parse_init_failed);
                break;
            case IMAGE_CREATION_FAILED:
                localizedMessage = resources.getString(R.string.error_image_creation);
                break;
            case TRANSLATION_FAILED:
                localizedMessage = resources.getString(R.string.error_translation);
                break;
            case THREAD_INTERRUPTED:
                localizedMessage = resources.getString(R.string.error_thread_interrupted);
                break;
            case UNABLE_TO_WRITE:
                localizedMessage = resources.getString(R.string.error_file_write);
                break;
            case OTHER:
            default:
                localizedMessage = resources.getString(R.string.error_generic);
        }
        displayMessage(activity, localizedMessage, snackBarStyle.ERROR);
    }

}

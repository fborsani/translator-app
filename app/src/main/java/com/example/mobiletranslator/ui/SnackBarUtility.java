package com.example.mobiletranslator.ui;

import com.example.mobiletranslator.AppException;

public class SnackBarUtility {
    public static void displayMessageInfo(String message){

    }

    public static void displayMessageSuccess(String message){

    }

    public static void displayMessageError(String message){

    }

    public static void displayMessageError(AppException e){
        displayMessageError(e.getMessage());
    }

}

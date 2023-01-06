package com.example.mobiletranslator;

import android.app.Activity;

public class AppException extends Throwable {
    public AppException(String msg){
        super(msg);
    }

    public AppException(Throwable cause, String msg){
        super(msg,cause);
    }

    public AppException(Throwable cause){
        super(cause);
    }
}

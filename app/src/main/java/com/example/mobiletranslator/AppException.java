package com.example.mobiletranslator;

import com.example.mobiletranslator.ui.NotificationUtility.CauseCode;

public class AppException extends Exception {
    private final CauseCode causeCode;

    public AppException(CauseCode causeCode) {
        super();
        this.causeCode = causeCode;
    }

    public AppException(Throwable cause, CauseCode causeCode){
        super(cause);
        this.causeCode = causeCode;
    }

    public CauseCode getCauseCode(){ return causeCode; }
}

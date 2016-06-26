package com.info.ghiny.examsystem.tools;

import android.graphics.drawable.Icon;

/**
 * Created by GhinY on 15/06/2016.
 */
public class CustomException extends Exception {

    public static final int MESSAGE_TOAST   = 0;
    public static final int UPDATE_PROMPT   = 1;
    public static final int MESSAGE_DIALOG  = 2;

    private int errorType;
    private String errorMsg;
    private int errorIconType;

    public CustomException(int errorType){
        this.errorType      = errorType;
        this.errorMsg       = null;
        this.errorIconType  = IconManager.WARNING;
    }

    public CustomException(String message, int errorType, int errorIconType){
        super(message);
        this.errorType      = errorType;
        this.errorMsg       = message;
        this.errorIconType  = errorIconType;
    }

    public int getErrorType(){
        return errorType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getErrorIcon(){
        return new IconManager().getIcon(errorIconType);
    }
}

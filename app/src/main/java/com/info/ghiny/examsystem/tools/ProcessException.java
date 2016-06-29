package com.info.ghiny.examsystem.tools;

import android.graphics.drawable.Icon;

/**
 * Created by GhinY on 15/06/2016.
 */
public class ProcessException extends Exception {

    public static final int MESSAGE_TOAST   = 0;
    public static final int UPDATE_PROMPT   = 1;
    public static final int MESSAGE_DIALOG  = 2;
    public static final int FATAL_MESSAGE   = 3;

    private int errorType;
    private String errorMsg;
    private int errorIconType;
    private ExceptionAction errorAction;

    public ProcessException(int errorType){
        this.errorType      = errorType;
        this.errorMsg       = null;
        this.errorIconType  = IconManager.WARNING;
        this.errorAction    = new ExceptionAction();
    }

    public ProcessException(String errMsg, int errType, int errIconType){
        super(errMsg);
        this.errorType      = errType;
        this.errorMsg       = errMsg;
        this.errorIconType  = errIconType;
    }
/*
    public ProcessException(String errMsg, int errType, int errIconType, ExceptionAction errAction){
        super(errMsg);
        this.errorType      = errType;
        this.errorMsg       = errMsg;
        this.errorIconType  = errIconType;
        this.errorAction    = errAction;
    }
*/
    public int getErrorType(){
        return errorType;
    }

    public String getErrorMsg() {
        if(errorMsg == null){
            errorMsg = "";
        }
        return errorMsg;
    }

    public int getErrorIcon(){
        return new IconManager().getIcon(errorIconType);
    }

    public void onPositive(){}

    public void onNegative(){}

    public void onNeutral(){}
}

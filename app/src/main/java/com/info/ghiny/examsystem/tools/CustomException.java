package com.info.ghiny.examsystem.tools;

/**
 * Created by GhinY on 15/06/2016.
 */
public class CustomException extends Exception {

    public static final int ERR_NULL_IDENTITY       = 0;
    public static final int ERR_ILLEGAL_IDENTITY    = 1;
    public static final int ERR_EMPTY_PASSWORD      = 2;
    public static final int ERR_WRONG_PASSWORD      = 3;

    private int errorCode;
    private String errorMsg;

    public CustomException(int errorCode){
        this.errorCode  = errorCode;
        this.errorMsg   = null;
    }

    public CustomException(String message, int errorCode){
        super(message);
        this.errorCode  = errorCode;
        this.errorMsg   = message;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}

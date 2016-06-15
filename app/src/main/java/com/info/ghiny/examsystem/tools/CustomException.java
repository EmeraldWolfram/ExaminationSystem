package com.info.ghiny.examsystem.tools;

/**
 * Created by GhinY on 15/06/2016.
 */
public class CustomException extends Exception {

    public static final int ERR_NOT_IDENTITY = 0;
    private int errorCode;
    private String errorMsg;

    public CustomException(){}

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

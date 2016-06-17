package com.info.ghiny.examsystem.tools;

import android.graphics.drawable.Icon;

/**
 * Created by GhinY on 15/06/2016.
 */
public class CustomException extends Exception {

    public static final int ERR_NULL_IDENTITY       = 0;
    public static final int ERR_ILLEGAL_IDENTITY    = 1;
    public static final int ERR_EMPTY_PASSWORD      = 2;
    public static final int ERR_WRONG_PASSWORD      = 3;
    public static final int ERR_NULL_CANDIDATE      = 4;
    public static final int ERR_STATUS_EXEMPTED     = 5;
    public static final int ERR_STATUS_BARRED       = 6;
    public static final int ERR_INCOMPLETE_ID       = 7;
    public static final int ERR_TABLE_REASSIGN      = 8;
    public static final int ERR_CANDIDATE_REASSIGN  = 9;
    public static final int ERR_NULL_TABLE          = 10;
    public static final int ERR_PAPER_NOT_MATCH     = 11;
    public static final int ERR_EMPTY_PAPER_LIST    = 12;
    public static final int ERR_NULL_PAPER          = 13;
    public static final int ERR_EMPTY_ATTD_LIST     = 14;

    private int errorCode;
    private String errorMsg;
    private int errorIconType;

    public CustomException(int errorCode){
        this.errorCode      = errorCode;
        this.errorMsg       = null;
        this.errorIconType  = IconManager.WARNING;
    }

    public CustomException(String message, int errorCode, int errorIconType){
        super(message);
        this.errorCode      = errorCode;
        this.errorMsg       = message;
        this.errorIconType  = errorIconType;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getErrorIcon(){
        return new IconManager().getIcon(errorIconType);
    }
}

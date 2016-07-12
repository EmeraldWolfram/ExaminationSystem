package com.info.ghiny.examsystem.tools;

import android.content.DialogInterface;
import android.graphics.drawable.Icon;

import java.util.HashMap;

/**
 * Created by GhinY on 15/06/2016.
 */
public class ProcessException extends Exception {

    public static final int MESSAGE_TOAST   = 0;
    public static final int UPDATE_PROMPT   = 1;
    public static final int MESSAGE_DIALOG  = 2;
    public static final int FATAL_MESSAGE   = 3;
    public static final int RESEND_CANCEL   = 4;

    public static final String updateButton = "UPDATE";
    public static final String cancelButton = "CANCEL";
    public static final String okayButton   = "OKAY";
    public static final String resendButton = "RESEND";

    private HashMap<String, DialogInterface.OnClickListener> buttonMap;
    private int errorType;
    private String errorMsg;
    private int errorIconType;

    public ProcessException(int errorType){
        this.errorType      = errorType;
        this.errorMsg       = null;
        this.errorIconType  = IconManager.WARNING;
    }

    public ProcessException(String errMsg, int errType, int errIconType){
        super(errMsg);
        this.errorType      = errType;
        this.errorMsg       = errMsg;
        this.errorIconType  = errIconType;
        this.buttonMap   = new HashMap<>();
    }

    public void setListener(String btnText, DialogInterface.OnClickListener listener){
        buttonMap.put(btnText, listener);
    }

    public DialogInterface.OnClickListener getListener(String btnText){
        DialogInterface.OnClickListener listener = buttonMap.get(btnText);

        if(listener == null){
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }

        return listener;
    }

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
}

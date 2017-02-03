package com.info.ghiny.examsystem.model;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.manager.IconManager;

import java.util.HashMap;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
public class ProcessException extends Exception {

    public static final int MESSAGE_TOAST   = 0;
    public static final int UPDATE_PROMPT   = 1;
    public static final int MESSAGE_DIALOG  = 2;
    public static final int FATAL_MESSAGE   = 3;
    public static final int YES_NO_MESSAGE  = 4;

    public static final String updateButton = "UPDATE";
    public static final String cancelButton = "CANCEL";
    public static final String okayButton   = "OKAY";
    public static final String yesButton    = "YES";
    public static final String noButton     = "NO";
    public static final String submitButton = "SUBMIT";

    private HashMap<String, DialogInterface.OnClickListener> buttonMap;
    private DialogInterface.OnCancelListener backPressListener;
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
        this.buttonMap      = new HashMap<>();
    }

    public void setListener(String btnText, DialogInterface.OnClickListener listener){
        buttonMap.put(btnText, listener);
    }

    public void setBackPressListener(DialogInterface.OnCancelListener backPressListener) {
        this.backPressListener = backPressListener;
    }

    public DialogInterface.OnClickListener getListener(String btnText){
        DialogInterface.OnClickListener listener = buttonMap.get(btnText);

        return listener;
    }

    public DialogInterface.OnCancelListener getBackPressListener() {
        if(backPressListener == null){
            return new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            };
        } else {
            return backPressListener;
        }
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

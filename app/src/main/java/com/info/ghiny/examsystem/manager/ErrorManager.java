package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.info.ghiny.examsystem.view_holder.CustomToast;
import com.info.ghiny.examsystem.model.ProcessException;

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
public class ErrorManager {

    private Activity act;

    public ErrorManager(Activity act) {
        this.act        = act;
    }

    public void displayError(ProcessException err){
        switch(err.getErrorType()){
            case ProcessException.UPDATE_PROMPT:
                this.showReassignDialog(err);
                break;
            case ProcessException.MESSAGE_DIALOG:
                this.showMessageDialog(err);
                break;
            case ProcessException.MESSAGE_TOAST:
                this.showToastMessage(err);
                break;
            case ProcessException.FATAL_MESSAGE:
                this.showFatalError(err);
                break;
            case ProcessException.YES_NO_MESSAGE:
                this.showYesNoDialog(err);
                break;
        }
    }

    private void showToastMessage(ProcessException err){
        CustomToast message = new CustomToast(act);
        message.showCustomMessage(err.getErrorMsg(), err.getErrorIcon());
    }

    //public void showToast(String msg){
    //    Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
    //}

    private void showReassignDialog(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle("REASSIGN OCCUR");
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(false);
        dialog.setPositiveButton(ProcessException.updateButton,
                getListener(err, ProcessException.updateButton));
        dialog.setNegativeButton(ProcessException.cancelButton,
                getListener(err, ProcessException.cancelButton));
        dialog.setIcon(err.getErrorIcon());

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void showMessageDialog(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle("MESSAGE");
        dialog.setIcon(err.getErrorIcon());
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);
        dialog.setOnCancelListener(err.getBackPressListener());
        dialog.setNeutralButton(ProcessException.okayButton,
                getListener(err, ProcessException.okayButton));

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void showFatalError(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle("FATAL ERROR");
        dialog.setIcon(err.getErrorIcon());
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);
        dialog.setOnCancelListener(err.getBackPressListener());

        dialog.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        act.finish();
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void showYesNoDialog(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle("MESSAGE");
        dialog.setIcon(err.getErrorIcon());
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);
        dialog.setOnCancelListener(err.getBackPressListener());

        dialog.setPositiveButton(ProcessException.yesButton,
                getListener(err, ProcessException.yesButton));
        dialog.setNegativeButton(ProcessException.noButton,
                getListener(err, ProcessException.noButton));

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private DialogInterface.OnClickListener getListener(ProcessException err, String button){
        DialogInterface.OnClickListener listener = err.getListener(button);
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
}

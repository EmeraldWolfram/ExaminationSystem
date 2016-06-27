package com.info.ghiny.examsystem.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by GhinY on 27/06/2016.
 */
public class ErrorManager {

    private Activity activity;

    public ErrorManager(Activity act){
        this.activity   = act;
    }

    public void displayError(ProcessException err){
        switch(err.getErrorType()){
            case ProcessException.UPDATE_PROMPT:
                showReassignDialog(err.getMessage());
                break;
            case ProcessException.MESSAGE_DIALOG:
                showMessageDialog(err.getMessage());
                break;
            case ProcessException.MESSAGE_TOAST:
                showToastMessage(err);
                break;
        }
    }

    public void showToastMessage(ProcessException err){
        CustomToast message = new CustomToast(activity);
        message.showCustomMessage(err.getErrorMsg(), err.getErrorIcon());
    }

    public void showReassignDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton(
                "UPDATE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Update the previous assigned candidate and table set
                        AssignHelper.updateNewCandidate();
                        dialog.cancel();
                    }
                });
        dialog.setNegativeButton(
                "REMAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Remain the previous assigned candidate and table set
                        AssignHelper.cancelNewAssign();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    public void showMessageDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(message);
        dialog.setCancelable(true);

        dialog.setNeutralButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();
    }


}

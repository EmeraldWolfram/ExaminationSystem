package com.info.ghiny.examsystem.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

/**
 * Created by GhinY on 27/06/2016.
 */
public class ErrorManager {

    private Activity act;

    public ErrorManager(Activity act){
        this.act = act;
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
            case ProcessException.FATAL_MESSAGE:
                showFatalError(err);
                break;
        }
    }

    public void showToastMessage(ProcessException err){
        CustomToast message = new CustomToast(act);
        message.showCustomMessage(err.getErrorMsg(), err.getErrorIcon());
    }

    public void showReassignDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
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

    public void showFatalError(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);

        dialog.setNeutralButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        act.finish();
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();

    }
}

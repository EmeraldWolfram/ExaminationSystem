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
                showReassignDialog(err);
                break;
            case ProcessException.MESSAGE_DIALOG:
                showMessageDialog(err);
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

    public void showReassignDialog(final ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);
        dialog.setPositiveButton(err.getButtons()[0], err.getListener(err.getButtons()[0]));
        dialog.setNegativeButton(err.getButtons()[1], err.getListener(err.getButtons()[1]));
        AlertDialog alert = dialog.create();
        alert.show();
    }

    public void showMessageDialog(final ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);
        dialog.setNeutralButton(err.getButtons()[0], err.getListener(err.getButtons()[0]));
        AlertDialog alert = dialog.create();
        alert.show();
    }

    public void showFatalError(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setMessage(err.getMessage());
        dialog.setCancelable(true);

        dialog.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        act.finish();
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();

    }
}

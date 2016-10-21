package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;

import com.info.ghiny.examsystem.model.CustomToast;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 27/06/2016.
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

        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void showMessageDialog(ProcessException err){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle("ERROR");
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

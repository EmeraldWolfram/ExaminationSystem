package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.interfacer.SetterView;
import com.info.ghiny.examsystem.model.AssignModel;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.LoginHelper;
import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public class AssignManager {
    private ScannerView scannerView;
    private SetterView setterView;
    private AssignModel assignModel;

    public AssignManager(ScannerView scannerView, SetterView setterView, CheckListLoader dBLoader){
        this.scannerView    = scannerView;
        this.setterView     = setterView;
        this.assignModel    = new AssignModel(this);
        try{
            this.assignModel.initLoader(dBLoader);
        } catch (ProcessException err) {
            scannerView.displayError(err);
        }
    }

    public void setAssignModel(AssignModel assignModel) {
        this.assignModel = assignModel;
    }

    public void onScanForTableOrCandidate(String scanStr){
        try{
            scannerView.pauseScanning();
            assignModel.tryAssignScanValue(scanStr);
            scannerView.resumeScanning();
        } catch (ProcessException err) {
            scannerView.displayError(err);
            scannerView.resumeScanning();
        }
    }

    public void onReceivePassword(int requestCode, int resultCode, Intent data){
        if(requestCode == PopUpLogin.PASSWORD_REQ_CODE && resultCode == Activity.RESULT_OK){
            String password = data.getStringExtra("Password");
            try{
                scannerView.pauseScanning();
                if(!LoginHelper.getStaff().matchPassword(password))
                    throw new ProcessException("Access denied. Incorrect Password",
                            ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);

                scannerView.resumeScanning();
            } catch(ProcessException err){
                scannerView.displayError(err);
                scannerView.securityPrompt();
            }
        }
    }

    public void onBackPressed(){
        ProcessException err    = new ProcessException("Confirm logout and exit?",
                ProcessException.YES_NO_MESSAGE, IconManager.MESSAGE);
        err.setListener(ProcessException.yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                scannerView.finishActivity();
            }
        });
        scannerView.displayError(err);
    }

    public void onPause(){
        scannerView.pauseScanning();
    }

    public void onResume(){
        scannerView.resumeScanning();
    }

    public void onRestart() {
        scannerView.securityPrompt();
    }

    public void setTable(Integer tableNum){
        if(tableNum > 0){
            setterView.setTableView(tableNum.toString());
        } else{
            setterView.setTableView("");
        }
    }

    public void setCandidate(Candidate cdd){
        try{
            ExamSubject paper = cdd.getPaper();
            setterView.setCandidateView(cdd.getExamIndex(), cdd.getRegNum(),
                    paper.toString());
        } catch (ProcessException err) {
            scannerView.displayError(err);
        }
    }

    public void clearTableAndCandidate(){
        setterView.setTableView("");
        setterView.setCandidateView("","","");
    }
}

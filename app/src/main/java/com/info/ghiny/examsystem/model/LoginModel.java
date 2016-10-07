package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.LoginMVP;

import java.util.HashMap;

/**
 * Created by GhinY on 15/06/2016.
 *
 * Login Helper is a tools to execute all back end logic for --- MainLoginActivity ---
 * without touching the UI interface. This allow automated test without using a hardware
 * mobile.
 */

public class LoginModel implements LoginMVP.Model{
    private static StaffIdentity staff;
    private int loginCount = 3;
    private LoginMVP.MPresenter taskPresenter;
    private String qrStaffID;
    private String inputPW;
    //This method take in an id and check if the id is an invigilator identity

    public LoginModel(LoginMVP.MPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
    }

    //= Setter & Getter ============================================================================
    public static StaffIdentity getStaff() {
        return staff;
    }
    public static void setStaff(StaffIdentity staff) {
        LoginModel.staff = staff;
    }

    public int getLoginCount() {
        return loginCount;
    }
    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public String getInputPW() {
        return inputPW;
    }
    public void setInputPW(String inputPW) {
        this.inputPW = inputPW;
    }

    public String getQrStaffID() {
        return qrStaffID;
    }
    public void setQrStaffID(String qrStaffID) {
        this.qrStaffID = qrStaffID;
    }

    //= Useable Methods ============================================================================
    @Override
    public void checkQrId(String scanStr) throws ProcessException{
        if(scanStr.length() != 6){
            throw new ProcessException("Invalid staff ID Number", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }

        this.qrStaffID  = scanStr;
        //StaffIdentity staff = new StaffIdentity();
        //staff.setIdNo(scanStr);
        //LoginModel.setStaff(staff);
    }

    @Override
    public void matchStaffPw(String inputPw) throws ProcessException{
        if(this.qrStaffID == null) {
            throw new ProcessException("Input ID is null", ProcessException.FATAL_MESSAGE,
                    IconManager.WARNING);
        } else {
            //staff.setPassword(inputPw);
            this.inputPW    = inputPw;
            if(inputPw == null || inputPw.isEmpty()){
                throw new ProcessException("Please enter a password to proceed",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            } else {
                ConnectionTask.setCompleteFlag(false);
                ExternalDbLoader.tryLogin(this.qrStaffID, this.inputPW);
            }
        }
    }

    @Override
    public void checkLoginResult(String msgFromChief) throws ProcessException{
        ConnectionTask.setCompleteFlag(true);
        loginCount--;
        //String pw   = staff.getPassword();

        if(loginCount < 1){
            try{
                staff       = JsonHelper.parseStaffIdentity(msgFromChief, loginCount);
            } catch (ProcessException err) {
                throw new ProcessException("You have failed to login!",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }
        }
        staff       = JsonHelper.parseStaffIdentity(msgFromChief, loginCount);
        staff.setPassword(this.inputPW);

        loginCount  = 3;

        //AttendanceList attdList = JsonHelper.parseAttdList(msgFromChief);
        //AssignModel.setAttdList(attdList);

        //HashMap<String, ExamSubject> papers = JsonHelper.parsePaperMap(msgFromChief);
        //Candidate.setPaperList(papers);

        //ExternalDbLoader.dlPaperList();
    }

    //This method should be removed. Should be placed in TakeAttendance
    //This is temporary for demo purposes
    @Override
    public void checkDetail(String msgFromChief) throws ProcessException {
        AttendanceList attdList = JsonHelper.parseAttdList(msgFromChief);
        AssignModel.setAttdList(attdList);

        HashMap<String, ExamSubject> papers = JsonHelper.parsePaperMap(msgFromChief);
        Candidate.setPaperList(papers);
    }


    @Override
    public void run() {
        try{
            if(!ConnectionTask.isComplete()) {
                ProcessException err = new ProcessException(
                        "Identity verification times out.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err){
            taskPresenter.onTimesOut(err);
        }
    }
}

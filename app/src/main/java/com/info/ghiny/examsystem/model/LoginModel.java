package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.manager.IconManager;

/**
 * Created by GhinY on 15/06/2016.
 *
 * Login Helper is a tools to execute all back end logic for --- MainLoginActivity ---
 * without touching the UI interface. This allow automated test without using a hardware
 * mobile.
 */

public class LoginModel implements LoginMVP.MvpModel {
    private static StaffIdentity staff;
    private int loginCount = 3;
    private LoginMVP.MvpMPresenter taskPresenter;
    private LocalDbLoader dbLoader;

    private String qrStaffID;
    private String inputPW;
    private String hashCode;

    public LoginModel(LoginMVP.MvpMPresenter taskPresenter, LocalDbLoader dbLoader){
        this.taskPresenter  = taskPresenter;
        this.dbLoader       = dbLoader;
    }

    //= Setter & Getter ============================================================================
    public static StaffIdentity getStaff() {
        return staff;
    }
    public static void setStaff(StaffIdentity staff) {
        LoginModel.staff = staff;
    }

    int getLoginCount() {
        return loginCount;
    }
    void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    void setInputPW(String inputPW) {
        this.inputPW = inputPW;
    }

    String getQrStaffID() {
        return qrStaffID;
    }
    void setQrStaffID(String qrStaffID) {
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

        if(staff != null){
            staff = staff.getIdNo().equals(scanStr) ? staff : null;
        }
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

                String duelMsg  = JavaHost.getConnector().getDuelMessage();
                StaffIdentity staffIdentity = new StaffIdentity();
                this.hashCode   = staffIdentity.hmacSha(this.inputPW, duelMsg);

                ExternalDbLoader.tryLogin(this.qrStaffID, hashCode);
            }
        }
    }

    @Override
    public Role checkLoginResult(String msgFromChief) throws ProcessException {
        loginCount--;

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
        staff.setHashPass(this.hashCode);

        dbLoader.saveUser(staff);
        loginCount  = 3;

        return staff.getRole();
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

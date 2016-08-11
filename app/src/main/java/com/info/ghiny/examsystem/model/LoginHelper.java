package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import java.util.HashMap;

/**
 * Created by GhinY on 15/06/2016.
 *
 * Login Helper is a tools to execute all back end logic for --- MainLoginActivity ---
 * without touching the UI interface. This allow automated test without using a hardware
 * mobile.
 */

public class LoginHelper {
    private static StaffIdentity staff;
    private int loginCount = 3;
    //This method take in an id and check if the id is an invigilator identity

    //= Setter & Getter ============================================================================
    public static StaffIdentity getStaff() {
        return staff;
    }
    public static void setStaff(StaffIdentity staff) {
        LoginHelper.staff = staff;
    }

    //= Useable Methods ============================================================================
    public void verifyChief(String scanStr) throws ProcessException{
        if(scanStr.contains("CHIEF:") && scanStr.endsWith("$") && scanStr.startsWith("$")){
            String[] chiefArr   = scanStr.split(":");
            TCPClient.SERVERIP      = chiefArr[1];
            TCPClient.SERVERPORT    = Integer.parseInt(chiefArr[2]);
        } else {
            throw new ProcessException("Not a chief address", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    public void checkQrId(String scanStr) throws ProcessException{
        if(scanStr.length() != 6){
            throw new ProcessException("Invalid staff ID Number", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }

        StaffIdentity staff = new StaffIdentity();
        staff.setIdNo(scanStr);
        LoginHelper.setStaff(staff);
    }

    public void matchStaffPw(String inputPw) throws ProcessException{
        if(staff == null) {
            throw new ProcessException("Input ID is null", ProcessException.FATAL_MESSAGE,
                    IconManager.WARNING);
        } else {
            staff.setPassword(inputPw);
            if(inputPw == null || inputPw.isEmpty()){
                throw new ProcessException("Please enter a password to proceed",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            } else {
                ChiefLink.setCompleteFlag(false);
                ExternalDbLoader.tryLogin(staff.getIdNo(), inputPw);
            }
        }
    }

    public void checkLoginResult(String msgFromChief) throws ProcessException{
        loginCount--;
        String pw   = staff.getPassword();
        staff       = JsonHelper.parseStaffIdentity(msgFromChief, loginCount);
        staff.setPassword(pw);

        loginCount  = 3;

        AttendanceList attdList = JsonHelper.parseAttdList(msgFromChief);
        AssignModel.setAttdList(attdList);

        HashMap<String, ExamSubject> papers = JsonHelper.parsePaperMap(msgFromChief);
        Candidate.setPaperList(papers);
    }
}

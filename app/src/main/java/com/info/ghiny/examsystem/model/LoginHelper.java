package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import java.util.Calendar;
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
            Connector connector     = new Connector(chiefArr[1], Integer.parseInt(chiefArr[2]));
            TCPClient.setConnector(connector);
            //Also save the connector into database
        } else {
            throw new ProcessException("Not a chief address", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    public boolean tryConnection(CheckListLoader dbLoader){
        Connector connector = connector   = dbLoader.queryConnector();

        Calendar now    = Calendar.getInstance();
        if(connector == null){
            return false;
        } else {
            if(now.get(Calendar.YEAR) == connector.getDate().get(Calendar.YEAR)
                    && now.get(Calendar.MONTH) == connector.getDate().get(Calendar.MONTH)
                    && now.get(Calendar.DAY_OF_MONTH) == connector.getDate().get(Calendar.DAY_OF_MONTH)){
                TCPClient.setConnector(connector);
                return true;
            }
            return false;
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
                ConnectionTask.setCompleteFlag(false);
                ExternalDbLoader.tryLogin(staff.getIdNo(), inputPw);
            }
        }
    }

    public void checkLoginResult(String msgFromChief) throws ProcessException{
        ConnectionTask.setCompleteFlag(true);
        loginCount--;
        String pw   = staff.getPassword();

        if(loginCount < 1){
            try{
                staff       = JsonHelper.parseStaffIdentity(msgFromChief, loginCount);
            } catch (ProcessException err) {
                throw new ProcessException("You have failed to login!",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
            }

        }
        staff       = JsonHelper.parseStaffIdentity(msgFromChief, loginCount);
        staff.setPassword(pw);

        AttendanceList attdList = JsonHelper.parseAttdList(msgFromChief);
        AssignModel.setAttdList(attdList);

        HashMap<String, ExamSubject> papers = JsonHelper.parsePaperMap(msgFromChief);
        Candidate.setPaperList(papers);

        loginCount  = 3;
        //ExternalDbLoader.dlPaperList();
    }

    public void checkDetail(String msgFromChief) throws ProcessException {
        AttendanceList attdList = JsonHelper.parseAttdList(msgFromChief);
        AssignModel.setAttdList(attdList);

        HashMap<String, ExamSubject> papers = JsonHelper.parsePaperMap(msgFromChief);
        Candidate.setPaperList(papers);
    }
}

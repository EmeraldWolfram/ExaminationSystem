package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

/**
 * Created by GhinY on 15/06/2016.
 *
 * Login Helper is a tools to execute all back end logic for --- MainLoginActivity ---
 * without touching the UI interface. This allow automated test without using a hardware
 * mobile.
 */

public class LoginHelper {
    private static StaffIdentity staff;
    //This method take in an id and check if the id is an invigilator identity
    //= Setter & Getter ============================================================================
    public static StaffIdentity getStaff() {
        return staff;
    }

    public static void setStaff(StaffIdentity staff) {
        LoginHelper.staff = staff;
    }

    //= Useable Methods ============================================================================
    public static void verifyChief(String scanStr) throws ProcessException{
        if(scanStr.contains("CHIEF:") && scanStr.endsWith("$") && scanStr.startsWith("$")){
            String[] chiefArr = scanStr.split(":");
            String ipAddr   = chiefArr[1];
            int portNum     = Integer.parseInt(chiefArr[2]);

            TCPClient.setServerIp(ipAddr);
            TCPClient.setServerPort(portNum);
        } else {
            throw new ProcessException("Not a chief address", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    public static void identifyStaff(String scanIdNum) throws ProcessException {
        setStaff(ExternalDbLoader.getStaffIdentity(scanIdNum));
        if(staff == null){
            throw new ProcessException("Not an StaffIdentity", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        } else {
            //Check staff status
            if(!staff.getEligible())
                throw new ProcessException("Unauthorized Invigilator",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }
    }

    public static void matchStaffPw(String inputPw) throws ProcessException{
        if(staff == null)
            throw new ProcessException("Input ID is null", ProcessException.FATAL_MESSAGE,
                    IconManager.WARNING);

        if(inputPw == null || inputPw.isEmpty()){
            throw new ProcessException("Please enter a password to proceed",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        }else {
            if (!staff.matchPassword(inputPw))
                throw new ProcessException("Input password is incorrect",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }
    }
    //= Removable Methods ==========================================================================
    //This method check whether the input password was the password of the invglt
    public static void checkInputPassword(StaffIdentity invglt, String pw) throws ProcessException {
        if(invglt == null)
            throw new ProcessException("Input ID is null", ProcessException.FATAL_MESSAGE,
                    IconManager.WARNING);

        if(pw == null || pw.isEmpty()) {
            throw new ProcessException("Please enter a password to proceed",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        } else {
            if (!invglt.matchPassword(pw))
                throw new ProcessException("Input password is incorrect",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }
    }

    public static void checkInvigilator(StaffIdentity invglt) throws ProcessException {
        if(invglt == null){
            throw new ProcessException("Not an StaffIdentity", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        } else {
            if(!invglt.getEligible())
                throw new ProcessException("Unauthorized Invigilator",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }
    }

}

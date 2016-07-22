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
            String[] chiefArr   = scanStr.split(":");
            TCPClient.SERVERIP      = chiefArr[1];
            TCPClient.SERVERPORT    = Integer.parseInt(chiefArr[2]);
        } else {
            throw new ProcessException("Not a chief address", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    public static void checkQrId(String scanStr) throws ProcessException{
        if(scanStr.length() != 6){
            throw new ProcessException("Invalid staff ID Number", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    public static void matchStaffPw(String inputPw) throws ProcessException{
        if(staff == null)
            throw new ProcessException("Input ID is null", ProcessException.FATAL_MESSAGE,
                    IconManager.WARNING);

        if(inputPw == null || inputPw.isEmpty()){
            throw new ProcessException("Please enter a password to proceed",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        } else {
            ExternalDbLoader.tryLogin(staff.getIdNo(), inputPw);
        }
    }
}

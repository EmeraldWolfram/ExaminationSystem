package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.Identity;

/**
 * Created by GhinY on 15/06/2016.
 *
 * Login Helper is a tools to execute all back end logic for --- MainLoginActivity ---
 * without touching the UI interface. This allow automated test without using a hardware
 * mobile.
 */
public class LoginHelper {
    //This method take in an id and check if the id is an invigilator identity
    public static void checkInvigilator(Identity invglt) throws ProcessException {
        if(invglt == null){
            throw new ProcessException("Not an Identity", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        } else {
            if(!invglt.getEligible())
                throw new ProcessException("Unauthorized Invigilator",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }
    }

    //This method check whether the input password was the password of the invglt
    public static void checkInputPassword(Identity invglt, String pw) throws ProcessException {
        if(invglt == null)
            throw new ProcessException("Input ID is null", ProcessException.MESSAGE_TOAST,
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

}

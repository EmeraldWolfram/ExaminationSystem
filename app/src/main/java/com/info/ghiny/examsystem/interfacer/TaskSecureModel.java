package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 14/10/2016.
 */

interface TaskSecureModel {
    void matchPassword(String password) throws ProcessException;
}

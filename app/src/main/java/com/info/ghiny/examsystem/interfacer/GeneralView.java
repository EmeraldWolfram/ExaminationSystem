package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.model.ProcessException;

/**
 * Created by GhinY on 08/08/2016.
 */
public interface GeneralView {
    void displayError(ProcessException err);
    void navigateActivity(Class<?> cls);
    void finishActivity();
}

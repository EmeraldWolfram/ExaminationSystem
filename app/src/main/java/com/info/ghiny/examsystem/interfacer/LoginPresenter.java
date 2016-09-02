package com.info.ghiny.examsystem.interfacer;

import android.content.Intent;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface LoginPresenter {
    /**
     * This method is used to handle the password entered by the user
     */
    void onPasswordReceived(int reqCode, int resCode, Intent intent);
}

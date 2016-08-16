package com.info.ghiny.examsystem.interfacer;

import android.content.Intent;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface LoginPresenter {
    /**
     * onPause() will always be called when:
     * -PopUpLogin that prompt user to key-in password is called
     *
     * onResume() will be called when the PopUpLogin window is gone
     *      &
     * onPasswordReceived() will then be called upon password received
     */
    void onResume(final ErrorManager errorManager);
    void onPause();
    void onDestroy();
    void onScanForIdentity(String scanStr);
    void onPasswordReceived(int reqCode, int resCode, Intent intent);
    void onChiefRespond(ErrorManager errorManager, String message);
}

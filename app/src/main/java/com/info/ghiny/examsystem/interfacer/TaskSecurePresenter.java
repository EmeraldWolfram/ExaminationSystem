package com.info.ghiny.examsystem.interfacer;

import android.content.Intent;

/**
 * Created by GhinY on 02/09/2016.
 */
public interface TaskSecurePresenter {
    void onRestart();
    void onPasswordReceived(int requestCode, int resultCode, Intent data);
}

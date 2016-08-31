package com.info.ghiny.examsystem.interfacer;

import android.content.Intent;
import android.view.View;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface AttendanceListPresenter {
    void onResume(final ErrorManager errorManager);
    void onDestroy();
    void toggleUnassign(View view);
    void signToUpload();
    void onPasswordReceived(int requestCode, int resultCode, Intent data);
}

package com.info.ghiny.examsystem.interfacer;

import android.content.Intent;

import com.info.ghiny.examsystem.database.Candidate;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface AssignPresenter {
    //INTERFACE FOR VIEW
    void onPause();
    void onResume();
    void onRestart();
    void onBackPressed();
    void onScanForTableOrCandidate(String scanStr);
    void onPasswordReceived(int requestCode, int resultCode, Intent data);
    void navigateToDisplay();
    void navigateToDetail();

    //INTERFACE FOR MODEL
    void displayTable(Integer tableNumber);
    void displayCandidate(Candidate cdd);
    void resetDisplay();
}

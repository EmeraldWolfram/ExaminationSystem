package com.info.ghiny.examsystem.interfacer;

import android.content.Intent;

import com.info.ghiny.examsystem.database.Candidate;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface AssignPresenter {
    //INTERFACE FOR VIEW
    void onBackPressed();
    void onSwipeLeft();
    void onSwipeBottom();

    //INTERFACE FOR MODEL
    void displayTable(Integer tableNumber);
    void displayCandidate(Candidate cdd);
    void resetDisplay();
}

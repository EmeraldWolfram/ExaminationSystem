package com.info.ghiny.examsystem.interfacer;

/**
 * Created by GhinY on 19/08/2016.
*/
public interface ConnectPresenter {
    void onResume();
    void onPause();
    void onDestroy();
    void onScanForChief(String scanStr);
    void setupConnection();
}

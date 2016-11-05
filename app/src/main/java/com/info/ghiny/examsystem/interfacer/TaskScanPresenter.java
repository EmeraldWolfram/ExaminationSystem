package com.info.ghiny.examsystem.interfacer;

/**
 * Created by GhinY on 02/09/2016.
 */
interface TaskScanPresenter {
    void onScan(String scanStr);
    void onPause();
    void onResume();
    void loadSetting();
}

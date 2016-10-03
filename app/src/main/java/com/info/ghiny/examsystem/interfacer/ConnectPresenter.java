package com.info.ghiny.examsystem.interfacer;

import com.google.zxing.client.android.BeepManager;

/**
 * Created by GhinY on 19/08/2016.
*/
public interface ConnectPresenter {
    /**
     * The objective of this method is to
     * 1. Check if there is a valid connector in database
     * 2. a. If No, do nothing
     * 2. b. If Yes, try to reconnect with the Chief IP in the database
     */
    void onCreate();

    /**
     * This method is a cleaning method that use to close the
     * connection between the user (this Android) and the Chief (PC)
     */
    void onDestroy();
}

package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface CollectionPresenter {
    void onPause();
    void onResume(final ErrorManager errorManager);
    void onDestroy();
    void onScanForCollection(String scanStr);
}

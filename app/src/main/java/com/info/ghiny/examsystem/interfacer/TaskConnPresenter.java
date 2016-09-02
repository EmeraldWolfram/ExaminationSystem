package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 02/09/2016.
 */
public interface TaskConnPresenter {
    void onResume(final ErrorManager errManager);
    void onChiefRespond(ErrorManager errManager, String messageRx);
    void onDestroy();
}

package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 16/08/2016.
 */
public interface ObtainInfoPresenter {
    void onPause();
    void onResume(final ErrorManager errManager);
    void onDestroy();
    void onScanForCandidateDetail(String scanStr);
}

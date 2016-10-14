package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 02/09/2016.
 */
interface TaskConnPresenter {
    /**
     * onResume(...)
     *
     * This method used to inject the message listener to the Running Connection Thread
     * So that the message received will be handled
     *
     * @param errManager    The ErrorManager used by the Connection Thread to display result
     */
    void onResume(final ErrorManager errManager);

    /**
     * onChiefRespond(...)
     *
     * As the name of the method said, this method is used to handle the message
     * received from the Chief
     *
     * @param errManager    ErrorManager used by the ConnectionThread to display result
     * @param messageRx     The message received from the Chief
     */
    void onChiefRespond(ErrorManager errManager, String messageRx);

    /**
     * onDestroy()
     *
     * This method used to clean up the code such as removing the Progress Dialog
     * open by calling openProgressWindow() or remove timer callback.
     *
     */
    void onDestroy();
}

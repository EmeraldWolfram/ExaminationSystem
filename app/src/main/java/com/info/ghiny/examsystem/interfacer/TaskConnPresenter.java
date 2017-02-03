package com.info.ghiny.examsystem.interfacer;

import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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

package com.info.ghiny.examsystem.interfacer;

import android.view.View;

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
interface TaskScanView extends Runnable{
    /**
     * beep()
     *
     * When using a QR scanner, BeepManager should always exist
     * This method make a beep sound
     */
    void beep();

    /**
     * pauseScanning()
     *
     * As the name said, this method pause the camera from capturing QR code
     */
    void pauseScanning();

    /**
     * resumeScanning()
     *
     * As the name said, this method resume the camera to continue capture for QR code
     */
    void resumeScanning();

    void changeScannerSetting(boolean crossHair, boolean beep, boolean vibrate, int mode);

    void onInitiateScan(View view);
}

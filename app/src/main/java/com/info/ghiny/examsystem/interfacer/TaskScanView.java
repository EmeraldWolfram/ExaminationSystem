package com.info.ghiny.examsystem.interfacer;

import android.view.View;

/**
 * Created by GhinY on 08/08/2016.
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

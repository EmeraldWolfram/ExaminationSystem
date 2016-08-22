package com.info.ghiny.examsystem.interfacer;

/**
 * Created by GhinY on 08/08/2016.
 */
public interface ScannerView extends GeneralView {
    void securityPrompt();
    void pauseScanning();
    void resumeScanning();
}

package com.info.ghiny.examsystem.manager;

import android.content.DialogInterface;
import android.os.Handler;

import com.google.zxing.client.android.Intents;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ScannerView;
import com.info.ghiny.examsystem.model.ChiefLink;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.InfoCollectHelper;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 08/08/2016.
 */
public class CollectManager {
    private InfoCollectHelper infoModel;
    private ScannerView scannerView;
    private Handler handler;

    public CollectManager(ScannerView scannerView){
        this.scannerView    = scannerView;
        this.infoModel      = new InfoCollectHelper();
        this.handler        = new Handler();
    }

    public void onScanForCollection(String scanStr){
        try{
            scannerView.pauseScanning();
            infoModel.bundleCollection(scanStr);
            handler.postDelayed(timer, 10000);

        } catch (ProcessException err) {
            scannerView.displayError(err);
            scannerView.resumeScanning();
        }
    }

    public void onPause(){
        scannerView.pauseScanning();
    }

    public void onResume(final ErrorManager errorManager){
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            //here the messageReceived method is implemented
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(true);
                    boolean ack = JsonHelper.parseBoolean(message);
                } catch (ProcessException err) {
                    ExternalDbLoader.getChiefLink().publishError(errorManager, err);
                }
            }
        });
        scannerView.resumeScanning();
    }

    public void onDestroy(){
        handler.removeCallbacks(timer);
    }

    private DialogInterface.OnClickListener timesOutListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeScanning();
                    dialog.cancel();
                }
            };

    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            if(!ChiefLink.isComplete()){
                ProcessException err = new ProcessException(
                        "Bundle collection times out.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, timesOutListener);
                if(scannerView != null){
                    scannerView.pauseScanning();
                    scannerView.displayError(err);
                    scannerView.resumeScanning();
                }
            }
        }
    };
}

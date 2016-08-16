package com.info.ghiny.examsystem.manager;

import android.content.DialogInterface;
import android.os.Handler;

import com.info.ghiny.examsystem.ExamListActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.ObtainInfoPresenter;
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
public class ObtainInfoManager implements ObtainInfoPresenter{
    private InfoCollectHelper infoModel;
    private ScannerView scannerView;
    private String studentSubjects;
    private Handler handler;

    public ObtainInfoManager(ScannerView scannerView){
        this.scannerView    = scannerView;
        this.infoModel      = new InfoCollectHelper();
        this.handler        = new Handler();
    }

    public void setInfoModel(InfoCollectHelper infoModel) {
        this.infoModel = infoModel;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String getStudentSubjects() {
        return studentSubjects;
    }

    @Override
    public void onPause(){
        scannerView.pauseScanning();
    }

    @Override
    public void onResume(final ErrorManager errManager){
        ExternalDbLoader.getTcpClient().setMessageListener(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                try{
                    ChiefLink.setCompleteFlag(true);
                    boolean ack =   JsonHelper.parseBoolean(message);
                    studentSubjects = message;
                    scannerView.navigateActivity(ExamListActivity.class);
                } catch (ProcessException err) {
                    ExternalDbLoader.getChiefLink().publishError(errManager, err);
                }
            }
        });

        scannerView.resumeScanning();
    }

    @Override
    public void onDestroy(){
        handler.removeCallbacks(timer);
    }

    @Override
    public void onScanForCandidateDetail(String scanStr){
        try{
            scannerView.pauseScanning();
            infoModel.reqCandidatePapers(scanStr);
            handler.postDelayed(timer, 5000);
        } catch (ProcessException err){
            scannerView.displayError(err);
            scannerView.resumeScanning();
        }
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
                        "Server busy. Request times out. \n Please try again later.",
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

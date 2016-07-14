package com.info.ghiny.examsystem.tools;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ChiefLink extends AsyncTask<String, String, TCPClient> {

    private static boolean msgReadyFlag = false;
    private static String msgReceived   = null;
    private static boolean timesOutFlag = false;
    private static boolean uploadedFlag = false;

    private static ErrorManager errorManager;

    public static void setErrorManager(ErrorManager errorManager) {
        ChiefLink.errorManager = errorManager;
    }

    //= Setter & Getter ============================================================================
    public static String getMsgReceived() {
        return msgReceived;
    }
    public static void setMsgReceived(String msgReceived) {
        ChiefLink.msgReceived = msgReceived;
    }

    public static boolean isMsgReadyFlag() {
        return msgReadyFlag;
    }
    public static void setMsgReadyFlag(boolean msgReadyFlag) {
        ChiefLink.msgReadyFlag = msgReadyFlag;
    }

    public static boolean isTimesOutFlag() {
        return timesOutFlag;
    }
    public static void setTimesOutFlag(boolean timesOutFlag) {
        ChiefLink.timesOutFlag = timesOutFlag;
    }

    public static boolean isUploadedFlag() {
        return uploadedFlag;
    }
    public static void setUploadedFlag(boolean uploadedFlag) {
        ChiefLink.uploadedFlag = uploadedFlag;
    }

    //= Public Methods =============================================================================
    public void publishMessage(String msg){
        publishProgress(msg);
    }

    @Override
    protected TCPClient doInBackground(String... params) {
        if(ExternalDbLoader.getTcpClient() != null)
            ExternalDbLoader.getTcpClient().run();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if(values[0].contains("Result")){
            setMsgReadyFlag(true);
            setMsgReceived(values[0]);
        }

        errorManager.showToast(values[0]);
        //ExternalDbLoader.checkForResult();
    }
}

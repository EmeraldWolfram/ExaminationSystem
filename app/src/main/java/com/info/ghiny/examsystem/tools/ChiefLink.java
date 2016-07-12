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

    //private static ErrorManager errorManager;

    //public static void setErrorManager(ErrorManager errorManager) {
    //    ChiefLink.errorManager = errorManager;
    //}

    //= Setter & Getter ============================================================================
    public static String getMsgReceived() {
        return msgReceived;
    }

    public static boolean isMsgReadyFlag() {
        return msgReadyFlag;
    }

    public static void setMsgReceived(String msgReceived) {
        ChiefLink.msgReceived = msgReceived;
    }

    public static void setMsgReadyFlag(boolean msgReadyFlag) {
        ChiefLink.msgReadyFlag = msgReadyFlag;
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

        //errorManager.showToast(values[0]);
        setMsgReadyFlag(true);
        setMsgReceived(values[0]);
    }
}

package com.info.ghiny.examsystem.tools;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ChiefLink extends AsyncTask<String, String, TCPClient> {

    private static boolean msgValid     = false;
    private static boolean completeFlag = false;
    private static boolean timesOutFlag = false;

    private static ErrorManager errorManager;

    //= Setter & Getter ============================================================================
    public static void setErrorManager(ErrorManager errorManager) {
        ChiefLink.errorManager = errorManager;
    }

    public static boolean isMsgValid() {
        return msgValid;
    }
    public static void setMsgValidFlag(boolean msgValidFlag) {
        ChiefLink.msgValid = msgValidFlag;
    }

    public static boolean isTimesOutFlag() {
        return timesOutFlag;
    }
    public static void setTimesOutFlag(boolean timesOutFlag) {
        ChiefLink.timesOutFlag = timesOutFlag;
    }

    public static boolean isComplete() {
        return completeFlag;
    }
    public static void setCompleteFlag(boolean completeFlag) {
        ChiefLink.completeFlag = completeFlag;
    }

    //= Public Methods =============================================================================
    public void publishMessage(String msg){
        publishProgress(msg);
    }

    @Override
    protected TCPClient doInBackground(String... params) {
        if(ExternalDbLoader.getTcpClient() != null){
            ExternalDbLoader.getTcpClient().run();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);

        setMsgValidFlag(false);
        setCompleteFlag(false);
        errorManager.showToast(values[0]);
        if(values[0].contains("Result")){
        }

        //ExternalDbLoader.checkForResult();
    }
}

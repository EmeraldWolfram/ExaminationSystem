package com.info.ghiny.examsystem.tools;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ChiefLink extends AsyncTask<String, String, TCPClient> {

    private static boolean completeFlag = false;
    //private static ErrorManager errorManager;

    //= Setter & Getter ============================================================================
    //public static void setErrorManager(ErrorManager errorManager) {
    //    ChiefLink.errorManager = errorManager;
    //}

    //public static ErrorManager getErrorManager() {
    //    return errorManager;
    //}

    //= Setter & Getter ============================================================================
    public static boolean isComplete() {
        return completeFlag;
    }
    public static void setCompleteFlag(boolean completeFlag) {
        ChiefLink.completeFlag = completeFlag;
    }

    //= Public Methods =============================================================================
    public void publishMsg(String msg){
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
        setCompleteFlag(false);
    }
}

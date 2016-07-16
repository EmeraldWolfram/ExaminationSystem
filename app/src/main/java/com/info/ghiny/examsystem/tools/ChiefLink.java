package com.info.ghiny.examsystem.tools;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.database.ExternalDbLoader;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ChiefLink extends AsyncTask<String, String, TCPClient> {

    private static boolean completeFlag = false;
    //= Setter & Getter ============================================================================
    public static boolean isComplete() {
        return completeFlag;
    }
    public static void setCompleteFlag(boolean completeFlag) {
        ChiefLink.completeFlag = completeFlag;
    }

    //= Public Methods =============================================================================
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

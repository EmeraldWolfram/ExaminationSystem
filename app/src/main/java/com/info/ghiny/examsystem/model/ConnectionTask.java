package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ConnectionTask extends AsyncTask<String, String, TCPClient> {

    private static boolean completeFlag = false;
    private ErrorManager errorManager;
    private ProcessException err;

    //= Setter & Getter ============================================================================
    public static boolean isComplete() {
        return completeFlag;
    }
    public static void setCompleteFlag(boolean completeFlag) {
        ConnectionTask.completeFlag = completeFlag;
    }

    //= Public Methods =============================================================================
    public void publishError(ErrorManager errManager, ProcessException err){
        this.errorManager   = errManager;
        this.err            = err;
        publishProgress("Error");
    }

    @Override
    protected TCPClient doInBackground(String... params) {

        TCPClient tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {}
        });
        ExternalDbLoader.setTcpClient(tcpClient);
        if(tcpClient != null)
            tcpClient.run();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
        errorManager.displayError(err);
    }
}

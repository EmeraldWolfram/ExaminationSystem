package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.manager.ErrorManager;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ConnectionTask extends AsyncTask<String, String, JavaHost> {

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
    protected JavaHost doInBackground(String... params) {

        JavaHost javaHost = new JavaHost(new JavaHost.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {}
        });
        ExternalDbLoader.setJavaHost(javaHost);
        if(javaHost != null)
            javaHost.run();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
        errorManager.displayError(err);
    }
}

package com.info.ghiny.examsystem.tools;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.ExternalDbLoader;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ChiefLink extends AsyncTask<String, String, TCPClient> {

    private String strReceived;

    @Override
    protected TCPClient doInBackground(String... params) {
        TCPClient mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                try {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mTcpClient.run();
        ExternalDbLoader.setTcpClient(mTcpClient);
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        ExternalDbLoader.setMsgReadyFlag(true);
        ExternalDbLoader.setMsgReceived(values[0]);
    }
}

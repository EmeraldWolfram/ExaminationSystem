package com.info.ghiny.examsystem.tools;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.ExternalDbLoader;

/**
 * Created by GhinY on 08/07/2016.
 */
public class ChiefLink extends AsyncTask<String, String, TCPClient> {

    private static boolean msgReadyFlag = false;
    private static String msgReceived   = null;

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
        setMsgReadyFlag(true);
        setMsgReceived(values[0]);
    }
}

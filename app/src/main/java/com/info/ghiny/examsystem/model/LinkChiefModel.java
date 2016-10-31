package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by GhinY on 05/10/2016.
 */

public class LinkChiefModel implements LinkChiefMVP.ModelFace {
    private LinkChiefMVP.MPresenter taskPresenter;
    private CheckListLoader dbLoader;

    public LinkChiefModel(CheckListLoader dbLoader, LinkChiefMVP.MPresenter taskPresenter){
        this.dbLoader       = dbLoader;
        this.taskPresenter  = taskPresenter;
    }

    @Override
    public void tryConnectWithQR(String scanStr) throws ProcessException{
        String[] chiefArr   = scanStr.split(":");
        if(chiefArr.length == 4 && scanStr.endsWith("$") && scanStr.startsWith("$")){
            Connector connector     = new Connector(chiefArr[1],
                    Integer.parseInt(chiefArr[2]), chiefArr[3]);

            dbLoader.saveConnector(connector);
            TCPClient.setConnector(connector);
            ConnectionTask connect   = new ConnectionTask();
            connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setConnectionTask(connect);
        } else {
            throw new ProcessException("Not a chief address", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    @Override
    public boolean tryConnectWithDatabase() {
        Connector connector = dbLoader.queryConnector();
        Calendar now    = Calendar.getInstance();

        if(connector == null){
            return false;
        } else {
            if(now.get(Calendar.YEAR) == connector.getDate().get(Calendar.YEAR)
                    && now.get(Calendar.MONTH) == connector.getDate().get(Calendar.MONTH)
                    && now.get(Calendar.DAY_OF_MONTH) == connector.getDate().get(Calendar.DAY_OF_MONTH)){

                TCPClient.setConnector(connector);
                ConnectionTask connect   = new ConnectionTask();
                connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                ExternalDbLoader.setConnectionTask(connect);
                return true;
            } else {
                dbLoader.clearUserDatabase();
                dbLoader.clearDatabase();
                //Remove away previous database
                return false;
            }
        }
    }

    @Override
    public void onChallengeMessageReceived(String messageRx) throws ProcessException {
        String challengeMsg = JsonHelper.parseChallengeMessage(messageRx);
        TCPClient.getConnector().setDuelMessage(challengeMsg);
    }

    @Override
    public void closeConnection() throws Exception{
        if(ExternalDbLoader.getTcpClient() != null){
            ExternalDbLoader.getTcpClient().sendMessage("Termination");
            ExternalDbLoader.getTcpClient().stopClient();
        }
        if(ExternalDbLoader.getConnectionTask() != null){
            ExternalDbLoader.getConnectionTask().cancel(true);
            ExternalDbLoader.setConnectionTask(null);
        }
    }

    @Override
    public void reconnect() throws ProcessException {
        if(!dbLoader.emptyUserInDB()){
            StaffIdentity prevStaff = dbLoader.queryUser();
            ConnectionTask.setCompleteFlag(false);
            ExternalDbLoader.requestDuelMessage(prevStaff.getIdNo());
        } else {
            throw new ProcessException("Failed to reconnect, no reference of staff in database",
                    ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
        }
    }

    @Override
    public void run() {
        try{
            if(!ConnectionTask.isComplete()) {
                ProcessException err = new ProcessException(
                        "Reconnection times out. Find Chief and connect with QR code",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err){
            taskPresenter.onTimesOut(err);
        }
    }
}

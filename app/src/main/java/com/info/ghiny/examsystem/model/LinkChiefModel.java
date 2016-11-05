package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;
import android.util.Log;

import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;

import java.util.Calendar;

/**
 * Created by GhinY on 05/10/2016.
 */

public class LinkChiefModel implements LinkChiefMVP.ModelFace {
    private LinkChiefMVP.MPresenter taskPresenter;
    private CheckListLoader dbLoader;
    private ConnectionTask task;

    public LinkChiefModel(CheckListLoader dbLoader, LinkChiefMVP.MPresenter taskPresenter){
        this.dbLoader       = dbLoader;
        this.taskPresenter  = taskPresenter;
    }

    public void setTask(ConnectionTask task) {
        this.task = task;
    }

    @Override
    public void tryConnectWithQR(String scanStr) throws ProcessException{
        String[] chiefArr   = scanStr.split(":");
        if(chiefArr.length == 5 && scanStr.endsWith("$") && scanStr.startsWith("$")){
            Connector connector     = new Connector(chiefArr[1],
                    Integer.parseInt(chiefArr[2]), chiefArr[3]);

            dbLoader.saveConnector(connector);
            TCPClient.setConnector(connector);
            task    = new ConnectionTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setConnectionTask(task);
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
                task    = new ConnectionTask();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                ExternalDbLoader.setConnectionTask(task);
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
    public boolean reconnect() throws ProcessException {
        if(!dbLoader.emptyUserInDB()){
            StaffIdentity prevStaff = dbLoader.queryUser();
            taskPresenter.setRequestComplete(false);
            ExternalDbLoader.requestDuelMessage(prevStaff.getIdNo());
            return true;
        } //else {
        //throw new ProcessException("Failed to reconnect, no reference of staff in database",
        //      ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
        //}
        return false;
    }

    @Override
    public void run() {
        try{
            if(!taskPresenter.isRequestComplete()) {
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

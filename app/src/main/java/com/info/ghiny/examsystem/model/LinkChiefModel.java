package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Role;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.ThreadManager;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;
import com.info.ghiny.examsystem.manager.IconManager;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by GhinY on 05/10/2016.
 */

public class LinkChiefModel implements LinkChiefMVP.ModelFace {
    private LinkChiefMVP.MPresenter taskPresenter;
    private LocalDbLoader dbLoader;
    private ConnectionTask task;

    public LinkChiefModel(LocalDbLoader dbLoader, LinkChiefMVP.MPresenter taskPresenter){
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
            prepareConnector(chiefArr);
            task    = new ConnectionTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ExternalDbLoader.setConnectionTask(task);
        } else {
            throw new ProcessException("Not a chief address", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }
    }

    void prepareConnector(String[] chiefArr){
        Role host       = Role.parseRole(chiefArr[0].substring(1));
        String ip       = chiefArr[1];
        Integer port    = Integer.parseInt(chiefArr[2]);
        String msg      = chiefArr[3];

        Connector connector     = new Connector(ip, port, msg);
        connector.setMyHost(host);

        if(connector.getMyHost() == Role.CHIEF){
            dbLoader.saveConnector(connector);
        }
        JavaHost.setConnector(connector);
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

                JavaHost.setConnector(connector);
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
        JavaHost.getConnector().setDuelMessage(challengeMsg);
    }

    @Override
    public void closeConnection() throws Exception{
        if(ExternalDbLoader.getJavaHost() != null){
            String end = JsonHelper.formatString(JsonHelper.TYPE_TERMINATION, "Terminate");
            ExternalDbLoader.getJavaHost().putMessageIntoSendQueue(end);
            ExternalDbLoader.getJavaHost().stopClient();
        }
        if(ExternalDbLoader.getConnectionTask() != null){
            ExternalDbLoader.getConnectionTask().cancel(true);
            ExternalDbLoader.setConnectionTask(null);
        }
        if(ThreadManager.isRunning()){
            HashMap<Integer, AndroidClient> map = ThreadManager.getClientsMap();
            if(map != null){
                while(map.size() > 0){
                    map.remove(0).stopClient();
                }
            }
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

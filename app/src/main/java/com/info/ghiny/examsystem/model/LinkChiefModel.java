package com.info.ghiny.examsystem.model;

import android.os.AsyncTask;

import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.interfacer.LinkChiefMVP;

import java.util.Calendar;

/**
 * Created by GhinY on 05/10/2016.
 */

public class LinkChiefModel implements LinkChiefMVP.ModelFace {

    private CheckListLoader dbLoader;

    public LinkChiefModel(CheckListLoader dbLoader){
        this.dbLoader   = dbLoader;
    }

    @Override
    public void tryConnectWithQR(String scanStr) throws ProcessException{
        if(scanStr.contains("CHIEF:") && scanStr.endsWith("$") && scanStr.startsWith("$")){
            String[] chiefArr   = scanStr.split(":");
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
    public boolean tryConnectWithDatabase(){
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
                //Check on connection established is required
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
}

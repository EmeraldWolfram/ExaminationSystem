package com.info.ghiny.examsystem.model;

import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.info.ghiny.examsystem.DistributionActivity;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.database.TasksSynchronizer;
import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.manager.IconManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user09 on 12/16/2016.
 */

public class AndroidClient extends Thread {

    private String serverMessage;
    private ServerSocket serverSocket;
    private Connector connector;
    private int localPort;
    private boolean running = false;

    private DistributionMVP.MvpView tempView;
    private DistributionMVP.MvpModel tempModel;
    private PowerManager.WakeLock wakeLock;

    private PrintWriter out = null;
    private BufferedReader in = null;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */

    public AndroidClient(PowerManager.WakeLock wakeLock){
        this.wakeLock   = wakeLock;
    }

    public void setTempView(DistributionMVP.MvpView tempView) {
        this.tempView = tempView;
    }

    public void setTempModel(DistributionMVP.MvpModel tempModel) {
        this.tempModel = tempModel;
    }

    public boolean isRunning() {
        return running;
    }

    public int getLocalPort() {
        return localPort;
    }

    public Connector getConnector() {
        return connector;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        running = false;
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException err){
            Log.d(DistributionActivity.TAG, err.getMessage());
        }
    }

    @Override
    public void run(){
        running = true;
        Socket socket;
        try {
            serverSocket    = new ServerSocket(0);
            localPort       = serverSocket.getLocalPort();


            if(tempView != null && tempModel != null){
                tempView.runItSeparate(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            tempView.setImageQr(tempModel.encodeQr(localPort));
                        } catch (ProcessException err) {
                            tempView.displayError(err);
                        }
                    }
                });
            }
            socket          = serverSocket.accept();
            connector       = new Connector(socket.getInetAddress().toString(),
                    socket.getPort(), TCPClient.getConnector().getDuelMessage());

            Log.d(DistributionActivity.TAG, "First Connection");

            TasksSynchronizer.notifyClientConnected(this);

            try {
                out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (running) {
                    serverMessage = in.readLine();
                    wakeLock.acquire(3000);
                    Log.d("#### RECEIVED ###: ", serverMessage);

                    if (serverMessage != null) {
                        onReceiveClientMessage(serverMessage);
                    }
                    serverMessage = null;
                }
            } catch (Exception e) {
                throw new ProcessException(e.getMessage(), ProcessException.FATAL_MESSAGE,
                        IconManager.WARNING);
            } finally {
                socket.close();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void onReceiveClientMessage(String clientMsg){
        try{
            String type = JsonHelper.parseType(clientMsg);
            switch (type) {
                case JsonHelper.TYPE_ATTENDANCE_UP:
                    onAttendanceUpdateFromClients(clientMsg);
                    break;
                case JsonHelper.TYPE_VENUE_INFO:
                    onReqVenueInfo();
                    break;
                default:
                    onExtraReq(clientMsg);
                    break;
            }

        } catch (ProcessException err) {
            Log.d(DistributionActivity.TAG, err.getErrorMsg());
        }
    }

    void onExtraReq(String inStr){
        try{
            JSONObject jsonObject   = new JSONObject(inStr);
            jsonObject.remove(JsonHelper.MAJOR_KEY_TYPE_ID);
            jsonObject.put(JsonHelper.MAJOR_KEY_TYPE_ID, localPort);
            sendMessage(jsonObject.toString());
        } catch (JSONException err){
            Log.d(DistributionActivity.TAG, err.getMessage());
        }
    }

    void onReqVenueInfo(){
        AttendanceList attdList                 = TakeAttdModel.getAttdList();
        HashMap<String, ExamSubject> subjects   = Candidate.getPaperList();

        String messageOut   = JsonHelper.formatVenueInfo(attdList, subjects);
        sendMessage(messageOut);
    }

    void onAttendanceUpdateFromClients(String inStr){
        try{
            ArrayList<Candidate> modifyList = JsonHelper.parseUpdateList(inStr);
            for(int i=0; i < modifyList.size(); i++){
                Candidate cdd = modifyList.get(i);
                if(cdd.getStatus() == Status.PRESENT){
                    TakeAttdModel.assignCandidate(cdd.getCollector(), cdd.getRegNum(),
                            cdd.getTableNumber(), cdd.isLate());
                    TakeAttdModel.updatePresentForUpdatingList(cdd);
                } else {
                    TakeAttdModel.unassignCandidate(cdd.getRegNum());
                    TakeAttdModel.updateAbsentForUpdatingList(cdd);
                }
            }

            HashMap<Integer, AndroidClient> clients = TasksSynchronizer.getClientsMap();
            for(AndroidClient client : clients.values()){
                if(client.getLocalPort() != localPort){
                    sendMessage(inStr);
                }
            }

        } catch (ProcessException err) {
            Log.d(DistributionActivity.TAG, err.getErrorMsg());
        }
    }

}

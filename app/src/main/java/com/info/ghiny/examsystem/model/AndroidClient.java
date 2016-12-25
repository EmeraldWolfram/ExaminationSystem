package com.info.ghiny.examsystem.model;

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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by user09 on 12/16/2016.
 */

public class AndroidClient extends Thread {

    private String serverMessage;
    private ServerSocket serverSocket;
    private Connector connector;
    private int localPort;
    private boolean running;
    private boolean sending;


    private DistributionMVP.MvpView tempView;
    private DistributionMVP.MvpModel tempModel;
    private PowerManager.WakeLock wakeLock;
    private Semaphore binarySem;
    private ArrayList<String> messageQueue;

    private PrintWriter out = null;
    private BufferedReader in = null;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */

    public AndroidClient(PowerManager.WakeLock wakeLock){
        this.wakeLock   = wakeLock;
        this.binarySem  = new Semaphore(1, true);
        this.messageQueue   = new ArrayList<>();
        this.running        = false;
        this.sending        = false;
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

    public DistributionMVP.MvpView getTempView(){
        return tempView;
    }

    public DistributionMVP.MvpModel getTempModel() {
        return tempModel;
    }


    public void putMessageIntoSendQueue(String message){
        try{
            binarySem.acquire();
            messageQueue.add(message);
        } catch (InterruptedException err) {
            Log.d(DistributionActivity.TAG, err.getMessage());
        } finally {
            binarySem.release();
        }

        if(!sending){
            sending = true;

            Thread sendOutThread = new Thread(){
                @Override
                public void run() {
                    while (messageQueue.size() > 0) {
                        try {
                            sendMessage(messageQueue.remove(0));
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Log.d(DistributionActivity.TAG, e.getMessage());
                        }
                    }
                    sending = false;
                }
            };
            sendOutThread.start();
        }
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
        } catch (final IOException err){
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
                    socket.getPort(), JavaHost.getConnector().getDuelMessage());

            if (tempView != null){
                tempView.runItSeparate(new Runnable() {
                    @Override
                    public void run() {
                        tempView.displayError(new ProcessException(connector.getIpAddress()
                                + " Connected", ProcessException.MESSAGE_TOAST,
                                IconManager.ASSIGNED));

                    }
                });
            }

            TasksSynchronizer.notifyClientConnected(this);

            try {
                out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (running) {
                    serverMessage = in.readLine();
                    if(wakeLock != null){
                        wakeLock.acquire();
                    }
                    if (serverMessage != null) {
                        Log.d("#### RECEIVED ###: ", serverMessage);
                        onReceiveClientMessage(serverMessage);
                    }
                    serverMessage = null;
                }
            } catch (SocketException e) {
                throw new ProcessException(e.getMessage(), ProcessException.FATAL_MESSAGE,
                        IconManager.WARNING);
            } finally {
                socket.close();
            }
        } catch (Exception err){
            Log.d(DistributionActivity.TAG, err.getMessage());
        }
    }

    void onReceiveClientMessage(String clientMsg){
        try{
            String type = JsonHelper.parseType(clientMsg);
            switch (type) {
                case JsonHelper.TYPE_TERMINATION:
                    serverSocket.close();
                    break;
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

        } catch (Exception err) {
            Log.d(DistributionActivity.TAG, err.getMessage());
        } finally {
            if(wakeLock != null && wakeLock.isHeld()){
                wakeLock.release();
            }
        }
    }

    void onExtraReq(String inStr){
        try{
            JSONObject jsonObject   = new JSONObject(inStr);
            jsonObject.remove(JsonHelper.MAJOR_KEY_TYPE_ID);
            jsonObject.put(JsonHelper.MAJOR_KEY_TYPE_ID, localPort);
            ExternalDbLoader.getJavaHost().putMessageIntoSendQueue(jsonObject.toString());
        } catch (final JSONException err){
            Log.d(DistributionActivity.TAG, err.getMessage());
        }
    }

    void onReqVenueInfo(){
        AttendanceList attdList                 = TakeAttdModel.getAttdList();
        HashMap<String, ExamSubject> subjects   = Candidate.getPaperList();

        String messageOut   = JsonHelper.formatVenueInfo(attdList, subjects);
        putMessageIntoSendQueue(messageOut);
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
                    client.putMessageIntoSendQueue(inStr);
                }
            }

        } catch (ProcessException err) {
            Log.d(DistributionActivity.TAG, err.getErrorMsg());
        }
    }

}

package com.info.ghiny.examsystem.database;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.info.ghiny.examsystem.DistributionActivity;
import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.AndroidClient;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by user09 on 12/13/2016.
 */

public final class ThreadManager extends Service{

    private static PowerManager powerManager;
    private static PowerManager.WakeLock wakeLock;

    private static boolean running = false;
    private static boolean distributed = false;
    private static HashMap<Integer, AndroidClient> clientsMap = new HashMap<>();

    private static DistributionMVP.MvpView view;
    private static DistributionMVP.MvpModel model;

    private static AndroidClient waitingThread;

    //==============================================================================================

    public static boolean isRunning() {
        return running;
    }

    public static boolean isDistributed() {
        return distributed;
    }

    public static HashMap<Integer, AndroidClient> getClientsMap() {
        return clientsMap;
    }

    static void setRunning(boolean running){
        ThreadManager.running   = running;
    }

    static void setDistributed(boolean distributed){
        ThreadManager.distributed   = distributed;
    }

    static void setClientsMap(HashMap<Integer, AndroidClient> testMap){
        clientsMap  = testMap;
    }

    static void setWaitingThread(AndroidClient waitingThread) {
        ThreadManager.waitingThread = waitingThread;
    }

    static AndroidClient getWaitingThread() {
        return waitingThread;
    }

    static void setPowerManager(PowerManager powerManager){
        ThreadManager.powerManager    = powerManager;
    }

    static void setWakeLock(PowerManager.WakeLock wakeLock){
        ThreadManager.wakeLock    = wakeLock;
    }

    //==============================================================================================

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        if(powerManager == null){
            powerManager    = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        if(wakeLock == null){
            wakeLock        = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    DistributionActivity.TAG);
        }
    }

    @Override
    public void onDestroy() {
        for(AndroidClient client : clientsMap.values()){
            client.stopClient();
        }
        distributed = false;
        clientsMap.clear();
        super.onDestroy();
    }

    public static void startNewThread(DistributionMVP.MvpView view, DistributionMVP.MvpModel model){
        if(view != null && model != null){
            waitingThread    = new AndroidClient(wakeLock);
            ThreadManager.view  = view;
            ThreadManager.model = model;

            waitingThread.setTempView(view);
            waitingThread.setTempModel(model);

            waitingThread.start();
        }
    }

    public static void updateAttendance(ArrayList<Candidate> updatingList){
        if(clientsMap.size() > 0){
            String msgUpdate    = JsonHelper.formatAttendanceUpdate(updatingList);

            for(AndroidClient client : clientsMap.values()){
                client.putMessageIntoSendQueue(msgUpdate);
            }
            updatingList.clear();
        }
    }

    public static void passMessageBack(int deviceId, String inStr){
        AndroidClient targetClient  = clientsMap.get(deviceId);
        if(targetClient != null){
            targetClient.putMessageIntoSendQueue(inStr);
        }
    }

    public static void notifyClientConnected(AndroidClient which){
        clientsMap.put(which.getLocalPort(), which);
        waitingThread   = null;
        distributed     = true;
        startNewThread(view, model);
    }

    public static void removeUnconnectedThread(){
        if(waitingThread != null){
            waitingThread.stopClient();
            waitingThread   = null;
        }
    }

    public static String getThisIpv4() throws ProcessException {
        String ip = null;
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        if(!inetAddress.getHostAddress().startsWith("10") || ip == null){
                            ip = inetAddress.getHostAddress();
                        }
                    }
                }
            }

            if(ip == null){
                throw new SocketException("Fail");
            }
            return ip;
        } catch (SocketException e) {
            throw new ProcessException("Network Error. Unable to generate Host IP",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }
}

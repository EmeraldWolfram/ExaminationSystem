package com.info.ghiny.examsystem.database;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.nfc.TagLostException;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.info.ghiny.examsystem.DistributionActivity;
import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.interfacer.DistributionMVP;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.AndroidClient;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user09 on 12/13/2016.
 */

public final class TasksSynchronizer extends Service{

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private static boolean running = false;
    private static boolean distributed = false;
    private static HashMap<Integer, AndroidClient> clientsMap = new HashMap<>();

    private static DistributionMVP.MvpView view;
    private static DistributionMVP.MvpModel model;

    private static AndroidClient waitingThread;

    public static boolean isRunning() {
        return running;
    }

    public static boolean isDistributed() {
        return distributed;
    }

    public static HashMap<Integer, AndroidClient> getClientsMap() {
        return clientsMap;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        powerManager    = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock        = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, DistributionActivity.TAG);
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
        waitingThread    = new AndroidClient();
        TasksSynchronizer.view  = view;
        TasksSynchronizer.model = model;

        waitingThread.setTempView(view);
        waitingThread.setTempModel(model);

        waitingThread.start();
    }

    public static void updateAttendance(ArrayList<Candidate> updatingList){
        String msgUpdate    = JsonHelper.formatAttendanceUpdate(updatingList);

        for(AndroidClient client : clientsMap.values()){
            client.sendMessage(msgUpdate);
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
}

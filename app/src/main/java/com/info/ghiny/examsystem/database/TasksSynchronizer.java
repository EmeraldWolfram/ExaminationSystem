package com.info.ghiny.examsystem.database;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.nfc.TagLostException;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.info.ghiny.examsystem.DistributionActivity;
import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.manager.IconManager;
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

/**
 * Created by user09 on 12/13/2016.
 */

public class TasksSynchronizer extends Service{

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private static boolean running = false;
    private static boolean distributed;


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


    public static boolean isRunning() {
        return running;
    }

    /**
     * Check Client Request <Live in Receiver Client Thread>
     *
     */

    public static boolean isDistributed() {
        return distributed;
    }

    //TODO: Use a loop and send to everyone connected
    public static void updateAttendance(ArrayList<Candidate> candidates) throws ProcessException {
        //if(tcpClient != null && candidates != null){
            String str = JsonHelper.formatAttendanceUpdate(candidates);
        //    tcpClient.sendMessage(str);
        //} else {
        throw new ProcessException("Fail to send out update!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        //}


        //candidates.clear();
    }

    void onReqCollection(String inStr){
        //ToDo: Pass the request
    }

    void onReqIdentification(String inStr){
        //ToDo: Pass the request
    }


    void onReqVenueInfo(){
        AttendanceList attdList                 = TakeAttdModel.getAttdList();
        HashMap<String, ExamSubject> subjects   = Candidate.getPaperList();

        String messageOut   = JsonHelper.formatVenueInfo(attdList, subjects);
        //ToDo: send the latest AttendanceList & PaperMap
    }

    void onAttendanceUpdateFromClients(ArrayList<Candidate> modifyList){
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
    }


    //=========================================@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    ServerSocket serverSocket;
    String message = "";
    Connector connector;

    public TasksSynchronizer() {
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(0);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    Socket socket = serverSocket.accept();

                    SocketServerReplyThread socketServerReplyThread =
                            new SocketServerReplyThread(socket);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
        }

    }



    //=========================================#####################################################


}

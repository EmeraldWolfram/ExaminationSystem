package com.info.ghiny.examsystem.model;


import android.util.Log;

import com.info.ghiny.examsystem.LinkChiefActivity;
import com.info.ghiny.examsystem.database.Connector;
import com.info.ghiny.examsystem.database.ThreadManager;
import com.info.ghiny.examsystem.interfacer.GeneralView;
import com.info.ghiny.examsystem.manager.IconManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by GhinY on 08/07/2016.
 */
public class JavaHost implements Runnable{
    private String serverMessage;
    /**
     * Specify the Server Ip Address here. Whereas our Socket Server is started.
     */
    private static Connector connector;
    private OnMessageReceived msgListener = null;
    private boolean running;
    private boolean sending;
    private Socket socket;
    private GeneralView taskView;

    private PrintWriter out = null;
    private BufferedReader in = null;

    private ReentrantLock mutex;
    private ArrayList<String> msgQueue;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public JavaHost(final OnMessageReceived listener) {
        msgListener = listener;
        mutex       = new ReentrantLock(true);
        msgQueue    = new ArrayList<>();
        running     = false;
        sending     = false;
    }

    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    public static void setConnector(Connector connector) {
        JavaHost.connector = connector;
    }

    public void setTaskView(GeneralView taskView) {
        this.taskView = taskView;
    }

    public static Connector getConnector() {
        return connector;
    }

    public void setMessageListener(OnMessageReceived messageListener) {
        this.msgListener = messageListener;
    }

    public void putMessageIntoSendQueue(String message) {
        mutex.lock();
        msgQueue.add(message);
        mutex.unlock();

        if(!sending){
            sending = true;
            Thread sendOutThread    = new Thread(){
                @Override
                public void run() {
                    while (msgQueue.size() > 0) {
                        try {
                            sendMessage(msgQueue.remove(0));
                            Thread.sleep(500);
                        } catch (final InterruptedException e) {
                            Log.d(LinkChiefActivity.TAG, e.getMessage());
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
    private void sendMessage(String message){
        if (out != null && !out.checkError()) {
            Log.d(LinkChiefActivity.TAG, "&&& Send to Host: " + message);
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        running = false;
    }

    @Override
    public void run(){
        running = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(connector.getIpAddress());
            SocketAddress address   = new InetSocketAddress(serverAddr, connector.getPortNumber());

            socket = new Socket(serverAddr, connector.getPortNumber());

            try {
                out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (running) {
                    serverMessage = in.readLine();
                    if(serverMessage != null){
                        Log.d(LinkChiefActivity.TAG, "&&& Receive from Host: " + serverMessage);
                        int deviceId    = JsonHelper.parseClientId(serverMessage);
                        if(deviceId != 0){
                            serverMessage   = JsonHelper.modifyDeviceId(serverMessage, 0);
                            ThreadManager.passMessageBack(deviceId, serverMessage);
                        } else if (msgListener != null) {
                            msgListener.messageReceived(serverMessage);
                        }
                    } else {
                        throw new ProcessException("Connection Lost!\n" +
                                "Please restart the app and connect again!",
                                ProcessException.FATAL_MESSAGE, IconManager.WARNING);
                    }

                    serverMessage = null;
                }
            } catch (SocketException err) {
                Log.d(LinkChiefActivity.TAG, err.getMessage());
            } catch (final ProcessException err) {
                taskView.runItSeparate(new Runnable() {
                    @Override
                    public void run() {
                        taskView.displayError(err);
                    }
                });
            } finally {
                socket.close();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }

}

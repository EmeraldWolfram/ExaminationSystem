package com.info.ghiny.examsystem.tools;



import android.app.ActivityManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by GhinY on 08/07/2016.
 */
public class TCPClient implements Runnable{
    private String serverMessage;
    /**
     * Specify the Server Ip Address here. Whereas our Socket Server is started.
     * */
    public static String SERVERIP = "192.168.0.112"; // your computer IP address
    public static int SERVERPORT = 5657;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    private PrintWriter out = null;
    private BufferedReader in = null;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public static void setServerIp(String ipAddress){
        TCPClient.SERVERIP = ipAddress;
    }
    public static void setServerPort(int portNumber){
        TCPClient.SERVERPORT = portNumber;
    }

    public void setmMessageListener(OnMessageReceived mMessageListener) {
        this.mMessageListener = mMessageListener;
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
        mRun = false;
    }

    public void run(){
        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
            try {
                //out = sender
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);

                //in = receiver
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(serverMessage);
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

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}

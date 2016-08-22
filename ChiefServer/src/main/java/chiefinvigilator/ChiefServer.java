/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Krissy
 */
public class ChiefServer {
    ServerSocket s ;
    int localPort;
    
    public ChiefServer(){
    }
    
    public void setPort() throws IOException{
        
        this.s = new ServerSocket(0);
        this.s.setSoTimeout(5000);
        this.localPort = s.getLocalPort();
    }
    
    public ServerSocket getServerSocket(){
        return s;
    }
    
    public int getPort(){
        return this.localPort;
    }
    
    public void boardCast() throws Exception{
        s.setSoTimeout(10000);
        Socket server = s.accept();
            System.out.println("Just connected to "
                  + server.getRemoteSocketAddress());
            DataInputStream in =
                  new DataInputStream(server.getInputStream());
            System.out.println(in.readUTF());
            DataOutputStream out =
                 new DataOutputStream(server.getOutputStream());
            out.writeUTF("Thank you for connecting to "
              + server.getLocalSocketAddress() + "\nGoodbye!");
            server.close();
        s.setSoTimeout(10);
    }
    
}

    


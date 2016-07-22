/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgen;

import chiefinvigilator.ChiefServer;
import chiefinvigilator.MainServer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Krissy
 */
public class Screen extends JPanel {
        
    ServerSocket socket;
    
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    String myWeb = "";
    int width = 300;
    int height = 300;
         
    BufferedImage bufferedImage = null;

         
    public Screen(){
    };
    
    public Screen(ServerSocket socket){
        
    this.socket = socket;
            try {
                this.myWeb = "$CHIEF:"+localIp(socket.getLocalPort())+":$";
            } catch (Exception ex) {
                Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();
        
    }
    
    public void regenerateQR(ServerSocket socket){
        removeAll();
        this.socket = socket;
        try {
                this.myWeb = "$CHIEF:"+localIp(socket.getLocalPort())+":$";
            } catch (Exception ex) {
                Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
            }
        updateUI();
//        repaint();
    }
    
    public String localIp(int port) throws Exception {
      InetAddress addr = InetAddress.getLocalHost();
      System.out.println("Local HostAddress:"+addr.getHostAddress());
      String hostname = addr.getHostName();
      System.out.println("Local host name: "+hostname);

      System.out.println("listening on port: " + port);
      return addr.getHostAddress()+":"+port;
   }   
    
    public void paintComponent (Graphics graphics){
        super.paintComponent(graphics);
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.AZTEC_LAYERS, 10);
        hints.put("Version", "10");  
        
            try {

                BitMatrix byteMatrix = qrCodeWriter.encode(myWeb, BarcodeFormat.QR_CODE, width, height,hints);
                for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
                
            }
            } catch (WriterException ex) {
                Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
            }
            SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                MainServer mainServer = new MainServer();
                try {
                    System.out.print(socket.getLocalPort());
                    mainServer.boardCast(socket);
                } catch (Exception ex) {
                    Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
    });
    }
    
}



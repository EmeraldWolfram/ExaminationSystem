/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgen;

import chiefinvigilator.ChiefServer;
import chiefinvigilator.ClientComm;
import chiefinvigilator.ServerComm;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Krissy
 */
public class QRgen extends JPanel {
        
    ServerSocket socket;
    ServerComm serverComm;
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    String qrCode = "";
    int width = 400;
    int height = 400;
    
    String randomString;
         
    BufferedImage bufferedImage = null;

         
    public QRgen(){
    };
    
    public QRgen(ServerSocket socket, ServerComm serverComm, String randomString){
        JLabel qrLabel = new JLabel("Scan the QR Code to sign in");
        qrLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        this.add(qrLabel, BorderLayout.NORTH);
        this.serverComm = serverComm;
        this.socket = socket;
        this.randomString = randomString;
        
            try {
                
                this.qrCode = localIp(socket.getLocalPort())+":"+randomString+":$";
            } catch (Exception ex) {
                Logger.getLogger(QRgen.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();
        
    }
    
    /**
     * To regenerate the QR code with different random message
     * @param socket 
     */
    public void regenerateQR(ServerSocket socket, ServerComm serverComm, String randomString) throws Exception{
        this.serverComm = serverComm;
        this.socket = socket;
        this.randomString = randomString;
        
            try {
                
//                this.qrCode = "$CHIEF:"+localIp(socket.getLocalPort())+":"+randomString+":$";
                this.qrCode = localIp(socket.getLocalPort())+":"+randomString+":$";
            } catch (Exception ex) {
                Logger.getLogger(QRgen.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();

//        this.randomString = randomString;
//        this.socket = socket;
//        this.qrCode = "$CHIEF:"+localIp(socket.getLocalPort())+":"+randomString+":$";
//        updateUI();
    }
    
    
    
    public String localIp(int port) throws Exception {
      InetAddress addr = InetAddress.getLocalHost();
//      System.out.println("Local HostAddress:"+addr.getHostAddress());
//      System.out.println("Local HostAddress:"+addr.getCanonicalHostName());
      String hostname = addr.getHostName();
      System.out.println("Local host name: "+hostname);
      System.out.println("Local host ip: "+addr.getHostAddress());
      System.out.println("listening on port: " + port);
      return "$CHIEF:"+addr.getHostAddress()+":"+port;
   }   
    
    public void paintComponent (Graphics graphics){
        super.paintComponent(graphics);
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.AZTEC_LAYERS, 10);
        hints.put("Version", "10");
        
        if(!this.qrCode.equals("")){
            try {

                BitMatrix byteMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, width, height,hints);
                for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
                
            }
            } catch (WriterException ex) {
                Logger.getLogger(QRgen.class.getName()).log(Level.SEVERE, null, ex);
            }
//            SwingUtilities.invokeLater(new Runnable(){
//            @Override
//            public void run(){
//
////                try 
//////                  (new ClientComm(socket, serverComm)).start();
////
//////                    System.out.print(socket.getLocalPort());
////                } catch (Exception ex) {
////                    System.out.println("Error: Create new ClientComm failed. ");
////                }
//                
//            }
//    });
        }
    }
    
    
    
}



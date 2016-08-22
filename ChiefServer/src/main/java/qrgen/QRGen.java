/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import javax.swing.JFrame;

/**
 *
 * @author Krissy
 */
public class QRGen extends JFrame{
//    Screen s;
    ServerSocket socket;
    
    public QRGen(ServerSocket socket){
        setTitle("Scan to Login in");
        setSize(450,350);
        this.socket = socket;
        init();
        setVisible(true);
        
    }
    public void init(){
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1,1,0,0));
//        Screen s = new Screen(socket);
//        add(s);

    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import qrgen.QRgen;

/**
 *
 * @author Krissy
 */
public class ChiefControl {
    ChiefGui chiefGui;
    ChiefModel chiefModel;
    ServerComm serverComm;
    ChiefServer chief;
    QRgen qrgen;
    
    HashMap invMap = new HashMap();
    Integer invNum;
//    ChiefControl(){}
    
    ChiefControl(ChiefGui chiefGui) throws Exception{
        this.invNum = 0;
        this.chiefGui = chiefGui;
        this.chiefGui.setVisible(true);
//        this.chiefModel = chiefModel;
        serverComm = new ServerComm();
        chief = new ChiefServer();
        
        chiefGui.addSignInButtonListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    chiefSignIn(chiefGui.getIdField(),chiefGui.getPsField(),chiefGui.getBlockField());
                } catch (Exception ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        chiefGui.addChiefTabbedListener(new ChangeListener(){
            public void stateChanged(ChangeEvent evt) {
                try {
                    activateQRTab(chiefGui.getTabbedNumber());
                } catch (IOException ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        chiefGui.addCandSearchListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e){
                candSearch();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
    /**
     * @brief   To sign in
     * @param id
     * @param password
     * @param block
     * @throws Exception 
     */
    public void chiefSignIn(String id, String password, String block) throws Exception{
        serverComm.loginToServer(id,password,block);
    }
    
    /**
     * @brief   To activate the Tab which generate the QR code
     * @param tabNum
     * @throws IOException 
     */
    public void activateQRTab(Integer tabNum) throws IOException{
        
        if(tabNum == 1){
            ServerSocket socket = new ServerSocket();
            
            String randomString = this.generateRandomString();
            chief.setPort();
            socket = this.chief.getServerSocket();
            System.out.println(randomString);
            generateQRInterface(socket, randomString); ///need to remove this
            System.out.println("new clientComm created");
            (new ClientComm(socket, this.serverComm, this.invMap, this.chief, this.qrgen)).start();
//            timer.start();
        }
//        else
//            timer.stop();
    }
    
    public void generateQRInterface(ServerSocket socket, String randomString){
        this.qrgen = new QRgen();
        this.qrgen.setPreferredSize(new Dimension(500,500));
      
        chiefGui.addQRPanel(qrgen);
    }
    

    public void candSearch(){
        InfoData data = new InfoData();
        
        data.setStatus(chiefGui.getStatusComboBox());
        data.setAttendance(chiefGui.getAttendanceComboBox());
        data.setRegNum(chiefGui.getRegNumCandidiate());
        data.setTableNum(chiefGui.getTableNumber());
        data.setVenueName(chiefGui.getVenueComboBox());
        
        ArrayList<InfoData> cddList = new ArrayList<>();
        System.out.print(chiefGui.getAttendanceComboBox());
        try {
            cddList = data.getDataFromTable();
        } catch (CustomException ex) {
            System.out.println(ex.getMessage());
        }
        
        chiefGui.setCandidateTableModelRow(0);
        
        for(int i = 0; i<cddList.size(); i++){
                chiefGui.addCandidateTableModelRow(new Object[]{cddList.get(i).getVenueName(), cddList.get(i).getRegNum(), cddList.get(i).getStatus(), cddList.get(i).getAttendance(), cddList.get(i).getTableNum()});
            }
        
        try {
            chiefGui.setSummaryPanel(data.getCountTotalCdd(), data.getCountAttdCdd("PRESENT"),
                                        data.getCountAttdCdd("ABSENT"));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    protected String generateRandomString() {
        String seed = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+Long.toString(System.nanoTime());
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < 18) {
            int index = (int) (rnd.nextFloat() * seed.length());
            str.append(seed.charAt(index));
        }
        String saltStr = str.toString();
        return saltStr;
    }

}

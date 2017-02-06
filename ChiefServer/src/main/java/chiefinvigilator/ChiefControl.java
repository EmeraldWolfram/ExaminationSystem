/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import globalvariable.CheckInType;
import globalvariable.InfoType;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import qrgen.QRgen;
import serverquerylist.ExamDataList;

/**
 *
 * @author Krissy
 */
public class ChiefControl {
    private final static String ELIGIBLE = "ELIGIBLE";
    private final static String BARRED = "BARRED";
    private final static String EXEMPTED = "EXEMPTED";
    
    
    ImageIcon connectedIcon;
    ImageIcon disconnectedIcon;

    
    ChiefGui chiefGui;
    ServerComm serverComm;
    ClientComm currentClientComm;
    ChiefServer chiefServer;
    QRgen qrgen;
    
    String mainServerHostName = "localhost";
    Integer mainServerPortNum = 5006;
    
    
    Timer timer = new Timer(5000, new ChiefControl.TimerActionListener());
    
    HashMap invMap = new HashMap();
    Integer invNum;
    private String chiefId;
    private String chiefPs;
    private String chiefBlock;
    
    ChiefControl(ChiefGui chiefGui) throws Exception{
        
        this.invNum = 0;
        this.chiefGui = chiefGui;
        this.chiefGui.setVisible(true);
        this.serverComm = new ServerComm(this);
        this.chiefServer = new ChiefServer();
        
        generateQRInterface();
        
        //initialize icon image
        this.connectedIcon = new ImageIcon(new ImageIcon("icons\\connected.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        this.disconnectedIcon = new ImageIcon(new ImageIcon("icons\\disconnected.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        
        chiefGui.setConnectButtonIcon(disconnectedIcon);
        
        chiefGui.addConnectListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    
                    //ChiefControl.this.chiefGui.popUpChiefSignInJOptionPane();
                    ChiefControl.this.serverComm.connectToServer(ChiefControl.this.mainServerHostName, ChiefControl.this.mainServerPortNum);
                   
                    popUpChiefSignInJOptionPane();
//                    ChiefControl.this.id = chiefGui.getChiefId();
//                    ChiefControl.this.block = chiefGui.getChiefBlock();
                     
                    ChiefControl.this.displayConnectivity("Connected");
                    chiefGui.setConnectButtonIcon(connectedIcon);
                } catch (Exception ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                    ChiefControl.this.displayConnectivity("Not connected");
                    chiefGui.setConnectButtonIcon(disconnectedIcon);
                }
                
                if(ChiefControl.this.serverIsConnected()){
                    ChiefControl.this.displayConnectivity("Connected");
                    chiefGui.setConnectButtonIcon(connectedIcon);
                    if(!ChiefControl.this.serverComm.isAlive())
                    ChiefControl.this.serverComm.start();
                }
                else{
                    ChiefControl.this.displayConnectivity("Not connected");
                    chiefGui.setConnectButtonIcon(disconnectedIcon);
                }
            }
        });
        
        chiefGui.addSignInButtonListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                try {
                    
                    if(ChiefControl.this.serverIsConnected()){
                        String id = ChiefControl.this.chiefGui.getIdField();
                        String ps = ChiefControl.this.chiefGui.getPsField();
                       
                        if(ChiefControl.this.serverComm.invIsAssigned(id))
                            ChiefControl.this.serverComm.signInToServer(id, ps, "", CheckInType.STAFF_LOGIN_FROM_CHIEF_SERVER);
                        else
                            ChiefControl.this.chiefGui.popUpErrorPane("Wrong ID/PASSWORD");
                    }
                    else
                        ChiefControl.this.chiefGui.popUpErrorPane("Server is not connected");
                        
                } catch (Exception ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        chiefGui.addSubmitActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                try {
                    if(ChiefControl.this.serverIsConnected()){
                        ChiefControl.this.displayConnectivity("Connected");
                        chiefGui.setConnectButtonIcon(connectedIcon);
                        ChiefControl.this.serverComm.submitDB(ChiefControl.this.getChiefId(), ChiefControl.this.getChiefBlock());
                    
                    }
                    else{
                        ChiefControl.this.displayConnectivity("Not connected");
                        chiefGui.setConnectButtonIcon(disconnectedIcon);
                }
                        
                } catch (Exception ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        chiefGui.addDownloadListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                try {
                    if(ChiefControl.this.serverIsConnected()){
                        ChiefControl.this.displayConnectivity("Connected");
                        chiefGui.setConnectButtonIcon(connectedIcon);
                        ChiefControl.this.serverComm.downloadDB(ChiefControl.this.getChiefId(), ChiefControl.this.getChiefBlock());
                    
                    }
                    else{
                        ChiefControl.this.displayConnectivity("Not connected");
                        chiefGui.setConnectButtonIcon(disconnectedIcon);
                }
                        
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
        
        chiefGui.addSearchButton4(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                collectorSearch();
            }
            
        });
        
        chiefGui.getVenueComboBox().addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                InfoData data = new InfoData();
                try {
                    ChiefControl.this.chiefGui.setSummaryPanel(data.getCountTotalCdd((String)chiefGui.getVenueComboBox().getSelectedItem()), data.getCountAttdCdd("PRESENT",(String)chiefGui.getVenueComboBox().getSelectedItem()),
                            data.getCountAttdCdd("ABSENT",(String)chiefGui.getVenueComboBox().getSelectedItem()));
                } catch (SQLException ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        
    }
    
    public boolean serverIsConnected(){
        return ChiefControl.this.serverComm.isSocketAliveUitlitybyCrunchify(ChiefControl.this.mainServerHostName, ChiefControl.this.mainServerPortNum);
    }
    
    /**
     * @brief   To record staff that sign in using the chief server
     */
    public void staffSignInRecord(Staff staff){
        chiefGui.addStaffToStaffInfoTable(staff);
    }
    
    public void addInvSignOutActionListener(ActionListener al){
        chiefGui.addInvSignOutActionListener(al);
    }
    
    /**
     * @brief   To sign in
     * @param id
     * @param password
     * @param block
     * @throws Exception 
     */
    public void chiefSignIn(String id, String password, String block) throws Exception{
        serverComm.signInToServer(id,password,block,CheckInType.CHIEF_LOGIN);
    }
    
    public void displayConnectivity(String status){
        this.chiefGui.setStatusForConnectivityTextField(status);
    }
    
    /**
     * @brief   To activate the Tab which generate the QR code
     * @param tabNum
     * @throws IOException 
     */
    public void activateQRTab(Integer tabNum) throws IOException{

        if(tabNum == 1){
            try {
                createNewClientComm();
                timer.start();
            } catch (Exception ex) {
                Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            timer.stop();
        }
    }
    
    
    public void generateQRInterface(){
        this.qrgen = new QRgen();
        this.qrgen.setPreferredSize(new Dimension(500,500));
      
        chiefGui.addQRPanel(qrgen);
    }
    
    public void createNewClientComm() throws IOException, Exception{
        ServerSocket serverSocket = new ServerSocket();
            
        String randomString = this.generateRandomString();
        chiefServer.setPort();
        serverSocket = this.chiefServer.getServerSocket();
        System.out.println(randomString);
        System.out.println("new clientComm created from address" + serverSocket);
        this.currentClientComm = new ClientComm(serverSocket, this.serverComm, this.chiefServer, this.qrgen, this);
        (this.currentClientComm).start();
        regenerateCurrentQRInterface();
        
    }
    
    public void regenerateCurrentQRInterface(){
        try {
            this.currentClientComm.requestForRandomMessage();
        } catch (Exception ex) {
            Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void candSearch(){
        InfoData data = new InfoData();
        
        data.setStatus(chiefGui.getStatusComboBox());
        data.setAttendance(chiefGui.getAttendanceComboBox());
        data.setRegNum(chiefGui.getRegNumCandidiate());
        data.setTableNum(chiefGui.getTableNumber());
        data.setVenueName((String)chiefGui.getVenueComboBox().getSelectedItem());
        
        ArrayList<InfoData> cddList = new ArrayList<>();
//        System.out.print(chiefGui.getAttendanceComboBox());
        try {
            cddList = data.getDataFromTable();
        } catch (CustomException ex) {
            System.out.println(ex.getMessage());
        }
        
        chiefGui.setCandidateTableModelRow(0);
        
        for(int i = 0; i<cddList.size(); i++){
            
                if (cddList.get(i).getStatus().equals(ELIGIBLE)){
                    chiefGui.addCandidateTableModelRow(new Object[]{cddList.get(i).getVenueName(), cddList.get(i).getRegNum(), cddList.get(i).getAttendance(), cddList.get(i).getTableNum()});

                }
                else if (cddList.get(i).getStatus().equals(EXEMPTED)){
                    chiefGui.addCandidateTableModelRow(new Object[]{cddList.get(i).getVenueName(), cddList.get(i).getRegNum(), cddList.get(i).getStatus(), cddList.get(i).getTableNum()});

                }
                else if (cddList.get(i).getStatus().equals(BARRED)){
                    chiefGui.addCandidateTableModelRow(new Object[]{cddList.get(i).getVenueName(), cddList.get(i).getRegNum(), cddList.get(i).getStatus(), cddList.get(i).getTableNum()});
 
                }
            }
        
                try {
                    ChiefControl.this.chiefGui.setSummaryPanel(data.getCountTotalCdd((String)chiefGui.getVenueComboBox().getSelectedItem()), data.getCountAttdCdd("PRESENT",(String)chiefGui.getVenueComboBox().getSelectedItem()),
                            data.getCountAttdCdd("ABSENT",(String)chiefGui.getVenueComboBox().getSelectedItem()));
                } catch (SQLException ex) {
                    Logger.getLogger(ChiefControl.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void collectorSearch(){
        InfoData data = new InfoData();
        
        data.setBundleId(chiefGui.getBundleIdField4().getText());
        data.setPaperCode(chiefGui.getPaperCodeField4().getText());
        data.setVenueName((String) chiefGui.getVenueComboBox().getSelectedItem());
        data.setProgName((String) chiefGui.getProgrammeBox4().getSelectedItem());
        data.setCollector(chiefGui.getStaffIdField4().getText());
        
        ArrayList<InfoData> collectorList = new ArrayList<>();
//        System.out.print(chiefGui.getAttendanceComboBox());
        try {
            collectorList = data.getCollectorList();
        } catch (CustomException ex) {
            System.out.println(ex.getMessage());
        }
        
        chiefGui.setCollectorTableModelRow(0);
        
        for(int i = 0; i<collectorList.size(); i++){
            
            chiefGui.addCollectorTable4ModelRow(new Object[]{collectorList.get(i).getBundleId(),
                                                            collectorList.get(i).getProgName(),
                                                            collectorList.get(i).getPaperCode(),
                                                            collectorList.get(i).getVenueName(),
                                                            collectorList.get(i).getCollector()
                                                            });
        }
        
    }
    
    public void addStaffInfoToGuiTable(Staff staff){
        chiefGui.addStaffToStaffInfoTable(staff);
    }
    
    public void updateGuiLoggedChief(){
        chiefGui.setLoggedChief("Staff: " + this.getChiefId());
        chiefGui.setLoggedBlock("Block: " + this.getChiefBlock());
    }
    
    public void setGuiPanelEnable(Boolean bool){
        chiefGui.setFrontPanelEnable(bool);
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
    
    public void popUpChiefSignInJOptionPane() throws Exception{
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField chiefIdField = new JTextField();
        JPasswordField chiefPsField = new JPasswordField();
        JTextField chiefBlockField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField sessionField = new JTextField();
        
        panel.add(new JLabel("Chief ID: "));
        panel.add(chiefIdField);
        panel.add(new JLabel("Password: "));
        panel.add(chiefPsField);
//        panel.add(new JLabel("Block: "));
//        panel.add(chiefBlockField);
//        panel.add(new JLabel("Date: "));
//        panel.add(dateField);
//        panel.add(new JLabel("Session: "));
//        panel.add(sessionField);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Chief Sign In",
            JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            this.chiefId = chiefIdField.getText();
            this.chiefPs = String.valueOf(chiefPsField.getPassword());
//            this.chiefBlock = chiefBlockField.getText();
            this.chiefBlock = "M";
//            String date = dateField.getText();
//            String session = sessionField.getText();
            chiefSignIn(ChiefControl.this.getChiefId(), this.chiefPs, this.getChiefBlock());
        }
    }

    /**
     * @return the chiefId
     */
    public String getChiefId() {
        return chiefId;
    }

    /**
     * @return the chiefBlock
     */
    public String getChiefBlock() {
        return chiefBlock;
    }

    class TimerActionListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
          try {
              regenerateCurrentQRInterface();
          } catch (Exception ex) {
              System.out.println("Error TimerActionListener->actionPerformed");
          }
            
      }
    }
    
    
    
}

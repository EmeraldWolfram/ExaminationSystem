/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import jsonconvert.JsonConvert;
import static jsonconvert.JsonConvert.jsonToAttdList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import querylist.AttdList;
import querylist.CddPapers;

/**
 *
 * @author Krissy
 */
public class ClientComm {
    boolean signIn = false;
    static ArrayList<Staff> staffList = new ArrayList<>();
    ServerSocket sSocket;
    
    public ClientComm(ServerSocket sSocket){
        this.sSocket= sSocket; 
    }
    
    /***
     * @brief broadcast the signal to receive incoming message and response
     * @throws Exception 
     */
    public void boardCast() throws Exception{
        (new Thread() {
            @Override
            public void run()
            {
                Staff staff = new Staff();
                JSONObject jsonMsg = new JSONObject();
                String retreiveMsg = null;
                
                try {
                    Socket mainSocket = sSocket.accept();
                    ChiefGui.regenerateQRInterface();
                    System.out.println("\nJust connected to "+ mainSocket.getRemoteSocketAddress());
                    
                    do{

                    retreiveMsg  = receiveMessage(mainSocket);
                    staff.setIdPsFromJsonString(retreiveMsg);
                    System.out.println(retreiveMsg);
                    
                    signIn = staff.staffVerify();

                    if(signIn){
                        jsonMsg = staffRequestSend(staff);
                        ChiefGui.addStaffInfoToRow(staff);
                    }
                    else
                        jsonMsg = JsonConvert.booleanToJson(false);

                    if(retreiveMsg != null){
//                        System.out.println("Message sent: " + jsonMsg.toString());
//                        PrintStream out = new PrintStream(mainSocket.getOutputStream());
//                        out.println(jsonMsg.toString());
//                        out.flush();
                        sendMessage(mainSocket,jsonMsg.toString());
                        
                    }
                    }while(signIn != true);
                    
                    
                    while(signIn != false){
                        System.out.println("Ready for incoming message");
                        retreiveMsg  = receiveMessage(mainSocket);
                        System.out.println("Message received");
                        System.out.println(retreiveMsg);
                        sendMessage(mainSocket,checkIn(new JSONObject(retreiveMsg)));
                    }
                    System.out.println("Signed Out");
                        mainSocket.close();

                        System.out.println("end!!");
                    
                } catch (SocketTimeoutException  ex) {
                    System.out.println("\nSocket timed out!");
                } catch (IOException ex) {
                    System.out.print(ex.getMessage());
                } catch (Exception ex) {
                    System.out.print(ex.getMessage());
                }
              }
        }).start();
        
    }
    
    private JSONObject staffRequestSend(Staff staff) throws Exception{
        staff = ServerComm.staffGetInfo(staff.id);
//        if((staffList.isEmpty())&&(Objects.equals(staff.getStatus(),"chief")))
//            throw new Exception("Chief should sign in first");
//        else
//            staffList.add(staff);

        JSONObject staffInfo = JsonConvert.staffInfoToJson(signIn, staff);
        JSONArray papers = JsonConvert.papersToJson(ServerComm.getPapers(staff.getVenue()));
        JSONArray attdList = JsonConvert.attdListToJson(ServerComm.getAttdList(staff.getVenue()));
        return JsonConvert.jsonStringConcatenate(staffInfo, papers, attdList);
    }
    
    private String receiveMessage(Socket socket) throws IOException{
        System.out.println(socket.getLocalPort());
        InputStreamReader ir = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(ir);

        return br.readLine();
    }
    
    private void sendMessage(Socket socket, String message) throws IOException{
        System.out.println("Message sent: " + message);
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.println(message);
        out.flush();
    }
    
    public String checkIn(JSONObject json) throws JSONException, SQLException{ 
        JSONObject jsonObject = new JSONObject();
            System.out.println(json.toString());
            String checkIn = json.getString("CheckIn");
            
            switch(checkIn){
                case "Collection" :
                    return null;
                    
                case "CddPapers" : 
                    ArrayList<CddPapers> cddPapers = ServerComm.getCddPapers(json.getString("Value"));
                    JSONArray jArr = JsonConvert.cddPapersToJson(cddPapers);
                    if(jArr.length() != 0){
                        jsonObject.put("Result", true);
                        jsonObject.put("PaperList", jArr);
                    }
                    else
                        jsonObject.put("Result", false);
                    return null;
                    
                case "AttdList" : 
                    ArrayList<AttdList> attdList = new ArrayList();
                    attdList = jsonToAttdList(json.getJSONArray("CddList").toString());
                    ServerComm.updateCandidateAttendence(attdList);
                    return JsonConvert.booleanToJson(false).toString();
                    
                default: return null;
            }
    
        
    }
    
    
    
}

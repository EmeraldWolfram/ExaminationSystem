/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MainServer {

    static ArrayList<Staff> staffList = new ArrayList<>();
    
    public void boardCast(ServerSocket sSocket) throws Exception{
        (new Thread() {
            @Override
            public void run()
            {
                Staff staff = new Staff();
                JSONObject jsonMsg = new JSONObject();
                boolean verify = false;
                
                try {
                    Socket mainSocket = sSocket.accept();
                    ChiefGui.regenerateQRInterface();
                    System.out.println("\nJust connected to "+ mainSocket.getRemoteSocketAddress());
                    
                    do{

                    String reteriveMsg  = getMessage(mainSocket);

                    staff = JsonConvert.jsonToSignIn(reteriveMsg);
                    System.out.println(reteriveMsg);
                    verify = ServerComm.staffVerify(staff.getID(),staff.getPassword());


                    if(verify){
                        jsonMsg = staffRequestSend(staff,verify);
                        ChiefGui.addStaffInfoToRow(staff);
                    }
                    else
                        jsonMsg = JsonConvert.booleanToJson(false);

                    if(reteriveMsg != null){
                        System.out.println("Message sent: " + jsonMsg.toString());
                        PrintStream out = new PrintStream(mainSocket.getOutputStream());
                        out.println(jsonMsg.toString());
                        out.flush();
                    
//                        OutputStream outToServer = mainSocket.getOutputStream();
//                        DataOutputStream asd = new DataOutputStream(outToServer);
//                        asd.writeUTF(jsonMsg.toString());
                    }
                    }while(verify != true);
                    
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
    
    private JSONObject staffRequestSend(Staff staff, boolean verify) throws Exception{
        staff = ServerComm.staffGetInfo(staff.id);
//        if((staffList.isEmpty())&&(Objects.equals(staff.getStatus(),"chief")))
//            throw new Exception("Chief should sign in first");
//        else
//            staffList.add(staff);

        JSONObject staffInfo = JsonConvert.staffInfoToJson(verify, staff);
        JSONArray papers = JsonConvert.papersToJson(ServerComm.getPapers(staff.getVenue()));
        JSONArray attdList = JsonConvert.attdListToJson(ServerComm.getAttdList(staff.getVenue()));
        return JsonConvert.jsonStringConcatenate(staffInfo, papers, attdList);
    }
    
    private String getMessage(Socket socket) throws IOException{
        
        InputStreamReader ir = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(ir);

        return br.readLine();
    }
    
    public JSONObject checkIn(JSONObject json) throws JSONException, SQLException{ 
        JSONObject jsonObject = new JSONObject();
        
   
            String checkIn = json.getString("CheckIn");
            
            switch(checkIn){
                case "Collection" :
                    break;
                    
                case "CddPapers" : 
                    ArrayList<CddPapers> cddPapers = ServerComm.getCddPapers(json.getString("Value"));
                    JSONArray jArr = JsonConvert.cddPapersToJson(cddPapers);
                    if(jArr.length() != 0){
                        jsonObject.put("Result", true);
                        jsonObject.put("PaperList", jArr);
                    }
                    else
                        jsonObject.put("Result", false);
                    break;
                    
                case "AttdList" : 
                    ArrayList<AttdList> attdList = new ArrayList();
                    attdList = jsonToAttdList(json.getJSONArray("CddList").toString());
                    ServerComm.updateCandidateAttendence(attdList);
                    break;
            }
    
        
        return json;
    }
    
}

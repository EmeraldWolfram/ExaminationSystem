package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.TCPClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 07/07/2016.
 */
public class ExternalDbLoader {

    private static TCPClient tcpClient;
    private static boolean msgReadyFlag;
    private static String msgReceived;

    public static void setTcpClient(TCPClient tcpClient) {
        ExternalDbLoader.tcpClient = tcpClient;
    }

    public static void setMsgReadyFlag(boolean msgReadyFlag) {
        ExternalDbLoader.msgReadyFlag = msgReadyFlag;
    }

    public static void setMsgReceived(String msgReceived) {
        ExternalDbLoader.msgReceived = msgReceived;
    }

    public static StaffIdentity getStaffIdentity(String scanIdNumber){
        StaffIdentity id;
        String str = JsonHelper.formatString(JsonHelper.TYPE_IDENTITY, scanIdNumber);

        tcpClient.sendMessage(str);
        while(!msgReadyFlag)
            id = null;

        id = JsonHelper.parseStaffIdentity(msgReceived);
        msgReceived     = null;
        msgReadyFlag    = false;
        LoginHelper.setStaff(id);

        return id;
    }

    public static AttendanceList dlAttdList(){
        AttendanceList attdList;
        String str = JsonHelper.formatString(JsonHelper.TYPE_VENUE,
                LoginHelper.getStaff().getVenueHandling());

        tcpClient.sendMessage(str);
        while(!msgReadyFlag)
            attdList = null;

        attdList = JsonHelper.parseAttdList(msgReceived);
        msgReceived     = null;
        msgReadyFlag    = false;

        return attdList;
    }

    public static HashMap<String, ExamSubject> dlPaperList(){
        HashMap<String, ExamSubject> map;

        String str = JsonHelper.formatString(JsonHelper.TYPE_VENUE,
                LoginHelper.getStaff().getVenueHandling());

        tcpClient.sendMessage(str);
        while(!msgReadyFlag)
            map = null;

        map = JsonHelper.parsePaperMap(msgReceived);
        msgReceived     = null;
        msgReadyFlag    = false;

        return map;
    }

    public static List<ExamSubject> getPapersExamineByCdd(String scanRegNum){
        List<ExamSubject> subjects;

        String str = JsonHelper.formatString(JsonHelper.TYPE_STUDENT, scanRegNum);

        tcpClient.sendMessage(str);    //suppose to send JSON
        while(!msgReadyFlag)
            subjects = null;

        subjects        = JsonHelper.parsePaperList(msgReceived);
        msgReceived     = null;
        msgReadyFlag    = false;
        //return null if wasn't a candidate
        return subjects;
    }

    public static void updateAttdList(AttendanceList attdList){
        String str = JsonHelper.formatAttdList(attdList);

        tcpClient.sendMessage(str);    //suppose to send JSON
    }

    public static void acknowledgeCollection(String scanBundleCode){
        String str = JsonHelper.formatCollection(scanBundleCode);

        tcpClient.sendMessage(str);
    }


}

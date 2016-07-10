package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.TCPClient;

import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 07/07/2016.
 */
public class ExternalDbLoader {

    private static TCPClient tcpClient  = null;

    public static void setTcpClient(TCPClient tcpClient) {
        ExternalDbLoader.tcpClient = tcpClient;
    }

    public static StaffIdentity getStaffIdentity(String scanIdNumber){
        StaffIdentity id = null;
        String str = JsonHelper.formatString(JsonHelper.TYPE_Q_IDENTITY, scanIdNumber);

        if(str != null){
            tcpClient.sendMessage(str);
            while(!ChiefLink.isMsgReadyFlag())
                id = null;
            id = JsonHelper.parseStaffIdentity(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
            LoginHelper.setStaff(id);
        }

        return id;
    }

    public static boolean matchPassword(String staffId, String staffPw){
        boolean isCorrect = false;

        String str = JsonHelper.formatPassword(staffId, staffPw);

        if(str != null){
            tcpClient.sendMessage(str);
            while(!ChiefLink.isMsgReadyFlag())
                isCorrect = false;
            isCorrect = JsonHelper.parseBoolean(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReadyFlag(false);
            ChiefLink.setMsgReceived(null);
        }

        return isCorrect;
    }

    public static AttendanceList dlAttdList(){
        AttendanceList attdList;
        String str = JsonHelper.formatString(JsonHelper.TYPE_Q_ATTD_VENUE,
                LoginHelper.getStaff().getVenueHandling());

        tcpClient.sendMessage(str);
        while(!ChiefLink.isMsgReadyFlag())
            attdList = null;

        attdList = JsonHelper.parseAttdList(ChiefLink.getMsgReceived());
        ChiefLink.setMsgReceived(null);
        ChiefLink.setMsgReadyFlag(false);

        return attdList;
    }

    public static HashMap<String, ExamSubject> dlPaperList(){
        HashMap<String, ExamSubject> map;

        String str = JsonHelper.formatString(JsonHelper.TYPE_Q_PAPERS_VENUE,
                LoginHelper.getStaff().getVenueHandling());

        tcpClient.sendMessage(str);
        while(!ChiefLink.isMsgReadyFlag())
            map = null;

        map = JsonHelper.parsePaperMap(ChiefLink.getMsgReceived());
        ChiefLink.setMsgReceived(null);
        ChiefLink.setMsgReadyFlag(false);

        return map;
    }

    public static List<ExamSubject> getPapersExamineByCdd(String scanRegNum){
        List<ExamSubject> subjects;

        String str = JsonHelper.formatString(JsonHelper.TYPE_Q_PAPERS_CDD, scanRegNum);

        tcpClient.sendMessage(str);    //suppose to send JSON
        while(!ChiefLink.isMsgReadyFlag())
            subjects = null;

        subjects        = JsonHelper.parsePaperList(ChiefLink.getMsgReceived());
        ChiefLink.setMsgReceived(null);
        ChiefLink.setMsgReadyFlag(false);
        //return null if wasn't a candidate
        return subjects;
    }

    public static boolean updateAttdList(AttendanceList attdList){
        String str = JsonHelper.formatAttdList(attdList);

        tcpClient.sendMessage(str);    //suppose to send JSON
        boolean received = false;
        while(!ChiefLink.isMsgReadyFlag())
            received = false;
        ChiefLink.setMsgReceived(null);
        ChiefLink.setMsgReadyFlag(false);
        received    = JsonHelper.parseBoolean(ChiefLink.getMsgReceived());

        return received;
    }

    public static boolean acknowledgeCollection(String scanBundleCode){
        String str = JsonHelper.formatCollection(scanBundleCode);

        tcpClient.sendMessage(str);
        boolean updated = false;
        while(!ChiefLink.isMsgReadyFlag())
            updated = false;
        ChiefLink.setMsgReceived(null);
        ChiefLink.setMsgReadyFlag(false);
        updated    = JsonHelper.parseBoolean(ChiefLink.getMsgReceived());

        return updated;
    }


}

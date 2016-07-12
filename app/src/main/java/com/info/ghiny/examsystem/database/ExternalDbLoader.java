package com.info.ghiny.examsystem.database;

import android.os.Handler;

import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.TCPClient;

import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 07/07/2016.
 */
public class ExternalDbLoader {

    private static TCPClient tcpClient  = null;

    //= Setter & Getter ============================================================================
    public static void setTcpClient(TCPClient tcpClient) {
        ExternalDbLoader.tcpClient = tcpClient;
    }

    public static TCPClient getTcpClient() {
        return tcpClient;
    }

    //= Extend Methods =============================================================================
    public void checkForResult(){

    }

    //= Public Methods =============================================================================
    /*public static StaffIdentity getStaffIdentity(String scanIdNumber){
        StaffIdentity id = null;
        String str = JsonHelper.formatString(JsonHelper.TYPE_Q_IDENTITY, scanIdNumber);

        if(str != null && tcpClient != null){
            tcpClient.sendMessage(str);
            while(!ChiefLink.isMsgReadyFlag())
                id = null;
            id = JsonHelper.parseStaffIdentity(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
            LoginHelper.setStaff(id);
        }

        return id;
    }*/

    public static boolean tryLogin(String staffId, String staffPw) throws ProcessException{
        boolean isCorrect = false;

        String str = JsonHelper.formatPassword(staffId, staffPw);

        if(str != null && tcpClient != null){
            tcpClient.sendMessage(str);
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    if(LoginHelper.getStaff().getName() == null){

                    }
                }
            }, 5000);*/
            while(!ChiefLink.isMsgReadyFlag())
                isCorrect = false;
            isCorrect = JsonHelper.parseStaffIdentity(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReadyFlag(false);
            ChiefLink.setMsgReceived(null);
        } else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        return isCorrect;
    }

    public static AttendanceList dlAttdList(){
        AttendanceList attdList = null;
        String str = JsonHelper.formatString(JsonHelper.TYPE_ATTD_LIST,
                LoginHelper.getStaff().getVenueHandling());

        if (tcpClient != null) {
            tcpClient.sendMessage(str);
            while(!ChiefLink.isMsgReadyFlag()) {
                attdList = null;
            }

            attdList = JsonHelper.parseAttdList(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
        }

        return attdList;
    }

    public static HashMap<String, ExamSubject> dlPaperList() {
        HashMap<String, ExamSubject> map = null;

        String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_VENUE,
                LoginHelper.getStaff().getVenueHandling());
        if (tcpClient != null){
            tcpClient.sendMessage(str);
            while(!ChiefLink.isMsgReadyFlag()) {
                map = null;
            }
            map = JsonHelper.parsePaperMap(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
        }

        return map;
    }

    public static List<ExamSubject> getPapersExamineByCdd(String scanRegNum){
        List<ExamSubject> subjects = null;
        String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_CDD, scanRegNum);

        if(tcpClient != null){
            tcpClient.sendMessage(str);    //suppose to send JSON
            while(!ChiefLink.isMsgReadyFlag()) {
                subjects = null;
            }

            subjects        = JsonHelper.parsePaperList(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
        }
        //return null if wasn't a candidate
        return subjects;
    }

    public static boolean updateAttdList(AttendanceList attdList){
        String str = JsonHelper.formatAttdList(attdList);
        boolean received = false;

        if(tcpClient != null){
            tcpClient.sendMessage(str);    //suppose to send JSON
            while(!ChiefLink.isMsgReadyFlag())
                received = false;
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
            received    = JsonHelper.parseBoolean(ChiefLink.getMsgReceived());
        }

        return received;
    }

    public static boolean acknowledgeCollection(String scanBundleCode){
        String str = JsonHelper.formatCollection(scanBundleCode);
        boolean updated = false;

        if(tcpClient != null){
            tcpClient.sendMessage(str);
            while(!ChiefLink.isMsgReadyFlag())
                updated = false;
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
            updated    = JsonHelper.parseBoolean(ChiefLink.getMsgReceived());
        }

        return updated;
    }


}

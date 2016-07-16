package com.info.ghiny.examsystem.database;

import android.os.Handler;

import com.info.ghiny.examsystem.MainLoginActivity;
import com.info.ghiny.examsystem.adapter.ExamSubjectAdapter;
import com.info.ghiny.examsystem.tools.AssignHelper;
import com.info.ghiny.examsystem.tools.ChiefLink;
import com.info.ghiny.examsystem.tools.ErrorManager;
import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.JsonHelper;
import com.info.ghiny.examsystem.tools.LoginHelper;
import com.info.ghiny.examsystem.tools.ProcessException;
import com.info.ghiny.examsystem.tools.TCPClient;

import org.json.JSONException;
import org.json.JSONObject;

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

    //= Public Methods =============================================================================

    public static void tryLogin(String staffId, String staffPw) throws ProcessException{
        String str = JsonHelper.formatPassword(staffId, staffPw);

        if(str != null && tcpClient != null){
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void dlAttdList(){

        String str = JsonHelper.formatString(JsonHelper.TYPE_ATTD_LIST,
                LoginHelper.getStaff().getVenueHandling());

        if (str != null && tcpClient != null) {
            tcpClient.sendMessage(str);
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!ChiefLink.isComplete()) {
                        ProcessException err = new ProcessException(
                                "Attendance List request times out.",
                                ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                        err.setListener(ProcessException.okayButton,
                                MainLoginActivity.timesOutListener);
                        if (errMng != null)
                            errMng.displayError(err);
                    }
                }
            }, 10000);*/
        }
    }

    public static void dlPaperList() {
        String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_VENUE,
                LoginHelper.getStaff().getVenueHandling());
        if (tcpClient != null){
            tcpClient.sendMessage(str);
            /*ChiefLink.setTimesOutFlag(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!ChiefLink.isComplete())
                        ChiefLink.setTimesOutFlag(true);
                }
            }, 10000);*/
            /*
            while(!ChiefLink.isMsgReadyFlag()) {
                map = null;
            }
            map = JsonHelper.parsePaperMap(ChiefLink.getMsgReceived());
            ChiefLink.setMsgReceived(null);
            ChiefLink.setMsgReadyFlag(false);
            */
        }
    }

    public static void getPapersExamineByCdd(String scanRegNum) throws ProcessException{
        String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_CDD, scanRegNum);

        if(tcpClient != null){
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void updateAttdList(AttendanceList attdList) throws ProcessException{
        String str = JsonHelper.formatAttdList(attdList);
        if(tcpClient != null){
            tcpClient.sendMessage(str);
        }else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void acknowledgeCollection(String scanBundleCode) throws ProcessException{
        String str = JsonHelper.formatCollection(scanBundleCode);
        if(tcpClient != null){
            tcpClient.sendMessage(str);
        }else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }


}
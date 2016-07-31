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

    private static TCPClient tcpClient;

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
        }
    }

    public static void dlPaperList() {
        String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_VENUE,
                LoginHelper.getStaff().getVenueHandling());
        if (tcpClient != null){
            tcpClient.sendMessage(str);
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
        }   else {
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

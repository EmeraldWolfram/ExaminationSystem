package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TCPClient;

/**
 * Created by GhinY on 07/07/2016.
 */
public class ExternalDbLoader {

    private static TCPClient tcpClient;
    private static ConnectionTask connectionTask;

    //= Setter & Getter ============================================================================
    public static void setTcpClient(TCPClient tcpClient) {
        ExternalDbLoader.tcpClient = tcpClient;
    }
    public static TCPClient getTcpClient() {
        return tcpClient;
    }

    public static void setConnectionTask(ConnectionTask connectionTask) {
        ExternalDbLoader.connectionTask = connectionTask;
    }
    public static ConnectionTask getConnectionTask() {
        return connectionTask;
    }

    //= Public Methods =============================================================================

    public static void requestDuelMessage() throws ProcessException {
        if (tcpClient != null) {
            String str  = JsonHelper.formatString(Connector.CONNECT_MESSAGE, "");
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to request duel message!\n" +
                    "Please consult developer", ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void tryLogin(String staffId, String staffPw) throws ProcessException{
        if(tcpClient != null && staffId != null && staffPw != null){
            String str = JsonHelper.formatPassword(staffId, staffPw);
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void dlAttendanceList() throws ProcessException {
        StaffIdentity id = LoginModel.getStaff();
        if (tcpClient != null && id != null) {
            String str = JsonHelper.formatString(JsonHelper.TYPE_ATTD_LIST, id.getVenueHandling());
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to request attendance list!\n" +
                    "Please consult developer", ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void dlPaperList() throws ProcessException {
        String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_VENUE,
                LoginModel.getStaff().getVenueHandling());
        if (tcpClient != null){
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to request attendance list!\n" +
                    "Please consult developer", ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void getPapersExamineByCdd(String scanRegNum) throws ProcessException{
        if(tcpClient != null && scanRegNum != null){
            String str = JsonHelper.formatString(JsonHelper.TYPE_PAPERS_CDD, scanRegNum);
            tcpClient.sendMessage(str);
        } else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void updateAttdList(AttendanceList attdList) throws ProcessException{
        if(tcpClient != null && attdList != null){
            String str = JsonHelper.formatAttdList(attdList);
            tcpClient.sendMessage(str);
        }   else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void acknowledgeCollection(String scanBundleCode) throws ProcessException{
        if(tcpClient != null && scanBundleCode != null){
            String str = JsonHelper.formatCollection(scanBundleCode);
            tcpClient.sendMessage(str);
        }else {
            throw new ProcessException("FATAL: Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }


}

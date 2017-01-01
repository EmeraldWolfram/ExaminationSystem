package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.ConnectionTask;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.LoginModel;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.JavaHost;

import java.util.ArrayList;

/**
 * Created by GhinY on 07/07/2016.
 */
public class ExternalDbLoader {

    private static JavaHost javaHost;
    private static ConnectionTask connectionTask;

    //= Setter & Getter ============================================================================
    public static void setJavaHost(JavaHost javaHost) {
        ExternalDbLoader.javaHost = javaHost;
    }
    public static JavaHost getJavaHost() {
        return javaHost;
    }

    public static void setConnectionTask(ConnectionTask connectionTask) {
        ExternalDbLoader.connectionTask = connectionTask;
    }
    public static ConnectionTask getConnectionTask() {
        return connectionTask;
    }

    //= Public Methods =============================================================================

    public static void requestDuelMessage(String idNo) throws ProcessException {
        if (javaHost != null) {
            String str  = JsonHelper.formatString(JsonHelper.TYPE_RECONNECTION, idNo);
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to request duel message!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void tryLogin(String staffId, String staffPw) throws ProcessException{
        if(javaHost != null && staffId != null && staffPw != null){
            String str = JsonHelper.formatStaff(staffId, staffPw);
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void dlAttendanceList() throws ProcessException {
        StaffIdentity id = LoginModel.getStaff();
        if (javaHost != null && id != null) {
            String str = JsonHelper.formatString(JsonHelper.TYPE_VENUE_INFO,
                    id.getExamVenue());
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to request attendance list!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void getPapersExamineByCdd(String scanRegNum) throws ProcessException{
        if(javaHost != null && scanRegNum != null){
            String str = JsonHelper.formatString(JsonHelper.TYPE_CANDIDATE_INFO, scanRegNum);
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void updateAttendanceList(AttendanceList attdList) throws ProcessException{
        if(javaHost != null && attdList != null){
            String str = JsonHelper.formatAttendanceList(LoginModel.getStaff(), attdList);
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void acknowledgeCollection(String staffId, PaperBundle bundle) throws ProcessException{
        if(javaHost != null && bundle != null && staffId != null){
            String str = JsonHelper.formatCollection(staffId, bundle);
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public static void undoCollection(String staffId, PaperBundle bundle) throws ProcessException {
        if(javaHost != null && bundle != null && staffId != null){
            String str = JsonHelper.formatUndoCollection(staffId, bundle);
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to send out request!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    //Android Android Communication
    public static void updateAttendance(ArrayList<Candidate> candidates) throws ProcessException {
        if(javaHost != null && candidates != null){
            String str = JsonHelper.formatAttendanceUpdate(candidates);
            javaHost.putMessageIntoSendQueue(str);
            candidates.clear();
        } else {
            throw new ProcessException("Fail to send out update!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

    }

    public static void acknowledgeUpdateReceive() throws ProcessException {
        if(javaHost != null){
            String str = JsonHelper.formatUpdateAcknowledge();
            javaHost.putMessageIntoSendQueue(str);
        } else {
            throw new ProcessException("Fail to send out update!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

}

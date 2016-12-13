package com.info.ghiny.examsystem.database;

import android.nfc.TagLostException;

import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user09 on 12/13/2016.
 */

public class TasksSynchronizer {

    private static boolean distributed;

    /**
     * Check Client Request <Live in Receiver Client Thread>
     *
     */

    public static boolean isDistributed() {
        return distributed;
    }

    //TODO: Use a loop and send to everyone connected
    public static void updateAttendance(ArrayList<Candidate> candidates) throws ProcessException {
        //if(tcpClient != null && candidates != null){
            String str = JsonHelper.formatAttendanceUpdate(candidates);
        //    tcpClient.sendMessage(str);
        //} else {
        throw new ProcessException("Fail to send out update!\nPlease consult developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        //}


        //candidates.clear();
    }

    void onReqCollection(String inStr){
        //ToDo: Pass the request
    }

    void onReqIdentification(String inStr){
        //ToDo: Pass the request
    }


    void onReqVenueInfo(){
        //ToDo: send the latest AttendanceList & PaperMap
        AttendanceList attdList = TakeAttdModel.getAttdList();
        HashMap<String, ExamSubject> subjects   = Candidate.getPaperList();
    }

    void onAttendanceUpdateFromClients(ArrayList<Candidate> modifyList){
        for(int i=0; i < modifyList.size(); i++){
            Candidate cdd = modifyList.get(i);
            if(cdd.getStatus() == Status.PRESENT){
                TakeAttdModel.assignCandidate(cdd.getCollector(), cdd.getRegNum(),
                        cdd.getTableNumber(), cdd.isLate());
                TakeAttdModel.updatePresentForUpdatingList(cdd);
            } else {
                TakeAttdModel.unassignCandidate(cdd.getRegNum());
                TakeAttdModel.updateAbsentForUpdatingList(cdd);
            }
        }

    }


}

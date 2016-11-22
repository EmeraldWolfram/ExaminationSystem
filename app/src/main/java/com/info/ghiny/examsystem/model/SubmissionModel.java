package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionModel implements SubmissionMVP.MvpModel {
    private HashMap<String, Integer> unassignedMap;
    private SubmissionMVP.MvpMPresenter taskPresenter;
    private boolean sent;
    private AttendanceList attendanceList;

    public SubmissionModel(SubmissionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.sent           = false;
        this.unassignedMap  = new HashMap<>();
        this.attendanceList = TakeAttdModel.getAttdList();
    }

    @Override
    public void uploadAttdList() throws ProcessException {
        this.sent   = false;
        ExternalDbLoader.updateAttendanceList(attendanceList);
    }

    @Override
    public void verifyChiefResponse(String messageRx) throws ProcessException {
        sent    = true;
        boolean uploaded = JsonHelper.parseBoolean(messageRx);
        throw new ProcessException("Submission successful",
                ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public ArrayList<Candidate> getCandidatesWith(Status status) {
        ArrayList<Candidate> candidates = new ArrayList<>();

        List<String> regNumList  = attendanceList.getAllCandidateRegNumList();

        for (int i = 0; i < regNumList.size(); i++) {
            if(attendanceList.getCandidate(regNumList.get(i)).getStatus() == status){
                candidates.add(attendanceList.getCandidate(regNumList.get(i)));
            }
        }

        return candidates;
    }

    @Override
    public void unassignCandidate(String tableNumber, String cddRegNum) throws ProcessException{
        if(attendanceList == null){
            throw new ProcessException("Attendance List not initialized",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Candidate targetCdd   = attendanceList.getCandidate(cddRegNum);

        if(targetCdd == null || targetCdd.getStatus() != Status.PRESENT){
            throw new ProcessException("Candidate Info Corrupted",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        unassignedMap.put(targetCdd.getRegNum(), Integer.parseInt(tableNumber));
        targetCdd.setStatus(Status.ABSENT);
        targetCdd.setTableNumber(0);

        attendanceList.removeCandidate(targetCdd.getRegNum());
        attendanceList.addCandidate(targetCdd, targetCdd.getPaperCode(),
                targetCdd.getStatus(), targetCdd.getProgramme());
    }

    @Override
    public void assignCandidate(String cddIndex) throws ProcessException{
        if(attendanceList == null){
            throw new ProcessException("Attendance List not initialized",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Candidate cdd   = attendanceList.getCandidateUsingExamIndex(cddIndex);

        if(cdd == null || cdd.getStatus() != Status.ABSENT){
            throw new ProcessException("Candidate Info Corrupted",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Integer table   = unassignedMap.get(cdd.getRegNum());
        if(table == null){
            throw new ProcessException("Candidate is never assign before",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        attendanceList.removeCandidate(cdd.getRegNum());
        cdd.setTableNumber(table);
        cdd.setStatus(Status.PRESENT);
        attendanceList.addCandidate(cdd, cdd.getPaperCode(), cdd.getStatus(), cdd.getProgramme());
        unassignedMap.remove(cdd.getRegNum());
    }

    @Override
    public void run() {
        try{
            if(!sent){
                ProcessException err = new ProcessException(
                        "Server busy. Upload times out.\nPlease try again later.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err) {
            taskPresenter.onTimesOut(err);
        }
    }
}

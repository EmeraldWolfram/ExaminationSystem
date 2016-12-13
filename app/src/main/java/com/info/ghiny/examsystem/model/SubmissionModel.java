package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.LoginMVP;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.manager.SortManager;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by user09 on 11/17/2016.
 */

public class SubmissionModel implements SubmissionMVP.MvpModel {
    private HashMap<String, Integer> unassignedMap;
    private SubmissionMVP.MvpMPresenter taskPresenter;
    private boolean sent;
    private AttendanceList attendanceList;
    private SortManager sortManager;
    private List<String> regNumList;
    private StaffIdentity user;

    public SubmissionModel(SubmissionMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.sent           = false;
        this.unassignedMap  = new HashMap<>();
        this.attendanceList = TakeAttdModel.getAttdList();
        this.sortManager    = new SortManager();
        this.regNumList     = attendanceList.getAllCandidateRegNumList();
        this.user           = LoginModel.getStaff();
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
        if(!user.matchPassword(password))
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
    public ArrayList<Candidate> getCandidatesWith(Status                    status,
                                                  SortManager.SortMethod    sortMethod,
                                                  boolean                   ascendingOrder) {

        TreeSet<Candidate> treeSet  = new TreeSet<>(sortManager.getComparator(sortMethod));
        for(int i = 0; i < regNumList.size(); i++) {
            Candidate tempCdd = attendanceList.getCandidate(regNumList.get(i));
            if(tempCdd.getStatus() == status){
                treeSet.add(tempCdd);
            }
        }

        if(!ascendingOrder)
            return new ArrayList<>(treeSet.descendingSet());
        return new ArrayList<>(treeSet);
    }

    @Override
    public void unassignCandidate(int lastPosition, Candidate candidate) throws ProcessException {
        if(candidate == null || candidate.getStatus() != Status.PRESENT){
            throw new ProcessException("Candidate Info Corrupted",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        unassignedMap.put(candidate.getRegNum(), candidate.getTableNumber());
        candidate.setStatus(Status.ABSENT);
        candidate.setTableNumber(0);
        attendanceList.removeCandidate(candidate.getRegNum());
        attendanceList.addCandidate(candidate);
        TakeAttdModel.updateAbsentForUpdatingList(candidate);
    }

    @Override
    public void assignCandidate(Candidate candidate) throws ProcessException {
        if(candidate == null || candidate.getStatus() != Status.ABSENT){
            throw new ProcessException("Candidate Info Corrupted",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Integer table   = unassignedMap.remove(candidate.getRegNum());
        if(table == null){
            throw new ProcessException("Candidate is never assign before",
                    ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        }

        attendanceList.removeCandidate(candidate.getRegNum());
        candidate.setTableNumber(table);
        candidate.setStatus(Status.PRESENT);
        attendanceList.addCandidate(candidate);
        TakeAttdModel.updatePresentForUpdatingList(candidate);
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

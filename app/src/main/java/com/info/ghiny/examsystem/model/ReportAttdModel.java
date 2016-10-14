package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.ReportAttdMVP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 24/06/2016.
 */

public class ReportAttdModel implements ReportAttdMVP.Model {
    private HashMap<String, Integer> unassignedMap;
    private ReportAttdMVP.MPresenter taskPresenter;

    public ReportAttdModel(ReportAttdMVP.MPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.unassignedMap  = new HashMap<>();
    }

    //= public Methods =============================================================================
    @Override
    public void uploadAttdList() throws ProcessException{
        ConnectionTask.setCompleteFlag(false);
        ExternalDbLoader.updateAttdList(TakeAttdModel.getAttdList());
    }

    @Override
    public List<String> getTitleList(Status status){
        List<String> papers = new ArrayList<>();
        AttendanceList attdList = TakeAttdModel.getAttdList();

        HashMap<String, HashMap<String, HashMap<String, Candidate>>>
                paperMap = attdList.getPaperList(status);

        String[] paperArr = Arrays.copyOf(paperMap.keySet().toArray(),
                paperMap.keySet().toArray().length, String[].class);

        for(String paper: paperArr){
            papers.add(paper);
        }

        return papers;
    }

    @Override
    public HashMap<String, List<Candidate>> getChildList(Status status){
        HashMap<String, HashMap<String, Candidate>> prgList;
        HashMap<String, Candidate> cddMap;
        HashMap<String, List<Candidate>> cddChild   = new HashMap<>();

        AttendanceList attdList     = TakeAttdModel.getAttdList();
        List<String> paperHeader    = getTitleList(status);

        for(int i = 0; i < paperHeader.size(); i++){
            List<Candidate> cddList = new ArrayList<>();
            prgList = attdList.getProgrammeList(status, paperHeader.get(i));
            String[] prgArr = Arrays.copyOf(prgList.keySet().toArray(),
                    prgList.keySet().toArray().length, String[].class);
            for(String prg: prgArr){
                cddMap = attdList.getCandidateList(status, paperHeader.get(i), prg);
                Candidate[] cddArr = Arrays.copyOf(cddMap.values().toArray(),
                        cddMap.values().toArray().length, Candidate[].class);
                for(Candidate cdd: cddArr){
                    cddList.add(cdd);
                }
            }
            cddChild.put(paperHeader.get(i), cddList);
        }

        return cddChild;
    }

    @Override
    public void unassignCandidate(String tableNumber, String cddIndex) throws ProcessException{
        assert tableNumber  != null;
        assert cddIndex     != null;

        AttendanceList attdList = TakeAttdModel.getAttdList();
        if(attdList == null){
            throw new ProcessException("FATAL ERROR: Attendance List not initialized",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Candidate targetCdd   = attdList.getCandidateUsingExamIndex(cddIndex);

        if(targetCdd == null || targetCdd.getStatus() != Status.PRESENT){
            throw new ProcessException("FATAL ERROR: Candidate Info Corrupted",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        unassignedMap.put(targetCdd.getRegNum(), Integer.parseInt(tableNumber));
        targetCdd.setStatus(Status.ABSENT);
        targetCdd.setTableNumber(0);

        attdList.removeCandidate(targetCdd.getRegNum());
        attdList.addCandidate(targetCdd, targetCdd.getPaperCode(),
                targetCdd.getStatus(), targetCdd.getProgramme());
    }

    @Override
    public void assignCandidate(String cddIndex) throws ProcessException{
        assert cddIndex != null;

        AttendanceList attdList = TakeAttdModel.getAttdList();
        if(attdList == null){
            throw new ProcessException("FATAL ERROR: Attendance List not initialized",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Candidate cdd   = attdList.getCandidateUsingExamIndex(cddIndex);

        if(cdd == null || cdd.getStatus() != Status.ABSENT){
            throw new ProcessException("FATAL ERROR: Candidate Info Corrupted",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        Integer table   = unassignedMap.get(cdd.getRegNum());
        if(table == null){
            throw new ProcessException("FATAL ERROR: Candidate is never assign before",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        attdList.removeCandidate(cdd.getRegNum());
        cdd.setTableNumber(table);
        cdd.setStatus(Status.PRESENT);
        attdList.addCandidate(cdd, cdd.getPaperCode(), cdd.getStatus(), cdd.getProgramme());
        unassignedMap.remove(cdd.getRegNum());
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void run() {
        try{
            if(!ConnectionTask.isComplete()){
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

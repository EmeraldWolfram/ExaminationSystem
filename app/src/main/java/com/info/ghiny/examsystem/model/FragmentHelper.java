package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 24/06/2016.
 */
public class FragmentHelper {

    //= public Methods =============================================================================
    public void uploadAttdList() throws ProcessException{
        ChiefLink.setCompleteFlag(false);
        ExternalDbLoader.updateAttdList(AssignHelper.getAttdList());
    }

    public List<String> getTitleList(Status status){
        List<String> papers = new ArrayList<>();
        AttendanceList attdList = AssignHelper.getAttdList();

        HashMap<String, HashMap<String, HashMap<String, Candidate>>>
                paperMap = attdList.getPaperList(status);

        String[] paperArr = Arrays.copyOf(paperMap.keySet().toArray(),
                paperMap.keySet().toArray().length, String[].class);

        for(String paper: paperArr){
            papers.add(paper);
        }

        return papers;
    }

    public HashMap<String, List<Candidate>> getChildList(Status status){
        HashMap<String, HashMap<String, Candidate>> prgList;
        HashMap<String, Candidate> cddMap;
        HashMap<String, List<Candidate>> cddChild   = new HashMap<>();

        AttendanceList attdList     = AssignHelper.getAttdList();
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

    public static void resetCandidate(Integer table){
        AttendanceList attdList = AssignHelper.getAttdList();
        HashMap<Integer, String> assgnList  = AssignHelper.getAssgnList();

        if(table != null){
            Candidate cdd = attdList.getCandidate(assgnList.get(table));
            attdList.removeCandidate(cdd.getRegNum());
            cdd.setTableNumber(0);
            cdd.setStatus(Status.ABSENT);
            attdList.addCandidate(cdd, cdd.getPaperCode(), cdd.getStatus(), cdd.getProgramme());
            assgnList.remove(table);
        }
    }

}

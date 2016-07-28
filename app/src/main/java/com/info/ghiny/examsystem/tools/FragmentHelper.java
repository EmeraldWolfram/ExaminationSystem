package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.FragmentListActivity;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.LocalDbLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 24/06/2016.
 */
public class FragmentHelper {

    private static FragmentListActivity listAct = null;

    //= Setter and Getter ==========================================================================
    public static void setListAct(FragmentListActivity listAct) {
        FragmentHelper.listAct = listAct;
    }

    public static FragmentListActivity getListAct() {
        return listAct;
    }

    public static void endListActivity(){
        listAct.finish();
        listAct = null;
    }

    //= public Methods =============================================================================
    public static void uploadAttdList() throws ProcessException{
        ExternalDbLoader.updateAttdList(AssignHelper.getAttdList());
    }

    public static List<String> getTitleList(AttendanceList.Status status){
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

    public static HashMap<String, List<Candidate>> getChildList(AttendanceList.Status status){
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

}

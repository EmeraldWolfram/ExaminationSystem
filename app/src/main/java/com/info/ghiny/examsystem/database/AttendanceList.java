package com.info.ghiny.examsystem.database;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class AttendanceList {

    private HashMap<Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>>
            attendanceList;

    public AttendanceList(){
        attendanceList = new HashMap<>();

        HashMap<String, HashMap<String, HashMap<String, Candidate>>> present = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> absent  = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> barred  = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> exempt  = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> quaran  = new HashMap<>();

        attendanceList.put(Status.PRESENT, present);
        attendanceList.put(Status.ABSENT, absent);
        attendanceList.put(Status.BARRED, barred);
        attendanceList.put(Status.EXEMPTED, exempt);
        attendanceList.put(Status.QUARANTINED, quaran);
    }

    public AttendanceList(HashMap<String, HashMap<String, HashMap<String, Candidate>>> present,
                          HashMap<String, HashMap<String, HashMap<String, Candidate>>> absent,
                          HashMap<String, HashMap<String, HashMap<String, Candidate>>> barred,
                          HashMap<String, HashMap<String, HashMap<String, Candidate>>> exempt,
                          HashMap<String, HashMap<String, HashMap<String, Candidate>>> quaran){
        attendanceList = new HashMap<>();
        attendanceList.put(Status.PRESENT, present);
        attendanceList.put(Status.ABSENT, absent);
        attendanceList.put(Status.BARRED, barred);
        attendanceList.put(Status.EXEMPTED, exempt);
        attendanceList.put(Status.QUARANTINED, quaran);
    }

    //Inherit Methods
    public HashMap<String, HashMap<String, HashMap<String, Candidate>>> getPaperList(Status status){
        return attendanceList.get(status);
    }

    public HashMap<String,HashMap<String,Candidate>> getProgrammeList(Status status, String paperCode){
        if(status == null || paperCode == null){
            return null;
        }
        HashMap<String, HashMap<String, Candidate>> prgList;
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperMap = getPaperList(status);

        if(!paperMap.containsKey(paperCode)){
            prgList = new HashMap<>();
            paperMap.put(paperCode, prgList);
        } else
            prgList = paperMap.get(paperCode);

        return prgList;
    }

    public HashMap<String, Candidate> getCandidateList(Status status, String paperCode, String programme){
        if(status == null || paperCode == null || programme == null){
            return null;
        }

        HashMap<String, Candidate> cddList = new HashMap<>();
        HashMap<String, HashMap<String, Candidate>> prgMap = getProgrammeList(status, paperCode);

        if(!prgMap.containsKey(programme))
            prgMap.put(programme, cddList);
        else
            cddList = prgMap.get(programme);

        return cddList;
    }

    //=========================================================================
    //Available Methods
    //=========================================================================

    //------------------- AttendanceList Setter Getter ------------------------------------
    public HashMap<Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>>
    getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(HashMap<Status, HashMap<String, HashMap<String,
            HashMap<String, Candidate>>>> attendanceList) {
        this.attendanceList = attendanceList;
    }
    //------------------- Methods Calculate Number of Item _-------------------------------
    public int getNumberOfStatus(){
        return attendanceList.size();
    }

    public int getNumberOfPaper(Status status){
        if(status == null){
            return 0;
        }
        return attendanceList.get(status).size();
    }

    public int getNumberOfProgramme(Status status, String paperCode){
        if(status == null || paperCode == null){
            return 0;
        }
        int size = 0;
        if(attendanceList.get(status).containsKey(paperCode))
            size = attendanceList.get(status).get(paperCode).size();
        return size;
    }

    public int getTotalNumberOfCandidates(){
        return getAllCandidateRegNumList().size();
    }

    public int getNumberOfCandidates(Status status){
        int size = 0;
        HashMap<String, HashMap<String, HashMap<String, Candidate>>> s1 = getPaperList(status);
        for(Map.Entry<String, HashMap<String, HashMap<String, Candidate>>> s2: s1.entrySet()){
            for(Map.Entry<String, HashMap<String, Candidate>> s3: s2.getValue().entrySet()){
                size = size + s3.getValue().size();
            }
        }

        return size;
    }

    public int getNumberOfCandidates(Status status, String paperCode, String programme){
        int size = 0;
        HashMap<String, HashMap<String, Candidate>> prgList;

        if(attendanceList.get(status).containsKey(paperCode)){
            prgList = attendanceList.get(status).get(paperCode);
            if(prgList.containsKey(programme))
                size = prgList.get(programme).size();
        }
        return size;
    }

    //------------------- Major Attendance Taking Tools --------------------------------------
    public void addCandidate(Candidate cdd){
        String paperCode    = cdd.getPaperCode();
        Status status       = cdd.getStatus();
        String programme    = cdd.getProgramme();

        try{
            HashMap<String, Candidate> cddList = getCandidateList(status, paperCode, programme);
            if(cddList.isEmpty()) {
                cddList.put(cdd.getRegNum(), cdd);
            } else {
                cdd.setStatus(status);
                cddList.put(cdd.getRegNum(), cdd);
            }

        }catch (Exception e){
            Log.d("EXAM System", e.getMessage());
        }
    }

    public Candidate removeCandidate(String regNum){
        assert regNum != null;
        Candidate cdd;
        for(Map.Entry<Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>> s:
                attendanceList.entrySet()){
            cdd = fromPaperRemoveCandidate(regNum, s.getValue());
            if(cdd != null){
                return cdd;
            }
        }
        return null;
    }

    public Candidate getCandidate(String regNum){
        assert regNum != null;
        Candidate candidate = null;
        for(Map.Entry<Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>> s:
                attendanceList.entrySet()){
            candidate = fromPaperFindCandidate(regNum, s.getValue());
            if(candidate != null)
                break;
        }
        return candidate;
    }

    public Candidate getCandidateUsingExamIndex(String index){
        Candidate targetCdd     = null;
        List<String> cddList    = getAllCandidateRegNumList();

        for(int i=0; i < cddList.size(); i++){
            Candidate cdd   = getCandidate(cddList.get(i));
            if(cdd.getExamIndex().equals(index)){
                targetCdd   = cdd;
            }
        }

        return targetCdd;
    }

    public List<String> getAllCandidateRegNumList(){
        List<String> regNumList = new ArrayList<>();

        for(Map.Entry<Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>> s:
                attendanceList.entrySet())
            fromPaperGetRegNum(regNumList, s.getValue());

        return regNumList;
    }

    //=========================================================================================
    //Private internal function used to traverse the level of Map
    //=========================================================================================
    private void fromPaperGetRegNum(List<String> regNumList,
                                    HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperMap){
        for(Map.Entry<String, HashMap<String, HashMap<String, Candidate>>> s:paperMap.entrySet())
            fromProgrammeGetRegNum(regNumList, s.getValue());
    }

    private void fromProgrammeGetRegNum(List<String> regNumList,
                                    HashMap<String, HashMap<String, Candidate>> map){
        for(Map.Entry<String, HashMap<String, Candidate>> s:map.entrySet())
            fromCandidatesGetRegNum(regNumList, s.getValue());
    }

    private void fromCandidatesGetRegNum(List<String> regNumList,
                                         HashMap<String, Candidate> cddMap){
        for(Map.Entry<String, Candidate> s:cddMap.entrySet())
            regNumList.add(s.getKey());
    }

    //========================================================================================
    private Candidate fromPaperRemoveCandidate(String regNum, HashMap<String, HashMap<String,
            HashMap<String, Candidate>>> paperMap){
        assert regNum != null;
        assert paperMap != null;
        Candidate cdd;

        for(Map.Entry<String, HashMap<String, HashMap<String, Candidate>>> s:paperMap.entrySet()){
            cdd = fromProgrammeRemoveCandidate(regNum, s.getValue());
            if(cdd != null){
                return cdd;
            }
        }
        return null;
    }

    private Candidate fromProgrammeRemoveCandidate(String regNum, HashMap<String,
            HashMap<String, Candidate>> map){
        assert regNum != null;
        assert map != null;
        Candidate cdd;

        for(Map.Entry<String, HashMap<String, Candidate>> s: map.entrySet()){
            cdd = fromCandidatesRemoveCandidate(regNum, s.getValue());
            if(cdd != null){
                return cdd;
            }
        }

        return null;
    }

    private Candidate fromCandidatesRemoveCandidate(String regNum, HashMap<String, Candidate> map){
        assert regNum != null;
        assert map != null;
        return map.remove(regNum);
    }
    //=========================================================================================
    private Candidate fromPaperFindCandidate(String regNum, HashMap<String,
            HashMap<String, HashMap<String, Candidate>>> paperMap){
        assert regNum != null;
        assert paperMap != null;

        Candidate candidate = null;
        for(Map.Entry<String, HashMap<String, HashMap<String, Candidate>>> s:paperMap.entrySet()){
            candidate = fromProgrammeFindCandidate(regNum, s.getValue());
            if(candidate != null)
                break;
        }
        return candidate;
    }

    private Candidate fromProgrammeFindCandidate(String regNum,
                                                 HashMap<String, HashMap<String, Candidate>> map){
        assert regNum != null;
        assert map != null;

        Candidate candidate = null;
        for(Map.Entry<String, HashMap<String, Candidate>> s:map.entrySet()){
            candidate = fromCandidatesFindCandidate(regNum, s.getValue());
            if(candidate != null)
                break;
        }
        return candidate;
    }

    private Candidate fromCandidatesFindCandidate(String regNum,
                                                  HashMap<String, Candidate> map){
        assert regNum != null;
        assert map != null;

        return map.get(regNum);
    }
}

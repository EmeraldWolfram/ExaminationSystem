package com.info.ghiny.examsystem.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GhinY on 25/05/2016.
 */
public class AttendanceList {
    public enum Status {
        PRESENT,
        ABSENT,
        EXEMPTED,
        BARRED
    }
    private HashMap<Status, HashMap<String, HashMap<String, Candidate>>> attendanceList;

    public AttendanceList(){
        attendanceList = new HashMap<Status, HashMap<String, HashMap<String, Candidate>>>();

        HashMap<String, HashMap<String, Candidate>> present =
                new HashMap<String, HashMap<String, Candidate>>();
        HashMap<String, HashMap<String, Candidate>> absent  =
                new HashMap<String, HashMap<String, Candidate>>();
        HashMap<String, HashMap<String, Candidate>> barred  =
                new HashMap<String, HashMap<String, Candidate>>();
        HashMap<String, HashMap<String, Candidate>> exempt  =
                new HashMap<String, HashMap<String, Candidate>>();

        attendanceList.put(Status.PRESENT, present);
        attendanceList.put(Status.ABSENT, absent);
        attendanceList.put(Status.BARRED, barred);
        attendanceList.put(Status.EXEMPTED, exempt);
    }

    public AttendanceList(HashMap<String, HashMap<String, Candidate>> present,
                          HashMap<String, HashMap<String, Candidate>> absent,
                          HashMap<String, HashMap<String, Candidate>> barred,
                          HashMap<String, HashMap<String, Candidate>> exempt){
        attendanceList = new HashMap<Status, HashMap<String, HashMap<String, Candidate>>>();
        attendanceList.put(Status.PRESENT, present);
        attendanceList.put(Status.ABSENT, absent);
        attendanceList.put(Status.BARRED, barred);
        attendanceList.put(Status.EXEMPTED, exempt);
    }

    //Add empty Candidate PaperList into the attendance list
    //Assume Each status will have the same papers
    public void addSubjectList(HashMap<String, HashMap<String, Candidate>> paperList){
        attendanceList.put(Status.PRESENT, paperList);
        attendanceList.put(Status.ABSENT, paperList);
        attendanceList.put(Status.BARRED, paperList);
        attendanceList.put(Status.EXEMPTED, paperList);
    }

    //Add CandidateList into attendance list
    public void addCandidateList(String paperCode,
                               HashMap<String, Candidate> candidateList,
                               Status status){

        HashMap<String, HashMap<String, Candidate>> statusList = attendanceList.get(status);
        HashMap<String, Candidate> papers;

        if(statusList.containsKey(paperCode))
            statusList.put(paperCode, candidateList);
        else{//Strongly error prompt area, if there are same candidate in here, require extra checking
            papers = statusList.get(paperCode);
            papers.putAll(candidateList);
        }
    }

    public HashMap<String, HashMap<String, Candidate>>  getStatusList(Status status) {
        assert status != null;
        HashMap<String, HashMap<String, Candidate>>  list;
        list = attendanceList.get(status);
        return list;
    }

    public void addCandidate(Candidate cdd, ExamSubject paper, Status status){
        assert cdd      != null : "Input Candidate argument cannot be null";
        assert paper    != null : "Input ExamSubject argument cannot be null";
        assert status   != null : "Input Status argument cannot be null";

        HashMap<String, HashMap<String, Candidate>> statusList = attendanceList.get(status);
        assert statusList != null;

        HashMap<String, Candidate> paperList = statusList.get(paper.getPaperCode());
        assert paperList != null;

        cdd.setStatus(status);
        paperList.put(cdd.getRegNum(), cdd);
    }

    public void removeCandidate(String regNum){
        assert regNum != null;
        for(Map.Entry<Status, HashMap<String, HashMap<String, Candidate>>> s: attendanceList.entrySet()){
            fromPaperRemoveCandidate(regNum, s.getValue());
        }
    }

    public Candidate getCandidate(String regNum){
        assert regNum != null;
        Candidate candidate = null;
        for(Map.Entry<Status, HashMap<String, HashMap<String, Candidate>>> s: attendanceList.entrySet()){
            candidate = fromPaperFindCandidate(regNum, s.getValue());
            if(candidate != null)
                break;
        }
        return candidate;
    }

    private void fromPaperRemoveCandidate(String regNum, HashMap<String,
            HashMap<String, Candidate>> paperMap){
        assert regNum != null;
        assert paperMap != null;

        for(Map.Entry<String, HashMap<String, Candidate>> s:paperMap.entrySet()){
            fromCandidatesRemoveCandidate(regNum, s.getValue());
        }
    }

    private void fromCandidatesRemoveCandidate(String regNum,
                                               HashMap<String, Candidate> map){
        map.remove(regNum);
    }

    private Candidate fromPaperFindCandidate(String regNum, HashMap<String,
            HashMap<String, Candidate>> paperMap){
        assert regNum != null;
        assert paperMap != null;

        Candidate candidate = null;
        for(Map.Entry<String, HashMap<String, Candidate>> s:paperMap.entrySet()){
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

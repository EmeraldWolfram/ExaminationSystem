package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Identity;

import java.util.HashMap;

/**
 * Created by GhinY on 15/06/2016.
 */
public class AssignHelper {
    private Candidate tempCdd;
    private Integer tempTable;
    private static AttendanceList attdList;
    public HashMap<Integer, String> assgnList;

    public AssignHelper(){
        this.assgnList  = new HashMap<>();
        this.tempCdd    = null;
        this.tempTable  = null;
    }

    public static void setAttdList(AttendanceList attdList) {
        AssignHelper.attdList = attdList;
    }

    public Integer checkTable(Integer table){
        //Add checking mechanism when venue number is valid
        tempTable = table;
        return tempTable;
    }

    public Candidate checkCandidate(Identity id) throws CustomException{
        if(id == null)
            throw new CustomException("Identity is null", CustomException.ERR_NULL_IDENTITY);

        if(id.getRegNum() == null)
            throw new CustomException("Incomplete Identity", CustomException.ERR_INCOMPLETE_ID);

        if(attdList == null)
            throw new CustomException("No Attendance List", CustomException.ERR_EMPTY_ATTD_LIST);

        Candidate candidate = attdList.getCandidate(id.getRegNum());

        if(candidate == null){
            throw new CustomException("Candidate not in list", CustomException.ERR_NULL_CANDIDATE);
        } else {
            if(candidate.getStatus() == AttendanceList.Status.EXEMPTED)
                throw new CustomException("Candidate exempted",CustomException.ERR_STATUS_EXEMPTED);
            if(candidate.getStatus() == AttendanceList.Status.BARRED)
                throw new CustomException("Candidate barred", CustomException.ERR_STATUS_BARRED);
        }

        tempCdd = candidate;
        return tempCdd;
    }

    public boolean tryAssignCandidate() throws CustomException{
        boolean assigned = false;

        if(tempTable != null && tempCdd != null){
            //If ExamSubject range does not meet, DO something
            if(assgnList.containsKey(tempTable))
                throw new CustomException("Table assigned before",
                        CustomException.ERR_TABLE_REASSIGN);

            if(assgnList.containsValue(tempCdd.getRegNum()))
                throw new CustomException("Candidate assigned before",
                        CustomException.ERR_CANDIDATE_REASSIGN);

            if(!tempCdd.getPaper().isValidTable(tempTable))
                throw new CustomException("Paper for table and candidate does not match",
                        CustomException.ERR_PAPER_NOT_MATCH);

            tempCdd.setTableNumber(tempTable);
            tempCdd.setStatus(AttendanceList.Status.PRESENT);

            assert attdList != null;
            attdList.removeCandidate(tempCdd.getRegNum());
            attdList.addCandidate(tempCdd, tempCdd.getPaperCode(),
                    AttendanceList.Status.PRESENT, tempCdd.getProgramme());
            assgnList.put(tempTable, tempCdd.getRegNum());
            tempCdd     = null;
            tempTable   = null;
            assigned    = true;
        }

        return assigned;
    }


}

package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.Identity;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by GhinY on 15/06/2016.
 */
public class AssignHelper {
    private Candidate tempCdd;
    private Integer tempTable;
    private static ExamDatabaseLoader exDBLoader;
    private static CheckListLoader clDBLoader;
    private static AttendanceList attdList;
    public HashMap<Integer, String> assgnList;

    //= Constructor ================================================================================
    public AssignHelper(){
        this.assgnList  = new HashMap<>();
        this.tempCdd    = null;
        this.tempTable  = null;
    }

    //= Setter & Getter ============================================================================
    //Static setter to initialize the value of Database and AttendanceList
    public static void setClDBLoader(CheckListLoader dBLoader) {
        assert dBLoader != null;
        if(dBLoader.isEmpty()){
            dBLoader.saveAttendanceList(prepareList()); //Suppose to query external DB
        }
        AssignHelper.clDBLoader = dBLoader;

        attdList = new AttendanceList();
        attdList.setAttendanceList(clDBLoader.getLastSavedAttendanceList());
    }

    //Static setter to load in the external Database
    public static void setExternalLoader(ExamDatabaseLoader dbLoader){
        assert dbLoader != null;
        exDBLoader = dbLoader;
        if(Candidate.getPaperList() == null)
            Candidate.setPaperList(fakeTheExamPaper()); //Suppose to query external DB
    }

    public static void setAttdList(AttendanceList attdList) {
        AssignHelper.attdList = attdList;
    }

    //A getter to retrieve the list to display in Activity
    public static AttendanceList getAttdList() {
        return attdList;
    }

    //= Assign =====================================================================================
    //check-in Table
    public Integer checkTable(Integer table){
        //Add checking mechanism when venue size is valid
        tempTable = table;
        return tempTable;
    }

    //check-in Candidate and also check if the candidate is eligible
    public Candidate checkCandidate(String scanString) throws CustomException{
        Identity id = exDBLoader.getIdentity(scanString);

        if(id == null)
            throw new CustomException("Not an Identity", CustomException.ERR_NULL_IDENTITY,
                    IconManager.WARNING);

        if(id.getRegNum() == null)
            throw new CustomException("FATAL: Unable to process ID",
                    CustomException.ERR_INCOMPLETE_ID, IconManager.WARNING);

        if(attdList == null || attdList.getAttendanceList() == null)
            throw new CustomException("No Attendance List", CustomException.ERR_EMPTY_ATTD_LIST,
                    IconManager.WARNING);

        Candidate candidate = attdList.getCandidate(id.getRegNum());

        if(candidate == null){
            throw new CustomException(id.getName() + " doest not belong to this venue",
                    CustomException.ERR_NULL_CANDIDATE, IconManager.WARNING);
        } else {
            if(candidate.getStatus() == AttendanceList.Status.EXEMPTED)
                throw new CustomException("The paper was exempted for " +
                        id.getName(),CustomException.ERR_STATUS_EXEMPTED,
                        IconManager.MESSAGE);
            if(candidate.getStatus() == AttendanceList.Status.BARRED)
                throw new CustomException(id.getName() + " have been barred",
                        CustomException.ERR_STATUS_BARRED, IconManager.MESSAGE);
        }

        tempCdd = candidate;
        return tempCdd;
    }

    //assign the check-in Table Candidate Set in to the List
    public boolean tryAssignCandidate() throws CustomException{
        boolean assigned = false;

        if(tempTable != null && tempCdd != null){
            //If ExamSubject range does not meet, DO something
            if(assgnList.containsKey(tempTable))
                throw new CustomException("Previous: Table " + tempTable + " assigned to "
                        + attdList.getCandidate(assgnList.get(tempTable)).getStudentName()
                        + "\nNew: Table " + tempTable + " assign to " + tempCdd.getStudentName(),
                        CustomException.ERR_TABLE_REASSIGN, IconManager.MESSAGE);

            if(assgnList.containsValue(tempCdd.getRegNum()))
                throw new CustomException("Previous: " + tempCdd.getStudentName()
                        + " assigned to Table "
                        + attdList.getCandidate(tempCdd.getRegNum()).getTableNumber()
                        + "\nNew: " + tempCdd.getStudentName() + " assign to " + tempTable,
                        CustomException.ERR_CANDIDATE_REASSIGN, IconManager.MESSAGE);

            if(!tempCdd.getPaper().isValidTable(tempTable))
                throw new CustomException(tempCdd.getStudentName() + " should not sit here\n"
                        + "Suggest to Table " + tempCdd.getPaper().getStartTableNum(),
                        CustomException.ERR_PAPER_NOT_MATCH, IconManager.WARNING);

            tempCdd.setTableNumber(tempTable);
            tempCdd.setStatus(AttendanceList.Status.PRESENT);

            assert attdList != null;
            attdList.removeCandidate(tempCdd.getRegNum());
            attdList.addCandidate(tempCdd, tempCdd.getPaperCode(), tempCdd.getStatus(),
                    tempCdd.getProgramme());
            assgnList.put(tempTable, tempCdd.getRegNum());
            tempCdd     = null;
            tempTable   = null;
            assigned    = true;
        }

        return assigned;
    }

    //= On Dialog ==================================================================================
    //Replace previously assigned Table Candidate set with New Table Candidate set
    public void updateNewCandidate(int errCode){
        if(errCode == CustomException.ERR_TABLE_REASSIGN){
            //Table reassign, reset the previous assigned candidate in the list to ABSENT
            Candidate cdd = attdList.getCandidate(assgnList.get(tempTable));
            attdList.removeCandidate(cdd.getRegNum());
            cdd.setTableNumber(0);
            cdd.setStatus(AttendanceList.Status.ABSENT);
            attdList.addCandidate(cdd, cdd.getPaperCode(), cdd.getStatus(), cdd.getProgramme());
            assgnList.remove(tempTable);
        } else {
            //Candidate reassign, remove the previously assignment
            assgnList.remove(tempCdd.getTableNumber());
        }

        tempCdd.setStatus(AttendanceList.Status.PRESENT);
        tempCdd.setTableNumber(tempTable);

        attdList.removeCandidate(tempCdd.getRegNum());
        attdList.addCandidate(tempCdd, tempCdd.getPaperCode(), tempCdd.getStatus(),
                tempCdd.getProgramme());
        assgnList.put(tempTable, tempCdd.getRegNum());

        tempCdd     = null;
        tempTable   = null;
    }

    //Remain previously assigned Table Candidate set and discard New Table Candidate set
    public void cancelNewAssign(){
        tempCdd     = null;
        tempTable   = null;
    }

    //= FAKE Function for demo purposes ============================================================
    private static AttendanceList prepareList(){
        AttendanceList attdList = new AttendanceList();

        Candidate cdd1 = new Candidate(0, "RMB3", "FGY", "15WAD00001", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd2 = new Candidate(0, "RMB3", "NYN", "15WAD00002", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd3 = new Candidate(0, "RMB3", "LHN", "15WAD00003", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cdd4 = new Candidate(0, "RMB3", "YZL", "15WAD00004", "BAME 0001", AttendanceList.Status.BARRED);
        Candidate cdd5 = new Candidate(0, "RMB3", "SYL", "15WAD00005", "BAME 0001", AttendanceList.Status.EXEMPTED);
        Candidate cdd6 = new Candidate(0, "RMB3", "WJS", "15WAD00006", "BAME 0001", AttendanceList.Status.BARRED);
        Candidate cddF = new Candidate(0, "RMB3", "FOONG GHIN YEW", "15WAU09184", "BAME 0001", AttendanceList.Status.ABSENT);
        Candidate cddN = new Candidate(0, "RMB3", "NG YEN AENG", "15WAD88888", "BAME 0001", AttendanceList.Status.ABSENT);

        attdList.addCandidate(cdd1, cdd1.getPaperCode(), AttendanceList.Status.ABSENT, cdd1.getProgramme());
        attdList.addCandidate(cdd2, cdd2.getPaperCode(), AttendanceList.Status.ABSENT, cdd2.getProgramme());
        attdList.addCandidate(cdd3, cdd3.getPaperCode(), AttendanceList.Status.ABSENT, cdd3.getProgramme());
        attdList.addCandidate(cdd4, cdd4.getPaperCode(), AttendanceList.Status.BARRED, cdd4.getProgramme());
        attdList.addCandidate(cdd5, cdd5.getPaperCode(), AttendanceList.Status.EXEMPTED, cdd5.getProgramme());
        attdList.addCandidate(cdd6, cdd6.getPaperCode(), AttendanceList.Status.BARRED, cdd6.getProgramme());
        attdList.addCandidate(cddF, cddF.getPaperCode(), AttendanceList.Status.ABSENT, cddF.getProgramme());
        attdList.addCandidate(cddN, cddN.getPaperCode(), AttendanceList.Status.ABSENT, cddN.getProgramme());

        return attdList;
    }
    private static HashMap<String, ExamSubject> fakeTheExamPaper(){
        HashMap<String, ExamSubject> paperMap = new HashMap<>();

        ExamSubject subject1 = new ExamSubject("BAME 0001", "SUBJECT 1", 25, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);
        ExamSubject subject2 = new ExamSubject("BAME 0002", "SUBJECT 2", 55, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);
        ExamSubject subject3 = new ExamSubject("BAME 0003", "SUBJECT 3", 10, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);
        ExamSubject subject4 = new ExamSubject("BAME 0004", "SUBJECT 4", 70, new Date(), 10,
                ExamSubject.ExamVenue.H2, ExamSubject.Session.AM);

        paperMap.put(subject1.getPaperCode(), subject1);
        paperMap.put(subject2.getPaperCode(), subject2);
        paperMap.put(subject3.getPaperCode(), subject3);
        paperMap.put(subject4.getPaperCode(), subject4);

        return paperMap;
    }

}

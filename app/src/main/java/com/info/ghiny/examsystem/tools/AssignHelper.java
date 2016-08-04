package com.info.ghiny.examsystem.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

import com.info.ghiny.examsystem.AssignInfoActivity;
import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.LocalDbLoader;
import com.info.ghiny.examsystem.database.Status;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by GhinY on 15/06/2016.
 */
public class AssignHelper {
    private Candidate tempCdd    = null;
    private Integer tempTable    = null;
    private LocalDbLoader JdbcLoader;

    private CheckListLoader clLoader;
    private static HashMap<Integer, String> assgnList = new HashMap<>();
    private static AttendanceList attdList;
    private AssignInfoActivity assignAct;

    private final DialogInterface.OnClickListener updateListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateNewCandidate();
                    assignAct.clearView();
                    dialog.cancel();
                }
            };

    private final DialogInterface.OnClickListener cancelListener =
            new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelNewAssign();
                    assignAct.clearView();
                    dialog.cancel();
                }
            };

    //= Setter & Getter ============================================================================
    //Static setter to initialize the value of Database and AttendanceList
    //public void initLoader(LocalDbLoader dBLoader) throws ProcessException{
    //    assert dBLoader != null;

    //    if(dBLoader.emptyAttdInDB()){
    //        dBLoader.saveAttendanceList(attdList); //Suppose to query external DB

            /*dBLoader.saveAttendanceList(ExternalDbLoader.dlAttdList());*/
    //    }
    //    if(dBLoader.emptyPapersInDB()) {
    //        dBLoader.savePaperList(Candidate.getPaperList()); //Suppose to query external DB
            /*dBLoader.savePaperList(ExternalDbLoader.dlPaperList());*/
    //    }

    //    this.JdbcLoader = dBLoader;
    //    attdList    = JdbcLoader.queryAttendanceList();
    //    Candidate.setPaperList(dBLoader.queryPapers());
    //}
    public void initLoader(CheckListLoader dBLoader) throws ProcessException{
        assert dBLoader != null;

        if(dBLoader.emptyAttdInDB()){
            dBLoader.saveAttendanceList(attdList); //Suppose to query external DB

            /*dBLoader.saveAttendanceList(ExternalDbLoader.dlAttdList());*/
        }
        if(dBLoader.emptyPapersInDB()) {
            dBLoader.savePaperList(Candidate.getPaperList()); //Suppose to query external DB
            /*dBLoader.savePaperList(ExternalDbLoader.dlPaperList());*/
        }

        this.clLoader = dBLoader;
        //attdList    = clLoader.queryAttendanceList();
        Candidate.setPaperList(clLoader.queryPapers());
    }

    public LocalDbLoader getJdbcLoader() {
        return JdbcLoader;
    }

    public static void setAttdList(AttendanceList attdList) {
        AssignHelper.attdList = attdList;
    }
    public static AttendanceList getAttdList() {
        return attdList;
    }

    public static void setAssgnList(HashMap<Integer, String> assgnList) {
        AssignHelper.assgnList = assgnList;
    }
    public static HashMap<Integer, String> getAssgnList() {
        return assgnList;
    }

    public void setAssignAct(AssignInfoActivity assignAct) {
        this.assignAct = assignAct;
    }
    protected AssignInfoActivity getAssignAct(){
        return assignAct;
    }

    //For test purposes=============================================================================
    public void setTempCdd(Candidate tempCdd) {
        this.tempCdd = tempCdd;
    }

    public Candidate getTempCdd() {
        return tempCdd;
    }

    public void setTempTable(Integer tempTable) {
        this.tempTable = tempTable;
    }

    public Integer getTempTable() {
        return tempTable;
    }

    //= Method for Assign process ==================================================================
    /**
     * tryAssignScanValue()
     *
     * This method check the scanStr length and call one of the following
     * methods to assign the value if the length is possible table or candidate
     * 1. checkCandidate
     * 2. checkTable
     *
     * If the length is not possible to be any useful data for assign process
     * this method throw MESSAGE_TOAST error
     *
     * After that, tryAssignCandidate was called to check if both table and candidate
     * is registered in the buffer and is a valid set of data and take the attendance
     *
     * @param scanStr               The value scan from the QR scanner
     */
    public void tryAssignScanValue(String scanStr) throws ProcessException{
        if(scanStr.length() < 4 && scanStr.length() > 0){
            checkTable(scanStr);
            assignAct.setTableView(tempTable);
        } else if(scanStr.length() == 10){
            checkCandidate(scanStr);
            assignAct.setCandidateView(tempCdd);
        } else {
            throw new ProcessException("Not a valid QR", ProcessException.MESSAGE_TOAST,
                    IconManager.MESSAGE);
        }

        if(tryAssignCandidate()){
            ProcessException err = new ProcessException(tempCdd.getExamIndex()+ " Assigned to "
                    + tempTable.toString(), ProcessException.MESSAGE_TOAST,
                    IconManager.ASSIGNED);
            tempCdd = null;
            tempTable = null;
            throw err;
        }
    }

    /**
     * checkTable()
     *
     * This method register the table to the assignment
     *
     * @param scanString    Possible Table Number in the venue
     */
    public void checkTable(String scanString){
        //Add checking mechanism when venue size is valid
        tempTable = Integer.parseInt(scanString);
    }

    /**
     * checkCandidate()
     *
     * This method check the input scanString
     *
     *  If scanString is a Candidate ID
     *  It register the Candidate to the buffer tempCdd
     *  else error will be thrown
     *
     * @param scanString            Possible Register Number of candidate
     * @throws ProcessException
     */
    public void checkCandidate(String scanString) throws ProcessException {
        if(scanString == null){
            throw new ProcessException("Scanning a null value", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }

        if(attdList == null || attdList.getAttendanceList() == null)
            throw new ProcessException("No Attendance List", ProcessException.MESSAGE_DIALOG,
                    IconManager.WARNING);

        Candidate candidate = attdList.getCandidate(scanString);

        if(candidate == null){
            throw new ProcessException(scanString + " doest not belong to this venue",
                    ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        } else {
            if(candidate.getStatus() == Status.EXEMPTED){
                throw new ProcessException("The paper was exempted for " + candidate.getExamIndex(),
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }
            if(candidate.getStatus() == Status.BARRED){
                throw new ProcessException(candidate.getExamIndex() + " have been barred",
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }
            if(candidate.getStatus() == Status.QUARANTIZED){
                throw new ProcessException("The paper was quarantized for "
                        + candidate.getExamIndex(),
                        ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
            }
        }

        tempCdd = candidate;
    }

    /**
     * tryAssignCandidate()
     *
     * This method check if the the buffer for table and buffer for candidate
     * were registered. If both are registered and capable to form a valid set,
     * The attendance will be taken and return a true
     *
     * If it failed half way, this method return false and do nothing.
     *
     * @return  the flag of registered the set of table and candidate or failed
     */
    public boolean tryAssignCandidate() throws ProcessException {
        boolean assigned = false;

        if(tempTable != null && tempCdd != null){
            //If ExamSubject range does not meet, DO something
            attempReassign();
            attempInvalidSeat();

            assignCandidate();
            assigned    = true;
        }

        return assigned;
    }

    public void attempReassign() throws ProcessException{
        ProcessException err;
        if(assgnList.containsKey(tempTable)){
            err = new ProcessException("Previous: Table " + tempTable + " assigned to "
                    + attdList.getCandidate(assgnList.get(tempTable)).getExamIndex()
                    + "\nNew: Table " + tempTable + " assign to " + tempCdd.getExamIndex(),
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);
            err.setListener(ProcessException.updateButton, updateListener);
            err.setListener(ProcessException.cancelButton, cancelListener);
            throw err;
        }

        if(assgnList.containsValue(tempCdd.getRegNum())){
            err = new ProcessException("Previous: " + tempCdd.getExamIndex()
                    + " assigned to Table "
                    + attdList.getCandidate(tempCdd.getRegNum()).getTableNumber()
                    + "\nNew: " + tempCdd.getExamIndex() + " assign to " + tempTable,
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);
            err.setListener(ProcessException.updateButton, updateListener);
            err.setListener(ProcessException.cancelButton, cancelListener);
            throw err;
        }
    }

    public void attempInvalidSeat() throws ProcessException{
        if(!tempCdd.getPaper().isValidTable(tempTable))
            throw new ProcessException(tempCdd.getExamIndex() + " should not sit here\n"
                    + "Suggest to Table " + tempCdd.getPaper().getStartTableNum(),
                    ProcessException.MESSAGE_TOAST, IconManager.WARNING);
    }

    public void assignCandidate(){
        tempCdd.setTableNumber(tempTable);
        tempCdd.setStatus(Status.PRESENT);

        attdList.removeCandidate(tempCdd.getRegNum());
        attdList.addCandidate(tempCdd, tempCdd.getPaperCode(), tempCdd.getStatus(),
                tempCdd.getProgramme());
        assgnList.put(tempTable, tempCdd.getRegNum());

        assignAct.clearView();
    }

    //= Methods for abnormal cases =================================================================
    //  updateNewCandidae()
    //  - Replace previously assigned Table Candidate set with New Table Candidate set
    //
    //  cancelNewAssign()
    //  - Remain previously assigned Table Candidate set and discard New Table Candidate set
    //
    //  resetCandidate()
    //  - Remove away the Candidate
    public void updateNewCandidate() {
        if(assgnList.containsKey(tempTable)){
            //Table reassign, reset the previous assigned candidate in the list to ABSENT
            Candidate cdd = attdList.getCandidate(assgnList.get(tempTable));
            attdList.removeCandidate(cdd.getRegNum());
            cdd.setTableNumber(0);
            cdd.setStatus(Status.ABSENT);
            attdList.addCandidate(cdd, cdd.getPaperCode(), cdd.getStatus(), cdd.getProgramme());
            assgnList.remove(tempTable);
        } else {
            //Candidate reassign, remove the previously assignment
            assgnList.remove(tempCdd.getTableNumber());
        }

        tempCdd.setStatus(Status.PRESENT);
        tempCdd.setTableNumber(tempTable);

        attdList.removeCandidate(tempCdd.getRegNum());
        attdList.addCandidate(tempCdd, tempCdd.getPaperCode(), tempCdd.getStatus(),
                tempCdd.getProgramme());
        assgnList.put(tempTable, tempCdd.getRegNum());

        tempCdd     = null;
        tempTable   = null;
    }

    public void cancelNewAssign(){
        tempCdd     = null;
        tempTable   = null;
    }
}

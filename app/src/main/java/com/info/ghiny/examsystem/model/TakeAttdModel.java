package com.info.ghiny.examsystem.model;

import android.content.DialogInterface;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.TakeAttdMVP;

import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 08/08/2016.
 */
public class TakeAttdModel implements TakeAttdMVP.Model {
    private HashMap<Integer, String> assgnList;
    private static AttendanceList attdList;
    private TakeAttdMVP.MPresenter taskPresenter;
    private CheckListLoader dbLoader;

    private boolean tagNextLate;
    private boolean isDownloadComplete;
    private boolean initialized;
    private Candidate tempCdd    = null;
    private Integer tempTable    = null;

    public TakeAttdModel(TakeAttdMVP.MPresenter taskPresenter, CheckListLoader dbLoader){
        this.taskPresenter      = taskPresenter;
        this.dbLoader           = dbLoader;
        this.initialized        = false;
        this.isDownloadComplete = false;
        this.tagNextLate        = false;
        this.assgnList          = new HashMap<>();
    }

    void setInitialized(boolean initialized){
        this.initialized    = initialized;
    }

    void setDownloadComplete(boolean downloadComplete) {
        isDownloadComplete = downloadComplete;
    }

    boolean isDownloadComplete() {
        return isDownloadComplete;
    }

    boolean isTagNextLate() {
        return tagNextLate;
    }

    void setTagNextLate(boolean tagNextLate) {
        this.tagNextLate = tagNextLate;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    //==============================================================================================
    @Override
    public void initAttendance() throws ProcessException {
        if(dbLoader.emptyAttdInDB() || dbLoader.emptyPapersInDB()){
            isDownloadComplete = false;
            ExternalDbLoader.dlAttendanceList();
        } else {
            attdList    = dbLoader.queryAttendanceList();
            Candidate.setPaperList(dbLoader.queryPapers());
            updateAssignList();
            initialized = true;
        }
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!LoginModel.getStaff().matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    @Override
    public void checkDownloadResult(String chiefMessage) throws ProcessException {
        isDownloadComplete  = true;
        attdList    = JsonHelper.parseAttdList(chiefMessage);
        Candidate.setPaperList(JsonHelper.parsePaperMap(chiefMessage));
        initialized = true;
        throw new ProcessException("Download Complete", ProcessException.MESSAGE_TOAST,
                IconManager.MESSAGE);
    }

    @Override
    public void saveAttendance() {
        dbLoader.saveAttendanceList(attdList);
        dbLoader.savePaperList(Candidate.getPaperList());
    }

    @Override
    public void updateAssignList() throws ProcessException{
        assgnList.clear();

        if(attdList == null){
            if(initialized)
                throw new ProcessException("Attendance List is not initialize",
                        ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        } else {
            List<String> cddList    = attdList.getAllCandidateRegNumList();

            for(int i=0; i < cddList.size(); i++){
                Candidate cdd   = attdList.getCandidate(cddList.get(i));
                if(cdd.getStatus() == Status.PRESENT){
                    assgnList.put(cdd.getTableNumber(), cdd.getRegNum());
                }
            }
        }
    }

    @Override
    public void tryAssignScanValue(String scanStr) throws ProcessException{

        if(scanStr == null){
            throw new ProcessException("Scanning a null value", ProcessException.MESSAGE_TOAST,
                    IconManager.WARNING);
        }

        if(tempCdd != null && tempTable != null) {
            if(verifyCandidate(scanStr)) {
                this.tempTable   = null;
                this.taskPresenter.notifyDisplayReset();
                this.taskPresenter.notifyCandidateScanned(tempCdd);
            } else if(verifyTable(scanStr)) {
                this.tempCdd     = null;
                this.taskPresenter.notifyDisplayReset();
                this.taskPresenter.notifyTableScanned(tempTable);
            } else {
                throw new ProcessException("Not a valid QR", ProcessException.MESSAGE_TOAST,
                        IconManager.MESSAGE);
            }
        } else {
            if(verifyCandidate(scanStr)) {
                this.taskPresenter.notifyCandidateScanned(tempCdd);
            } else if(verifyTable(scanStr)) {
                this.taskPresenter.notifyTableScanned(tempTable);
            } else {
                throw new ProcessException("Not a valid QR", ProcessException.MESSAGE_TOAST,
                        IconManager.MESSAGE);
            }

            if(tryAssignCandidate()){

                ProcessException err = new ProcessException(tempCdd.getExamIndex()+ " Assigned to "
                        + tempTable.toString(), ProcessException.MESSAGE_TOAST,
                        IconManager.ASSIGNED);

                throw err;
            }
        }
    }

    @Override
    public void run() {
        try{
            if(!isDownloadComplete && !initialized) {
                ProcessException err = new ProcessException(
                        "Download failed. Response times out.",
                        ProcessException.MESSAGE_DIALOG, IconManager.MESSAGE);
                err.setListener(ProcessException.okayButton, taskPresenter);
                err.setBackPressListener(taskPresenter);
                throw err;
            }
        } catch (ProcessException err){
            taskPresenter.onTimesOut(err);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                updateNewAssignment();
                break;
            default:
                cancelNewAssign();
                break;
        }
        dialog.cancel();
    }

    @Override
    public void tagAsLateNot() {
        if(tempCdd == null) {
            tagNextLate = !tagNextLate;
            taskPresenter.notifyTagUntag(tagNextLate);
        } else {
            tempCdd.setLate((!tempCdd.isLate()));
            taskPresenter.notifyTagUntag(tempCdd.isLate());
        }
    }

    //= Methods for abnormal cases =================================================================

    @Override
    public void updateNewAssignment() {
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
    }

    @Override
    public void cancelNewAssign(){
        tempCdd     = null;
        tempTable   = null;
        taskPresenter.notifyDisplayReset();
    }

    @Override
    public void resetAttendanceAssignment() {
        if(this.tempCdd != null && this.tempTable != null){
            assgnList.remove(tempTable);
            tempCdd.setStatus(Status.ABSENT);
            tempCdd.setTableNumber(0);
            attdList.removeCandidate(tempCdd.getRegNum());
            attdList.addCandidate(tempCdd, tempCdd.getPaperCode(),
                    tempCdd.getStatus(), tempCdd.getProgramme());
        }
        this.tempTable  = null;
        this.tempCdd    = null;
        this.taskPresenter.notifyDisplayReset();
    }

    //= Setter & Getter ============================================================================
    //For test purposes=============================================================================
    void setTempCdd(Candidate tempCdd) {
        this.tempCdd = tempCdd;
    }

    Candidate getTempCdd() {
        return tempCdd;
    }

    public static void setAttdList(AttendanceList attdList) {
        TakeAttdModel.attdList = attdList;
    }

    public static AttendanceList getAttdList() {
        return attdList;
    }

    void setAssgnList(HashMap<Integer, String> assgnList) {
        this.assgnList = assgnList;
    }

    HashMap<Integer, String> getAssgnList() {
        return assgnList;
    }

    void setTempTable(Integer tempTable) {
        this.tempTable = tempTable;
    }

    Integer getTempTable() {
        return tempTable;
    }

    //= Method for Assign process ==================================================================
    /**
     * checkTable()
     *
     * NOT FINISH YET - TODO: add checking mechanism for venue size
     * This method register the table to the assignment
     *
     * @param scanString    Possible Table Number in the venue
     */
    void checkTable(String scanString) throws ProcessException{
        int length  = scanString.length();

        for(int i = 0; i < length; i++){
            if(!Character.isDigit(scanString.charAt(i))){
                throw new ProcessException("Not a valid QR code", ProcessException.MESSAGE_TOAST,
                        IconManager.WARNING);
            }
        }

        this.tempTable = Integer.parseInt(scanString);
        this.taskPresenter.notifyTableScanned(tempTable);
        if(this.assgnList.containsKey(this.tempTable)){
            this.taskPresenter.notifyReassign(TakeAttdMVP.TABLE_REASSIGN);
        }
    }

    boolean verifyTable(String scanStr) throws ProcessException{
        int length  = scanStr.length();
        if(length > 0 && length < 5){
            for(int i = 0; i < length; i++){
                if(!Character.isDigit(scanStr.charAt(i))){
                    return false;
                }
            }

            this.tempTable = Integer.parseInt(scanStr);
            if(this.assgnList.containsKey(this.tempTable)){
                this.taskPresenter.notifyReassign(TakeAttdMVP.TABLE_REASSIGN);
            }
            return true;
        }
        return false;
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
    void checkCandidate(String scanString) throws ProcessException {
        ProcessException err;

        if(attdList == null || attdList.getAttendanceList() == null){
            err = new ProcessException("No Attendance List",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
            err.setListener(ProcessException.okayButton, taskPresenter);
            throw err;
        }

        Candidate candidate = attdList.getCandidate(scanString);

        if(candidate == null){
            throw new ProcessException(scanString + " doest not belong to this venue",
                    ProcessException.MESSAGE_TOAST, IconManager.WARNING);
        } else {
            inspectCandidateEligibility(candidate);
        }

        this.tempCdd = candidate;
        this.taskPresenter.notifyCandidateScanned(tempCdd);
        if(this.assgnList.containsValue(this.tempCdd.getRegNum())){
            this.taskPresenter.notifyReassign(TakeAttdMVP.CANDIDATE_REASSIGN);
        }
    }

    boolean verifyCandidate(String scanStr) throws ProcessException {
        ProcessException err;
        if(scanStr.length() == 10){
            if(attdList == null || attdList.getAttendanceList() == null){
                err = new ProcessException("No Attendance List",
                        ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
                err.setListener(ProcessException.okayButton, taskPresenter);
                throw err;
            }

            Candidate cdd = attdList.getCandidate(scanStr);

            if(cdd == null){
                throw new ProcessException(scanStr + " doest not belong to this venue",
                        ProcessException.MESSAGE_TOAST, IconManager.WARNING);
            } else {
                inspectCandidateEligibility(cdd);
            }

            if(tagNextLate){
                cdd.setLate(true);
                this.tagNextLate = false;
            }

            this.tempCdd = cdd;
            if(this.assgnList.containsValue(this.tempCdd.getRegNum())){
                this.taskPresenter.notifyReassign(TakeAttdMVP.CANDIDATE_REASSIGN);
            }
            return true;
        }
        return false;
    }

    private void inspectCandidateEligibility(Candidate cdd) throws ProcessException {
        if(cdd.getStatus() == Status.EXEMPTED){
            throw new ProcessException("The paper was exempted for " + cdd.getExamIndex(),
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        }
        if(cdd.getStatus() == Status.BARRED){
            throw new ProcessException(cdd.getExamIndex() + " have been barred",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        }
        if(cdd.getStatus() == Status.QUARANTINED){
            throw new ProcessException("The paper was quarantized for "
                    + cdd.getExamIndex(),
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
        }
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
    boolean tryAssignCandidate() throws ProcessException {
        boolean assigned = false;

        if(tempTable != null && tempCdd != null){
            attemptNotMatch();
            attemptReassign();

            assignCandidate();
            assigned    = true;
        }

        return assigned;
    }

    void attemptReassign() throws ProcessException{
        ProcessException err;
        if(assgnList.containsKey(tempTable)){
            if(!assgnList.get(tempTable).equals(tempCdd.getRegNum())) {
                err = new ProcessException("Previous: Table " + tempTable + " assigned to "
                        + attdList.getCandidate(assgnList.get(tempTable)).getExamIndex()
                        + "\nNew: Table " + tempTable + " assign to " + tempCdd.getExamIndex(),
                        ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);
                err.setListener(ProcessException.updateButton, this);
                err.setListener(ProcessException.cancelButton, this);
                throw err;
            }
        } else if(assgnList.containsValue(tempCdd.getRegNum())){
            err = new ProcessException("Previous: " + tempCdd.getExamIndex()
                    + " assigned to Table "
                    + attdList.getCandidate(tempCdd.getRegNum()).getTableNumber()
                    + "\nNew: " + tempCdd.getExamIndex() + " assign to " + tempTable,
                    ProcessException.UPDATE_PROMPT, IconManager.MESSAGE);
            err.setListener(ProcessException.updateButton, this);
            err.setListener(ProcessException.cancelButton, this);
            throw err;
        }
    }

    void attemptNotMatch() throws ProcessException{
        if(!tempCdd.getPaper().isValidTable(tempTable))
            throw new ProcessException(tempCdd.getExamIndex() + " should not sit here\n"
                    + "Suggest to Table " + tempCdd.getPaper().getStartTableNum(),
                    ProcessException.MESSAGE_TOAST, IconManager.WARNING);
    }

    void assignCandidate(){
        tempCdd.setTableNumber(tempTable);
        tempCdd.setStatus(Status.PRESENT);

        attdList.removeCandidate(tempCdd.getRegNum());
        attdList.addCandidate(tempCdd, tempCdd.getPaperCode(), tempCdd.getStatus(),
                tempCdd.getProgramme());
        assgnList.put(tempTable, tempCdd.getRegNum());
    }
}

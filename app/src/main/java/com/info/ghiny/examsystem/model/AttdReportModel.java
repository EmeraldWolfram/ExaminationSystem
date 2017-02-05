package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.AttdReportMVP;
import com.info.ghiny.examsystem.manager.IconManager;
import com.info.ghiny.examsystem.view_holder.ProgrammeDisplayHolder;
import com.info.ghiny.examsystem.view_holder.StatusDisplayHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by FOONG on 3/2/2017.
 */

public class AttdReportModel implements AttdReportMVP.MvpModel {

    private AttdReportMVP.MvpMPresenter taskPresenter;
    private AttendanceList attendanceList;
    private boolean sent;
    private StaffIdentity user;

    public AttdReportModel(AttdReportMVP.MvpMPresenter taskPresenter){
        this.taskPresenter  = taskPresenter;
        this.attendanceList = TakeAttdModel.getAttdList();
        this.sent           = false;
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
        if(uploaded){
            throw new ProcessException("Submission successful",
                    ProcessException.MESSAGE_DIALOG, IconManager.ASSIGNED);
        } else {
            throw new ProcessException("Submission denied by Chief",
                    ProcessException.MESSAGE_DIALOG, IconManager.WARNING);
        }
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }

    private List<StatusDisplayHolder> getDisplayBody(String programme) {

        List<StatusDisplayHolder> body  = new ArrayList<>();

        body.add(new StatusDisplayHolder(Status.PRESENT,
                attendanceList.getNumberOfCandidates(Status.PRESENT, programme)));
        body.add(new StatusDisplayHolder(Status.ABSENT,
                attendanceList.getNumberOfCandidates(Status.ABSENT, programme)));
        body.add(new StatusDisplayHolder(Status.BARRED,
                attendanceList.getNumberOfCandidates(Status.BARRED, programme)));
        body.add(new StatusDisplayHolder(Status.EXEMPTED,
                attendanceList.getNumberOfCandidates(Status.EXEMPTED, programme)));
        body.add(new StatusDisplayHolder(Status.QUARANTINED,
                attendanceList.getNumberOfCandidates(Status.QUARANTINED, programme)));

        return body;
    }

    @Override
    public HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>> getDisplayMap(){
        HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>> displaysMap = new HashMap<>();
        List<ProgrammeDisplayHolder> header = getDisplayHeader();
        List<StatusDisplayHolder> tempBody;

        for(int i = 0; i < header.size(); i++){
            tempBody    = getDisplayBody(header.get(i).getProgramme());
            displaysMap.put(header.get(i), tempBody);
        }

        return displaysMap;
    }

    @Override
    public List<ProgrammeDisplayHolder> getDisplayHeader() {
        List<String> programmeList  = attendanceList.getProgrammeList();
        List<ProgrammeDisplayHolder> displayHolders = new ArrayList<>();

        for(int i = 0; i < programmeList.size(); i++){
            String tempPrg  = programmeList.get(i);
            int tempTotal   = 0;

            for(Status status   :   attendanceList.getAttendanceList().keySet()){
                tempTotal   += attendanceList.getNumberOfCandidates(status, tempPrg);
            }

            displayHolders.add(new ProgrammeDisplayHolder(tempPrg, tempTotal));
        }


        return displayHolders;
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

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

    private ArrayList<StatusDisplayHolder> getDisplayBody(String programme) {

        ArrayList<StatusDisplayHolder> body  = new ArrayList<>();

        int presentNum  = attendanceList.getNumberOfCandidates(Status.PRESENT, programme);
        int absentNum   = attendanceList.getNumberOfCandidates(Status.ABSENT, programme);
        int barredNum   = attendanceList.getNumberOfCandidates(Status.BARRED, programme);
        int exemptNum   = attendanceList.getNumberOfCandidates(Status.EXEMPTED, programme);
        int quaranNum   = attendanceList.getNumberOfCandidates(Status.QUARANTINED, programme);

        body.add(new StatusDisplayHolder(Status.PRESENT, presentNum));
        body.add(new StatusDisplayHolder(Status.ABSENT, absentNum));
        body.add(new StatusDisplayHolder(Status.BARRED, barredNum));
        body.add(new StatusDisplayHolder(Status.EXEMPTED, exemptNum));
        body.add(new StatusDisplayHolder(Status.QUARANTINED, quaranNum));

        body.add(new StatusDisplayHolder(Status.TOTAL,
                presentNum + absentNum + barredNum + exemptNum + quaranNum));

        return body;
    }

    @Override
    public HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>> getDisplayMap(List<ProgrammeDisplayHolder> header){
        HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>> displaysMap = new HashMap<>();
        List<StatusDisplayHolder> tempBody;

        for(int i = 0; i < (header.size() - 1); i++){
            tempBody    = getDisplayBody(header.get(i).getProgramme());
            displaysMap.put(header.get(i), tempBody);
        }


        List<StatusDisplayHolder> summary   = new ArrayList<>();
        summary.add(new StatusDisplayHolder(Status.PRESENT, attendanceList.getNumberOfCandidates(Status.PRESENT)));
        summary.add(new StatusDisplayHolder(Status.ABSENT, attendanceList.getNumberOfCandidates(Status.ABSENT)));
        summary.add(new StatusDisplayHolder(Status.BARRED, attendanceList.getNumberOfCandidates(Status.BARRED)));
        summary.add(new StatusDisplayHolder(Status.EXEMPTED, attendanceList.getNumberOfCandidates(Status.EXEMPTED)));
        summary.add(new StatusDisplayHolder(Status.QUARANTINED, attendanceList.getNumberOfCandidates(Status.QUARANTINED)));
        summary.add(new StatusDisplayHolder(Status.TOTAL, attendanceList.getTotalNumberOfCandidates()));
        displaysMap.put(header.get(header.size() - 1), summary);

        return displaysMap;
    }

    @Override
    public List<ProgrammeDisplayHolder> getDisplayHeader() {
        List<String> programmeList  = attendanceList.getProgrammeList();
        ArrayList<ProgrammeDisplayHolder> displayHolders = new ArrayList<>();

        for(int i = 0; i < programmeList.size(); i++){
            String tempPrg  = programmeList.get(i);
            int tempTotal   = 0;

            for(Status status   :   attendanceList.getAttendanceList().keySet()){
                tempTotal   += attendanceList.getNumberOfCandidates(status, tempPrg);
            }

            displayHolders.add(new ProgrammeDisplayHolder(tempPrg, tempTotal));
        }

        displayHolders.add(new ProgrammeDisplayHolder("TOTAL", attendanceList.getTotalNumberOfCandidates()));

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

package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.InfoDisplayMVP;
import com.info.ghiny.examsystem.manager.IconManager;

import java.util.ArrayList;
import java.util.Calendar;
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

public class InfoDisplayModel implements InfoDisplayMVP.Model {

    private List<ExamSubject> papers;
    private StaffIdentity user;

    public InfoDisplayModel(){
        papers  = new ArrayList<>();
        user    = LoginModel.getStaff();
    }

    public void setPapers(List<ExamSubject> papers) {
        this.papers = papers;
    }

    @Override
    public void updateSubjects(String messageRx) throws ProcessException {
        papers.clear();
        List<ExamSubject> subjects  = JsonHelper.parsePaperList(messageRx);
        papers.addAll(subjects);
    }

    @Override
    public ExamSubject getSubjectAt(int position) {
        return papers.get(position);
    }

    @Override
    public int getDaysLeft(Calendar examTime) {
        Calendar today = Calendar.getInstance();
        Integer numberOfDay;

        numberOfDay = examTime.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);

        if(numberOfDay == 0 && examTime.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            numberOfDay = 0;
        } else if(today.after(examTime)){
            numberOfDay = -1;
        } else {
            if(today.get(Calendar.YEAR) < examTime.get(Calendar.YEAR)){
                int yearDiff = examTime.get(Calendar.YEAR) - today.get(Calendar.YEAR);
                numberOfDay = examTime.get(Calendar.DAY_OF_YEAR)
                        + (int)(yearDiff * 365.25) - today.get(Calendar.DAY_OF_YEAR);
            } else {
                numberOfDay = examTime.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            }
        }
        return numberOfDay;
    }

    @Override
    public int getNumberOfSubject() {
        return papers.size();
    }

    @Override
    public void matchPassword(String password) throws ProcessException {
        if(!user.matchPassword(password))
            throw new ProcessException("Access denied. Incorrect Password",
                    ProcessException.MESSAGE_TOAST, IconManager.MESSAGE);
    }
}

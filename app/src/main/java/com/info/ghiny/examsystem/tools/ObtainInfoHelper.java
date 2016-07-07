package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.database.ExamDatabaseLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by GhinY on 01/07/2016.
 */
public class ObtainInfoHelper {
    private static ExamDatabaseLoader examDBLoader;

    public static void setExamDBLoader(ExamDatabaseLoader examDBLoader) {
        ObtainInfoHelper.examDBLoader = examDBLoader;
    }

    public static List<ExamSubject> getCandidatePapers(String scanValue) throws ProcessException{
        //List<ExamSubject> subjects = new ArrayList<>();

        //StaffIdentity id = examDBLoader.getIdentity(scanValue);
        //if(id == null)
        //    throw new ProcessException("Not an StaffIdentity", ProcessException.MESSAGE_TOAST,
        //            IconManager.WARNING);

        //List<String> papers = examDBLoader.getPapersExamine(id.getRegNum());

        List<ExamSubject> subjects = ExternalDbLoader.getPapersExamineByCdd(scanValue);
        if(subjects == null){
            throw new ProcessException("Not a candidate ID", ProcessException.MESSAGE_TOAST,
                    IconManager.MESSAGE);
        }
        //for(int i = 0; i < papers.size(); i++){
        //    ExamSubject subject = examDBLoader.getPaperInfo(papers.get(i));
        //    subjects.add(subject);
        //}

        return subjects;
    }

    public static Integer getDaysLeft(Calendar paperDate) {
        Calendar today = Calendar.getInstance();
        Integer numberOfDay;

        numberOfDay = paperDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);

        if(numberOfDay == 0 && paperDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            numberOfDay = 0;
        } else if(today.after(paperDate)){
            numberOfDay = -1;
        } else {
            if(today.get(Calendar.YEAR) < paperDate.get(Calendar.YEAR)){
                int yearDiff = paperDate.get(Calendar.YEAR) - today.get(Calendar.YEAR);
                numberOfDay = paperDate.get(Calendar.DAY_OF_YEAR)
                        + (int)(yearDiff * 365.25) - today.get(Calendar.DAY_OF_YEAR);
            } else {
                numberOfDay = paperDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            }
        }
        return numberOfDay;
    }

}

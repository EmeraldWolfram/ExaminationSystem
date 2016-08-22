package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;

import java.util.Calendar;

/**
 * Created by GhinY on 01/07/2016.
 */
public class InfoCollectHelper {

    public void bundleCollection(String scanValue) throws ProcessException{
        ChiefLink.setCompleteFlag(false);
        ExternalDbLoader.acknowledgeCollection(scanValue);
    }

    public void reqCandidatePapers(String scanValue) throws ProcessException{

        if(scanValue.length() != 10)
            throw new ProcessException("Not a candidate ID", ProcessException.MESSAGE_TOAST,
                    IconManager.MESSAGE);

        ChiefLink.setCompleteFlag(false);
        ExternalDbLoader.getPapersExamineByCdd(scanValue);
    }

    public Integer getDaysLeft(Calendar paperDate) {
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

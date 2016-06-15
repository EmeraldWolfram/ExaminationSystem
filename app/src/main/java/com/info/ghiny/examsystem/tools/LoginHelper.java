package com.info.ghiny.examsystem.tools;

import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.Identity;

/**
 * Created by GhinY on 15/06/2016.
 */
public class LoginHelper {

    public static void checkInvigilator(Identity invglt) throws CustomException{
        if(invglt == null){
            throw new CustomException("ID is null", CustomException.ERR_NULL_IDENTITY);
        } else {
            if(!invglt.getEligible())
                throw new CustomException("ID not eligible", CustomException.ERR_ILLEGAL_IDENTITY);
        }
    }

}

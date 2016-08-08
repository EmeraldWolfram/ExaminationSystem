package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.R;

/**
 * Created by GhinY on 17/06/2016.
 */
public class IconManager {
    public static final int WARNING     = 0;
    public static final int MESSAGE     = 1;
    public static final int ASSIGNED    = 2;

    public IconManager(){}

    public int getIcon(int iconType){
        int icon = R.drawable.warn_icon;    //WARNING is default
        if(iconType == MESSAGE)
            icon    = R.drawable.msg_icon;

        if(iconType == ASSIGNED)
            icon    = R.drawable.entry_icon;

        return icon;
    }
}

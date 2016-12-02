package com.info.ghiny.examsystem.manager;

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
        int icon;
        switch (iconType){
            case MESSAGE:
                icon    = R.drawable.other_msg_icon;
                break;
            case ASSIGNED:
                icon    = R.drawable.other_entry_icon_2;
                break;
            default:
                icon = R.drawable.other_warn_icon;
        }
        return icon;
    }

}

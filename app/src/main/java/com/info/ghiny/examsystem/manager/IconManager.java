package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.R;

/**
 * Created by GhinY on 17/06/2016.
 */
public class IconManager {
    public static final int WARNING     = 0;
    public static final int MESSAGE     = 1;
    public static final int ASSIGNED    = 2;

    public static final int SWIPE_ICON_RETAKE   = 3;
    public static final int SWIPE_ICON_TRASH    = 4;

    public IconManager(){}

    public int getIcon(int iconType){
        int icon;
        switch (iconType){
            case MESSAGE:
                icon    = R.drawable.msg_icon;
                break;
            case ASSIGNED:
                icon    = R.drawable.entry_icon_2;
                break;
            case SWIPE_ICON_RETAKE:
                icon    = R.drawable.scroll_with_tick_icon;
                break;
            case SWIPE_ICON_TRASH:
                icon    = R.drawable.trash_icon;
                break;
            default:
                icon = R.drawable.warn_icon;
        }
        return icon;
    }

}

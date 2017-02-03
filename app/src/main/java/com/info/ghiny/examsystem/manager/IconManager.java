package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.R;

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

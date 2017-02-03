package com.info.ghiny.examsystem.manager;

import android.content.Context;
import android.graphics.Typeface;
import android.renderscript.Type;

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
public class ConfigManager {
    public static String DEFAULT_FONT = "fonts/DroidSerif-Regular.ttf";
    public static String MESSAGE_FONT = "";
    public static String THICK_FONT = "fonts/Chunkfive.otf";
    public static String BOLD_FONT = "fonts/Oswald-Bold.ttf";

    public static void setBoldFont(String boldFont) {
        BOLD_FONT = boldFont;
    }

    public static void setDefaultFont(String defaultFont) {
        DEFAULT_FONT = defaultFont;
    }

    public static void setMessageFont(String messageFont) {
        MESSAGE_FONT = messageFont;
    }

    public static void setThickFont(String thickFont) {
        THICK_FONT = thickFont;
    }

    private Context context;

    public ConfigManager(Context context){
        this.context    = context;
    }

    public Typeface getTypeface(String font){
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}

package com.info.ghiny.examsystem.manager;

import android.content.Context;
import android.graphics.Typeface;
import android.renderscript.Type;

/**
 * Created by GhinY on 05/08/2016.
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

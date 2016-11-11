package com.info.ghiny.examsystem.database;

import java.util.Locale;

/**
 * Created by user09 on 11/9/2016.
 */

public class PaperBundle {
    public static final String BUNDLE_ID    = "Id";
    public static final String BUNDLE_VENUE = "Venue";
    public static final String BUNDLE_PROG  = "Programme";
    public static final String BUNDLE_PAPER = "PaperCode";

    private String colId;
    private String colVenue;
    private String colPaperCode;
    private String colProgramme;

    public PaperBundle(){}

    public String getColId() {
        return colId;
    }

    public String getColPaperCode() {
        return colPaperCode;
    }

    public String getColProgramme() {
        return colProgramme;
    }

    public String getColVenue() {
        return colVenue;
    }

    public boolean parseBundle(String bundleStr){
        String[] bundleInfo = bundleStr.split("/");

        if(bundleInfo.length != 4){
            return false;
        } else {
            colId           = bundleInfo[0];
            colVenue        = bundleInfo[1];
            colPaperCode    = bundleInfo[2];
            colProgramme    = bundleInfo[3];
            return true;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s/%s/%s/%s", colId, colVenue, colPaperCode, colProgramme);
    }
}

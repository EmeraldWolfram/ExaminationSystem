package com.info.ghiny.examsystem.database;

import java.util.Locale;

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

public class PaperBundle {
    public static final String BUNDLE_ID    = "BundleId";
    public static final String BUNDLE_VENUE = "BundleVenue";
    public static final String BUNDLE_PROG  = "BundleProgramme";
    public static final String BUNDLE_PAPER = "BundlePaperCode";

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

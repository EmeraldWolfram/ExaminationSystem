package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.database.Candidate;

import java.util.Comparator;
import java.util.Map;

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

public class SortManager {
    public enum SortMethod {
        GROUP_PAPER_GROUP_PROGRAM_SORT_ID,
        GROUP_PAPER_GROUP_PROGRAM_SORT_NAME,
        GROUP_PAPER_GROUP_PROGRAM_SORT_TABLE,
        GROUP_PAPER_SORT_ID,
        GROUP_PAPER_SORT_NAME
    }

    private Comparator<Candidate> grpProgSortId;
    private Comparator<Candidate> grpProgSortName;
    private Comparator<Candidate> grpProgSortTable;
    private Comparator<Candidate> noGrpSortId;
    private Comparator<Candidate> noGrpSortName;

    public SortManager(){
        grpProgSortId = new Comparator<Candidate>() {
            @Override
            public int compare(Candidate lhs, Candidate rhs) {
                int check;
                check = lhs.getPaperCode().compareTo(rhs.getPaperCode());
                if(check != 0){
                    return check;
                }
                check = lhs.getProgramme().compareTo(rhs.getProgramme());
                if(check != 0){
                    return check;
                }
                check   =  lhs.getRegNum().compareTo(rhs.getRegNum());
                if(check != 0){
                    return check;
                }
                return -1;
            }
        };

        grpProgSortName = new Comparator<Candidate>() {
            @Override
            public int compare(Candidate lhs, Candidate rhs) {
                int check;
                check = lhs.getPaperCode().compareTo(rhs.getPaperCode());
                if(check != 0){
                    return check;
                }
                check = lhs.getProgramme().compareTo(rhs.getProgramme());
                if(check != 0){
                    return check;
                }
                check = lhs.getExamIndex().compareTo(rhs.getExamIndex());
                if(check != 0){
                    return check;
                }
                check = lhs.getRegNum().compareTo(rhs.getRegNum());
                if(check != 0){
                    return check;
                }
                return -1;
            }
        };

        grpProgSortTable = new Comparator<Candidate>() {
            @Override
            public int compare(Candidate lhs, Candidate rhs) {
                int table = lhs.getTableNumber().compareTo(rhs.getTableNumber());
                if(table != 0) {
                    return table;
                }
                int check = lhs.getRegNum().compareTo(rhs.getRegNum());
                if(check != 0){
                    return check;
                }
                return -1;
            }
        };

        noGrpSortId = new Comparator<Candidate>() {
            @Override
            public int compare(Candidate lhs, Candidate rhs) {
                int check = lhs.getRegNum().compareTo(rhs.getRegNum());
                if(check != 0){
                    return check;
                }
                return -1;
            }
        };

        noGrpSortName = new Comparator<Candidate>() {
            @Override
            public int compare(Candidate lhs, Candidate rhs) {
                int check;
                check = lhs.getExamIndex().compareTo(rhs.getExamIndex());
                if(check != 0){
                    return check;
                }
                check = lhs.getRegNum().compareTo(rhs.getRegNum());
                if(check != 0){
                    return check;
                }
                return -1;
            }
        };
    }

    public Comparator<Candidate> getComparator(SortMethod sortMethod) {
        switch(sortMethod){
            case GROUP_PAPER_GROUP_PROGRAM_SORT_ID:
                return grpProgSortId;
            case GROUP_PAPER_GROUP_PROGRAM_SORT_NAME:
                return grpProgSortName;
            case GROUP_PAPER_GROUP_PROGRAM_SORT_TABLE:
                return grpProgSortTable;
            case GROUP_PAPER_SORT_ID:
                return noGrpSortId;
            case GROUP_PAPER_SORT_NAME:
                return noGrpSortName;
            default:
                return grpProgSortTable;
        }
    }
}

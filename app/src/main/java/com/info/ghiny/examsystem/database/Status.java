package com.info.ghiny.examsystem.database;

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

public enum Status {
    PRESENT,
    ABSENT,
    EXEMPTED,
    BARRED,
    QUARANTINED;

    @Override
    public String toString() {
        return super.toString();
    }

    public static Status parseStatus(String statusString){
        switch (statusString){
            case "PRESENT":
                return Status.PRESENT;
            case "BARRED":
                return Status.BARRED;
            case "EXEMPTED":
                return Status.EXEMPTED;
            case "QUARANTINED":
                return Status.QUARANTINED;
            default:
                return Status.ABSENT;
        }
    }
}

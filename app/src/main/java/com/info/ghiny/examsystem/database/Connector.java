package com.info.ghiny.examsystem.database;

import java.util.Calendar;
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

public class Connector {
    public static final String CONNECT_DB_ID    = "_id";
    public static final String CONNECT_IP       = "IP";
    public static final String CONNECT_PORT     = "Port";
    public static final String CONNECT_DATE     = "RegDate";
    public static final String CONNECT_SESSION  = "Session";
    public static final String CONNECT_MESSAGE  = "DuelMsg";

    private String ipAddress;
    private Integer portNumber;
    private Calendar date;
    private Session session;
    private String duelMessage;
    private Role myHost;

    public Connector(String ipAddress, Integer portNumber, String duelMessage){
        this.ipAddress     = ipAddress;
        this.portNumber    = portNumber;
        this.duelMessage   = duelMessage;
        this.date          = Calendar.getInstance();

        int hour            = this.date.get(Calendar.HOUR_OF_DAY);
        if(hour < 12){
            this.session    = Session.AM;
        } else if(hour > 17) {
            this.session    = Session.VM;
        } else {
            this.session    = Session.PM;
        }
    }

    public void setMyHost(Role myHost) {
        this.myHost = myHost;
    }

    public Role getMyHost() {
        return myHost;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDuelMessage(String duelMessage) {
        this.duelMessage = duelMessage;
    }

    public String getDuelMessage() {
        return duelMessage;
    }

    public String getDateInString(){
        int day, month, year;
        day     = this.date.get(Calendar.DAY_OF_MONTH);
        month   = this.date.get(Calendar.MONTH) + 1;
        year    = this.date.get(Calendar.YEAR);

        return String.format(Locale.ENGLISH, "%d:%d:%d", day, month, year);
    }

    public Calendar parseStringToDate(String date) {
        Calendar dbDate = null;
        try{
            String[] strArr   = date.split(":");
            if(strArr.length == 3){
                dbDate = Calendar.getInstance();

                int day     = Integer.parseInt(strArr[0]);
                int month   = Integer.parseInt(strArr[1]) - 1;
                int year    = Integer.parseInt(strArr[2]);

                dbDate.set(year, month, day);
            }
        } catch (Exception err) {
            dbDate = null;
        }

        return dbDate;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "$IN_CHARGE:"
                + ipAddress     + ":"
                + portNumber    + ":"
                + duelMessage   + ":$");
    }
}

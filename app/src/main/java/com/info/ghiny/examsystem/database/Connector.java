package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.model.IconManager;
import com.info.ghiny.examsystem.model.ProcessException;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by GhinY on 18/08/2016.
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

                int day     = Integer.parseInt(strArr[2]);
                int month   = Integer.parseInt(strArr[1]) - 1;
                int year    = Integer.parseInt(strArr[0]);

                dbDate.set(day, month, year);
            }
        } catch (Exception err) {
            dbDate = null;
        }

        return dbDate;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "$CHIEF:"
                + ipAddress     + ":"
                + portNumber    + ":"
                + duelMessage   + ":$");
    }
}

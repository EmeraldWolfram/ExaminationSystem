package com.info.ghiny.examsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 18/06/2016.
 */
public class JdbcDatabase {

    private static final int DB_VERSION = 1;
    private static final String PACKAGE = "com.info.ghiny.examsystem";
    private static final String DB_NAME = "CheckList.db";

    public static final String DRIVER  = "org.sqldroid.SQLDroidDriver";
    public static final String ADDRESS = "jdbc:sqldroid:/data/data/" + PACKAGE + "/" + DB_NAME;

    private static final String ATTENDANCE_TABLE        = "AttdTable";
    public static final String TABLE_INFO_ID            = "_id";
    public static final String TABLE_INFO_COLUMN_NAME   = "Name";
    public static final String TABLE_INFO_COLUMN_REGNUM = "RegNum";
    public static final String TABLE_INFO_COLUMN_STATUS = "Status";
    public static final String TABLE_INFO_COLUMN_CODE   = "Code";
    public static final String TABLE_INFO_COLUMN_PRG    = "Programme";
    public static final String TABLE_INFO_COLUMN_TABLE  = "TableNo";

    private String curDriver;
    private String curAddress;

    public JdbcDatabase(String driver, String url){
        curAddress  = url;
        curDriver   = driver;
    }

    public Connection estaConnection() throws Exception{
        Class.forName(curDriver);
        return DriverManager.getConnection(curAddress);
    }

    public void createTableIfNotExist() throws Exception{
        Connection con  = estaConnection();

        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + ATTENDANCE_TABLE  + "( "
                + TABLE_INFO_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_INFO_COLUMN_REGNUM  + " TEXT    NOT NULL, "
                + TABLE_INFO_COLUMN_NAME    + " TEXT    NOT NULL, "
                + TABLE_INFO_COLUMN_STATUS  + " TEXT    NOT NULL, "
                + TABLE_INFO_COLUMN_CODE    + " TEXT    NOT NULL, "
                + TABLE_INFO_COLUMN_PRG     + " TEXT    NOT NULL, "
                + TABLE_INFO_COLUMN_TABLE   + " INT     NOT NULL)";

        Statement stmt = con.createStatement();
        stmt.executeUpdate(CREATE_TABLE);

        stmt.close();
        con.close();
    }

    public void clearDatabase() throws Exception{
        Connection con = estaConnection();
        Statement stmt = con.createStatement();

        stmt.executeUpdate("DELETE FROM " + ATTENDANCE_TABLE);
        stmt.executeUpdate("VACUUM");

        stmt.close();
        con.close();
    }

    public void saveAttendanceList(AttendanceList attdList) throws Exception{
        //clearDatabase();
        Connection con = estaConnection();
        Statement stmt = con.createStatement();
        List<String> regNumList = attdList.getAllCandidateRegNumList();

        for(int i = 0; i < regNumList.size(); i++)
            saveAttendance(attdList.getCandidate(regNumList.get(i)), stmt);

        stmt.close();
        con.close();
    }

    public HashMap<AttendanceList.Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>>
    getLastSavedAttendanceList() throws Exception{
        HashMap<AttendanceList.Status, HashMap<String, HashMap<String, HashMap<String, Candidate>>>> map;
        Connection con = estaConnection();
        Statement stmt = con.createStatement();

        map = new HashMap<>();
        map.put(AttendanceList.Status.PRESENT, getPaperMap(AttendanceList.Status.PRESENT, stmt));
        //map.put(AttendanceList.Status.ABSENT, getPaperMap(AttendanceList.Status.ABSENT, stmt));
        //map.put(AttendanceList.Status.BARRED, getPaperMap(AttendanceList.Status.BARRED, stmt));
        //map.put(AttendanceList.Status.EXEMPTED, getPaperMap(AttendanceList.Status.EXEMPTED, stmt));

        stmt.close();
        con.close();
        return map;
    }

    public boolean isEmpty() throws Exception{
        Boolean status = true;

        Connection con = estaConnection();
        Statement stmt = con.createStatement();

        ResultSet ptr = stmt.executeQuery("SELECT * FROM " + ATTENDANCE_TABLE + ";");
        if(ptr.first())
            status = false;

        ptr.close();
        stmt.close();
        con.close();
        return status;
    }

    private void saveAttendance(Candidate cdd, Statement stmt) throws Exception{
        String SAVE_ATTENDANCE = "INSERT INTO "     + ATTENDANCE_TABLE
                + " (" + TABLE_INFO_COLUMN_NAME     + ", " + TABLE_INFO_COLUMN_REGNUM
                + ", " + TABLE_INFO_COLUMN_CODE     + ", " + TABLE_INFO_COLUMN_TABLE
                + ", " + TABLE_INFO_COLUMN_STATUS   + ", " + TABLE_INFO_COLUMN_PRG
                + ") VALUES ('";

        stmt.executeUpdate(SAVE_ATTENDANCE
                + cdd.getStudentName()  + "', '"
                + cdd.getRegNum()       + "', '"
                + cdd.getPaperCode()    + "', "
                + cdd.getTableNumber()  + ", '"
                + cdd.getStatus()       + "', '"
                + cdd.getProgramme()    + "')");
    }

    private HashMap<String, HashMap<String, HashMap<String, Candidate>>>
    getPaperMap(AttendanceList.Status status, Statement stmt) throws Exception{

        HashMap<String, HashMap<String, HashMap<String, Candidate>>> paperMap = new HashMap<>();
        List<String> paperCodeList = getDistinctPaperCode(stmt);

        ResultSet ptr = stmt.executeQuery("SELECT * FROM "  + ATTENDANCE_TABLE + " WHERE "
                + TABLE_INFO_COLUMN_STATUS + " = '" + status.toString() + "';");

        for(int i = 0; i < paperCodeList.size(); i++){
            if (ptr.first()) {
                do {
                    HashMap<String, HashMap<String, Candidate>> prgMap;
                    prgMap = getProgrammeMap(status, paperCodeList.get(i), stmt);

                    paperMap.put(paperCodeList.get(i), prgMap);
                } while (ptr.next());
            }
        }
        paperCodeList.clear();
        ptr.close();
        return paperMap;
    }

    private HashMap<String, HashMap<String, Candidate>>
    getProgrammeMap(AttendanceList.Status status, String paperCode, Statement stmt) throws Exception{

        HashMap<String, HashMap<String, Candidate>> prgMap = new HashMap<>();
        List<String> prgList = getDistinctProgramme(stmt);

        ResultSet ptr = stmt.executeQuery("SELECT * FROM "  + ATTENDANCE_TABLE  + " WHERE "
                + TABLE_INFO_COLUMN_STATUS  + " = '" + status.toString() + "' AND "
                + TABLE_INFO_COLUMN_CODE    + " = '" + paperCode + "';");

        for(int i = 0; i < prgList.size(); i++){
            if (ptr.first()) {
                do {
                    HashMap<String, Candidate> candidateMap;
                    candidateMap = getCandidateMap(paperCode, status, prgList.get(i), stmt);

                    prgMap.put(prgList.get(i), candidateMap);
                } while (ptr.next());
            }
        }
        prgList.clear();
        ptr.close();
        return prgMap;
    }

    private HashMap<String, Candidate>
    getCandidateMap(String paperCode, AttendanceList.Status status,
                     String prg, Statement stmt) throws Exception{
        HashMap<String, Candidate> candidateMap= new HashMap<>();

        ResultSet ptr = stmt.executeQuery("SELECT * FROM "  + ATTENDANCE_TABLE+ " WHERE "
                + TABLE_INFO_COLUMN_CODE + " = '" + paperCode + "' AND " + TABLE_INFO_COLUMN_STATUS
                + " = '" + status.toString() + "' AND " + TABLE_INFO_COLUMN_PRG + " = '" + prg + "';");

        if(ptr.first()){
            do{
                Candidate cdd = new Candidate();

                cdd.setStudentName(ptr.getString(TABLE_INFO_COLUMN_NAME));
                cdd.setTableNumber(ptr.getInt(TABLE_INFO_COLUMN_TABLE));
                cdd.setRegNum(ptr.getString(TABLE_INFO_COLUMN_REGNUM));
                cdd.setPaperCode(paperCode);
                cdd.setStatus(status);
                cdd.setProgramme(prg);
                candidateMap.put(cdd.getRegNum(), cdd);
            }while (ptr.next());
        }
        ptr.close();
        return candidateMap;
    }

    private List<String> getDistinctPaperCode(Statement stmt) throws Exception{
        List<String> paperCodeList = new ArrayList<>();

        ResultSet ptr   = stmt.executeQuery( "SELECT DISTINCT " + TABLE_INFO_COLUMN_CODE +
                " FROM " + ATTENDANCE_TABLE + ";");

        if(ptr.first()){
            do{
                paperCodeList.add(ptr.getString(TABLE_INFO_COLUMN_CODE));
            }while (ptr.next());
        }
        ptr.close();
        return paperCodeList;
    }

    private List<String> getDistinctProgramme(Statement stmt) throws Exception{
        List<String> prgList = new ArrayList<>();

        ResultSet ptr   = stmt.executeQuery( "SELECT DISTINCT " + TABLE_INFO_COLUMN_PRG +
                " FROM " + ATTENDANCE_TABLE + ";");

        if(ptr.first()){
            do{
                prgList.add(ptr.getString(TABLE_INFO_COLUMN_PRG));
            } while (ptr.next());
        }
        ptr.close();
        return prgList;
    }
}

package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.ProcessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 18/06/2016.
 */
public class LocalDbLoader {

    public static final String DB_NAME = "CheckList.db";
    private static final String PACKAGE = "com.info.ghiny.examsystem";
    public static final String DRIVER  = "org.sqldroid.SQLDroidDriver";
    public static final String ADDRESS = "jdbc:sqldroid:/data/data/" + PACKAGE
                                            + "/databases/" + DB_NAME;

    private static final String ATTENDANCE_TABLE        = "AttdTable";
    public static final String TABLE_INFO_ID            = "_id";
    public static final String TABLE_INFO_COLUMN_INDEX  = "ExamIndex";
    public static final String TABLE_INFO_COLUMN_REGNUM = "RegNum";
    public static final String TABLE_INFO_COLUMN_STATUS = "Status";
    public static final String TABLE_INFO_COLUMN_CODE   = "Code";
    public static final String TABLE_INFO_COLUMN_PRG    = "Programme";
    public static final String TABLE_INFO_COLUMN_TABLE  = "TableNo";

    private static final String PAPERS_TABLE    = "PaperTable";
    public static final String PAPER_ID         = "_id";
    public static final String PAPER_CODE       = "PaperCode";
    public static final String PAPER_DESC       = "PaperDesc";
    public static final String PAPER_START_NO   = "PaperStartNo";
    public static final String PAPER_TOTAL_CDD  = "PaperTotalCdd";

    private String curDriver;
    private String curAddress;

    private String CREATE_ATTD_TABLE = "CREATE TABLE IF NOT EXISTS " + ATTENDANCE_TABLE  + "( "
            + TABLE_INFO_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_INFO_COLUMN_REGNUM  + " TEXT    NOT NULL, "
            + TABLE_INFO_COLUMN_INDEX    + " TEXT    NOT NULL, "
            + TABLE_INFO_COLUMN_STATUS  + " TEXT    NOT NULL, "
            + TABLE_INFO_COLUMN_CODE    + " TEXT    NOT NULL, "
            + TABLE_INFO_COLUMN_PRG     + " TEXT    NOT NULL, "
            + TABLE_INFO_COLUMN_TABLE   + " INT     NOT NULL)";

    private String CREATE_PAPERS_TABLE = "CREATE TABLE IF NOT EXISTS " + PAPERS_TABLE + "( "
            + PAPER_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PAPER_CODE        + " TEXT    NOT NULL, "
            + PAPER_DESC        + " TEXT    NOT NULL, "
            + PAPER_START_NO    + " INTEGER NOT NULL, "
            + PAPER_TOTAL_CDD   + " INTEGER NOT NULL)";

    public LocalDbLoader(String driver, String url){
        curAddress  = url;
        curDriver   = driver;
    }

    public Connection estaConnection() throws ProcessException{
        Connection con = null;
        try{
            Class.forName(curDriver);
            con = DriverManager.getConnection(curAddress);
        } catch(Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        return con;
    }

    public void createTableIfNotExist() throws ProcessException{
        try{
            Connection con = estaConnection();
            Statement stmt = con.createStatement();

            stmt.execute(CREATE_ATTD_TABLE);
            stmt.execute(CREATE_PAPERS_TABLE);

            stmt.close();
            con.close();
        } catch(Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public boolean emptyAttdInDB() throws ProcessException{
        Boolean status = true;
        try{
            Connection con = estaConnection();
            Statement stmt = con.createStatement();

            stmt.execute(CREATE_ATTD_TABLE);
            ResultSet ptr = stmt.executeQuery("SELECT * FROM " + ATTENDANCE_TABLE);

            if(ptr.first())
                status = false;

            ptr.close();
            stmt.close();
            con.close();
        } catch(Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        return status;
    }

    public boolean emptyPapersInDB() throws ProcessException{
        Boolean status = true;
        try{
            Connection con = estaConnection();
            Statement stmt = con.createStatement();

            stmt.execute(CREATE_PAPERS_TABLE);
            ResultSet ptr = stmt.executeQuery("SELECT * FROM " + PAPERS_TABLE);

            if(ptr.first())
                status = false;

            ptr.close();
            stmt.close();
            con.close();
        } catch(Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        return status;
    }

    public void clearDatabase() throws ProcessException{
        try {
            Connection con = estaConnection();
            Statement stmt = con.createStatement();

            stmt.execute(CREATE_ATTD_TABLE);
            stmt.execute(CREATE_PAPERS_TABLE);
            stmt.executeUpdate("DELETE FROM " + ATTENDANCE_TABLE);
            stmt.executeUpdate("DELETE FROM " + PAPERS_TABLE);
            stmt.executeUpdate("VACUUM");

            stmt.close();
            con.close();
        } catch (Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public void saveAttendanceList(AttendanceList attdList) throws ProcessException{
        try {
            Connection con = estaConnection();
            Statement stmt = con.createStatement();
            List<String> regNumList = attdList.getAllCandidateRegNumList();

            stmt.execute(CREATE_ATTD_TABLE);

            for (int i = 0; i < regNumList.size(); i++)
                saveAttendance(attdList.getCandidate(regNumList.get(i)), stmt);

            stmt.close();
            con.close();
        } catch (Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public AttendanceList queryAttendanceList() throws ProcessException{
        createTableIfNotExist();
        AttendanceList attdList = new AttendanceList();
        List<AttendanceList.Status> statusList = new ArrayList<>();
        statusList.add(AttendanceList.Status.PRESENT);
        statusList.add(AttendanceList.Status.ABSENT);
        statusList.add(AttendanceList.Status.BARRED);
        statusList.add(AttendanceList.Status.EXEMPTED);
        statusList.add(AttendanceList.Status.QUARANTIZED);

        try {
            for(int j = 0; j < statusList.size(); j++){
                List<Candidate> cdds = getCandidatesWithStatus(statusList.get(j));
                for(int i = 0; i < cdds.size(); i ++ ){
                    attdList.addCandidate(cdds.get(i), cdds.get(i).getPaperCode(),
                            cdds.get(i).getStatus(), cdds.get(i).getProgramme());
                }
            }
        } catch (Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
        return attdList;
    }

    public void savePaperList(HashMap<String, ExamSubject> papers) throws ProcessException{
        try{
            Connection con = estaConnection();
            Statement stmt = con.createStatement();

            String[] paperArr = Arrays.copyOf(papers.keySet().toArray(),
                    papers.keySet().toArray().length, String[].class);

            stmt.execute(CREATE_PAPERS_TABLE);

            for(int i = 0; i < paperArr.length; i++)
                savePaper(papers.get(paperArr[i]), stmt);

            stmt.close();
            con.close();
        } catch (Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }
    }

    public HashMap<String, ExamSubject> queryPapers() throws ProcessException{
        HashMap<String, ExamSubject> paperMap = new HashMap<>();
        try{
            Connection con = estaConnection();
            Statement stmt = con.createStatement();

            stmt.execute(CREATE_PAPERS_TABLE);
            ResultSet ptr = stmt.executeQuery("SELECT * FROM "  + PAPERS_TABLE);

            if(ptr.first()){
                do{
                    ExamSubject subject = new ExamSubject();

                    subject.setPaperCode(ptr.getString(PAPER_CODE));
                    subject.setPaperDesc(ptr.getString(PAPER_DESC));
                    subject.setStartTableNum(ptr.getInt(PAPER_START_NO));
                    subject.setNumOfCandidate(ptr.getInt(PAPER_TOTAL_CDD));
                    paperMap.put(subject.getPaperCode(), subject);
                }while (ptr.next());
            }

            ptr.close();
            stmt.close();
            con.close();
        }catch (Exception err){
            throw new ProcessException("FATAL: " + err.getMessage() + "\nPlease Consult Developer",
                    ProcessException.FATAL_MESSAGE, IconManager.WARNING);
        }

        return paperMap;
    }

    //= Private methods ============================================================================
    private void saveAttendance(Candidate cdd, Statement stmt) throws Exception{
        String SAVE_ATTENDANCE = "INSERT OR REPLACE INTO "     + ATTENDANCE_TABLE
                + " (" + TABLE_INFO_COLUMN_INDEX     + ", " + TABLE_INFO_COLUMN_REGNUM
                + ", " + TABLE_INFO_COLUMN_TABLE    + ", " + TABLE_INFO_COLUMN_STATUS
                + ", " + TABLE_INFO_COLUMN_CODE     + ", " + TABLE_INFO_COLUMN_PRG
                + ") VALUES ('";

        stmt.executeUpdate(SAVE_ATTENDANCE
                + cdd.getExamIndex()  + "', '"
                + cdd.getRegNum()       + "', "
                + cdd.getTableNumber()  + ", '"
                + cdd.getStatus()       + "', '"
                + cdd.getPaperCode()    + "', '"
                + cdd.getProgramme()    + "')");
    }

    private void savePaper(ExamSubject paper, Statement stmt) throws Exception{
        String SAVE_PAPER = "INSERT OR REPLACE INTO "  + PAPERS_TABLE
                + " (" + PAPER_CODE     + ", " + PAPER_DESC
                + ", " + PAPER_START_NO + ", " + PAPER_TOTAL_CDD
                + ") VALUES ('";

        stmt.executeUpdate(SAVE_PAPER
                + paper.getPaperCode()      + "', '"
                + paper.getPaperDesc()      + "', "
                + paper.getStartTableNum()  + ", "
                + paper.getNumOfCandidate() + ")");
    }

    private List<Candidate> getCandidatesWithStatus(AttendanceList.Status status) throws Exception{

        List<Candidate> candidates= new ArrayList<>();
        Connection con  = estaConnection();
        Statement stmt  = con.createStatement();

        ResultSet ptr = stmt.executeQuery("SELECT * FROM "  + ATTENDANCE_TABLE + " WHERE "
                + TABLE_INFO_COLUMN_STATUS  + " = '" + status.toString() + "';" );

        if(ptr.first()){
            do{
                Candidate cdd = new Candidate();

                cdd.setExamIndex(ptr.getString(TABLE_INFO_COLUMN_INDEX));
                cdd.setTableNumber(ptr.getInt(TABLE_INFO_COLUMN_TABLE));
                cdd.setRegNum(ptr.getString(TABLE_INFO_COLUMN_REGNUM));
                cdd.setPaperCode(ptr.getString(TABLE_INFO_COLUMN_CODE));
                cdd.setStatus(status);
                cdd.setProgramme(ptr.getString(TABLE_INFO_COLUMN_PRG));
                candidates.add(cdd);
            }while (ptr.next());
        }
        ptr.close();
        stmt.close();
        con.close();
        return candidates;
    }

    private List<String> getDistinctPaperCode() throws Exception{
        List<String> paperCodeList = new ArrayList<>();

        Connection con = estaConnection();
        Statement stmt = con.createStatement();

        ResultSet ptr   = stmt.executeQuery( "SELECT DISTINCT " + TABLE_INFO_COLUMN_CODE +
                " FROM " + ATTENDANCE_TABLE + ";");

        if(ptr.first()){
            do{
                paperCodeList.add(ptr.getString(TABLE_INFO_COLUMN_CODE));
            }while (ptr.next());
        }
        ptr.close();
        stmt.close();
        con.close();
        return paperCodeList;
    }

    private List<String> getDistinctProgramme() throws Exception{
        List<String> prgList = new ArrayList<>();

        Connection con = estaConnection();
        Statement stmt = con.createStatement();

        ResultSet ptr   = stmt.executeQuery( "SELECT DISTINCT " + TABLE_INFO_COLUMN_PRG +
                " FROM " + ATTENDANCE_TABLE + ";");

        if(ptr.first()){
            do{
                prgList.add(ptr.getString(TABLE_INFO_COLUMN_PRG));
            } while (ptr.next());
        }
        ptr.close();
        stmt.close();
        con.close();
        return prgList;
    }
}

package com.info.ghiny.examsystem.database;

import com.info.ghiny.examsystem.tools.IconManager;
import com.info.ghiny.examsystem.tools.ProcessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.sql.DriverManager.*;

/**
 * Created by GhinY on 18/06/2016.
 */
public class LocalDbLoader {

    public static final String DB_NAME = "FragList.sqlite";
    private static final String PACKAGE = "com.info.ghiny.examsystem";
    public static final String DRIVER  = "org.sqldroid.SQLDroidDriver";
    public static final String ADDRESS = "jdbc:sqldroid:/data/data/" + PACKAGE
                                            + "/databases/" + DB_NAME;

    private static final String ATTENDANCE_TABLE    = "AttdTable";

    private static final String PAPERS_TABLE        = "PaperTable";

    private String curDriver;
    private String curAddress;

    private String CREATE_ATTD_TABLE = "CREATE TABLE IF NOT EXISTS " + ATTENDANCE_TABLE  + "( "
            + Candidate.CDD_DB_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Candidate.CDD_REG_NUM     + " TEXT    NOT NULL, "
            + Candidate.CDD_EXAM_INDEX  + " TEXT    NOT NULL, "
            + Candidate.CDD_STATUS      + " TEXT    NOT NULL, "
            + Candidate.CDD_PAPER       + " TEXT    NOT NULL, "
            + Candidate.CDD_PROG        + " TEXT    NOT NULL, "
            + Candidate.CDD_TABLE       + " INT     NOT NULL)";

    private String CREATE_PAPERS_TABLE = "CREATE TABLE IF NOT EXISTS " + PAPERS_TABLE + "( "
            + ExamSubject.PAPER_DB_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ExamSubject.PAPER_CODE        + " TEXT    NOT NULL, "
            + ExamSubject.PAPER_DESC        + " TEXT    NOT NULL, "
            + ExamSubject.PAPER_START_NO    + " INTEGER NOT NULL, "
            + ExamSubject.PAPER_TOTAL_CDD   + " INTEGER NOT NULL)";

    public LocalDbLoader(String driver, String url){
        curDriver   = driver;
        curAddress  = url;
    }

    public Connection estaConnection() throws ProcessException{
        Connection con;
        try{
            Class.forName(curDriver);
            con = getConnection(curAddress);
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

                    subject.setPaperCode(ptr.getString(ExamSubject.PAPER_CODE));
                    subject.setPaperDesc(ptr.getString(ExamSubject.PAPER_DESC));
                    subject.setStartTableNum(ptr.getInt(ExamSubject.PAPER_START_NO));
                    subject.setNumOfCandidate(ptr.getInt(ExamSubject.PAPER_TOTAL_CDD));
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
                + " (" + Candidate.CDD_EXAM_INDEX   + ", " + Candidate.CDD_REG_NUM
                + ", " + Candidate.CDD_TABLE        + ", " + Candidate.CDD_STATUS
                + ", " + Candidate.CDD_PAPER        + ", " + Candidate.CDD_PROG
                + ") VALUES ('";

        stmt.executeUpdate(SAVE_ATTENDANCE
                + cdd.getExamIndex()    + "', '"
                + cdd.getRegNum()       + "', "
                + cdd.getTableNumber()  + ", '"
                + cdd.getStatus()       + "', '"
                + cdd.getPaperCode()    + "', '"
                + cdd.getProgramme()    + "')");
    }

    private void savePaper(ExamSubject paper, Statement stmt) throws Exception{
        String SAVE_PAPER = "INSERT OR REPLACE INTO "  + PAPERS_TABLE
                + " (" + ExamSubject.PAPER_CODE     + ", " + ExamSubject.PAPER_DESC
                + ", " + ExamSubject.PAPER_START_NO + ", " + ExamSubject.PAPER_TOTAL_CDD
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
                + Candidate.CDD_STATUS  + " = '" + status.toString() + "';" );

        if(ptr.first()){
            do{
                Candidate cdd = new Candidate();

                cdd.setExamIndex(ptr.getString(Candidate.CDD_EXAM_INDEX));
                cdd.setTableNumber(ptr.getInt(Candidate.CDD_TABLE));
                cdd.setRegNum(ptr.getString(Candidate.CDD_REG_NUM));
                cdd.setPaperCode(ptr.getString(Candidate.CDD_PAPER));
                cdd.setStatus(status);
                cdd.setProgramme(ptr.getString(Candidate.CDD_PROG));
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

        ResultSet ptr   = stmt.executeQuery( "SELECT DISTINCT " + Candidate.CDD_PAPER +
                " FROM " + ATTENDANCE_TABLE + ";");

        if(ptr.first()){
            do{
                paperCodeList.add(ptr.getString(Candidate.CDD_PAPER));
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

        ResultSet ptr   = stmt.executeQuery( "SELECT DISTINCT " + Candidate.CDD_PROG +
                " FROM " + ATTENDANCE_TABLE + ";");

        if(ptr.first()){
            do{
                prgList.add(ptr.getString(Candidate.CDD_PROG));
            } while (ptr.next());
        }
        ptr.close();
        stmt.close();
        con.close();
        return prgList;
    }
}

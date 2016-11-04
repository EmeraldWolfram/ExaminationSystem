package com.info.ghiny.examsystem.database;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 22/07/2016.
 */
public class CheckListLoader {
    private static final int DATABASE_VERSION       = 5;
    private static final String DATABASE_NAME       = "FragListDb";
    private static final String ATTENDANCE_TABLE    = "AttdTable";
    private static final String PAPERS_TABLE        = "PaperTable";
    private static final String CONNECTOR_TABLE     = "ConnectionTable";
    private static final String USER_TABLE          = "StaffTable";

    private static final String SAVE_ATTENDANCE = "INSERT OR REPLACE INTO " + ATTENDANCE_TABLE
            + " (" + Candidate.CDD_EXAM_INDEX   + ", " + Candidate.CDD_REG_NUM
            + ", " + Candidate.CDD_TABLE        + ", " + Candidate.CDD_STATUS
            + ", " + Candidate.CDD_PAPER        + ", " + Candidate.CDD_PROG
            + ") VALUES ('";

    private static final String SAVE_PAPER      = "INSERT OR REPLACE INTO " + PAPERS_TABLE
            + " (" + ExamSubject.PAPER_CODE     + ", " + ExamSubject.PAPER_DESC
            + ", " + ExamSubject.PAPER_START_NO + ", " + ExamSubject.PAPER_TOTAL_CDD
            + ") VALUES ('";

    private static final String SAVE_CONNECTOR  = "INSERT OR REPLACE INTO " + CONNECTOR_TABLE
            + " (" + Connector.CONNECT_IP       + ", " + Connector.CONNECT_PORT
            + ", " + Connector.CONNECT_DATE     + ", " + Connector.CONNECT_SESSION
            + ") VALUES ('";

    private static final String SAVE_USER       = "INSERT OR REPLACE INTO " + USER_TABLE
            + " (" + StaffIdentity.STAFF_ID_NO  + ", " + StaffIdentity.STAFF_HPASS
            + ", " + StaffIdentity.STAFF_NAME   + ", " + StaffIdentity.STAFF_VENUE
            + ", " + StaffIdentity.STAFF_ROLE   + ") VALUES ('";

    private static SQLiteDatabase database;
    private static CheckListOpenHelper openHelper;

    public CheckListLoader(Context context){
        openHelper  = new CheckListOpenHelper(context);
        database    = openHelper.getWritableDatabase();
    }

    public static void setDatabase(SQLiteDatabase database) {
        CheckListLoader.database = database;
    }

    //AVAILABLE METHOD ========================================================================
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    public boolean emptyAttdInDB(){
        Boolean status = true;

        Cursor ptr = database.rawQuery("SELECT * FROM " + ATTENDANCE_TABLE, null);
        if(ptr.moveToFirst())
            status = false;

        ptr.close();
        return status;
    }

    public boolean emptyPapersInDB(){
        Boolean status = true;

        Cursor ptr = database.rawQuery("SELECT * FROM " + PAPERS_TABLE, null);
        if(ptr.moveToFirst())
            status = false;

        ptr.close();
        return status;
    }

    public boolean emptyConnectorInDB(){
        Boolean status = true;

        Cursor ptr = database.rawQuery("SELECT * FROM " + CONNECTOR_TABLE, null);
        if(ptr.moveToFirst())
            status = false;
        ptr.close();

        return status;
    }

    public boolean emptyUserInDB() {
        Boolean status  = true;

        Cursor ptr = database.rawQuery("SELECT * FROM " + USER_TABLE, null);
        if(ptr.moveToFirst())
            status = false;
        ptr.close();

        return status;
    }

    //Simply clean the database
    public void clearDatabase(){
        database.execSQL("DELETE FROM " + ATTENDANCE_TABLE);
        database.execSQL("DELETE FROM " + PAPERS_TABLE);
        database.execSQL("VACUUM");
    }

    public void clearUserDatabase(){
        database.execSQL("DELETE FROM " + CONNECTOR_TABLE);
        database.execSQL("DELETE FROM " + USER_TABLE);
        database.execSQL("VACUUM");
    }

    //Clear the database and save a new set of AttendanceList into the database
    public void saveAttendanceList(AttendanceList attdList){
        List<String> regNumList = attdList.getAllCandidateRegNumList();

        for(int i = 0; i < regNumList.size(); i++)
            saveAttendance(attdList.getCandidate(regNumList.get(i)));
    }

    public void savePaperList(HashMap<String, ExamSubject> papers){
        String[] paperArr = Arrays.copyOf(papers.keySet().toArray(),
                papers.keySet().toArray().length, String[].class);

        for(int i = 0; i < paperArr.length; i++)
            savePaper(papers.get(paperArr[i]));
    }

    public void saveConnector(Connector connector){
        database.execSQL(SAVE_CONNECTOR
                + connector.getIpAddress()          +   "', "
                + connector.getPortNumber()         +   ", '"
                + connector.getDateInString()       +   "', '"
                + connector.getSession().toString() +   "')");
    }

    public void saveUser(StaffIdentity staffIdentity) {
        database.execSQL(SAVE_USER
                + staffIdentity.getIdNo()           + "', '"
                + staffIdentity.getHashPass()       + "', '"
                + staffIdentity.getName()           + "', '"
                + staffIdentity.getExamVenue()  + "', '"
                + staffIdentity.getRole().get(0)    + "')");
    }

    //Retrieve an attendanceList from the database
    public AttendanceList queryAttendanceList() {
        AttendanceList attdList = new AttendanceList();
        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.PRESENT);
        statusList.add(Status.ABSENT);
        statusList.add(Status.BARRED);
        statusList.add(Status.EXEMPTED);
        statusList.add(Status.QUARANTINED);

        for(int j = 0; j < statusList.size(); j++){
            List<Candidate> cdds = getCandidatesWithStatus(statusList.get(j));
            for(int i = 0; i < cdds.size(); i ++ ){
                attdList.addCandidate(cdds.get(i), cdds.get(i).getPaperCode(),
                        cdds.get(i).getStatus(), cdds.get(i).getProgramme());
            }
        }
        return attdList;
    }

    public HashMap<String, ExamSubject> queryPapers() {
        HashMap<String, ExamSubject> paperMap = new HashMap<>();

        Cursor ptr = database.rawQuery("SELECT * FROM "  + PAPERS_TABLE, null);

        if(ptr.moveToFirst()){
            do{
                ExamSubject subject = new ExamSubject();

                subject.setPaperCode(ptr.getString(ptr.getColumnIndex(ExamSubject.PAPER_CODE)));
                subject.setPaperDesc(ptr.getString(ptr.getColumnIndex(ExamSubject.PAPER_DESC)));
                subject.setStartTableNum(ptr.getInt(ptr.getColumnIndex(ExamSubject.PAPER_START_NO)));
                subject.setNumOfCandidate(ptr.getInt(ptr.getColumnIndex(ExamSubject.PAPER_TOTAL_CDD)));
                paperMap.put(subject.getPaperCode(), subject);
            }while (ptr.moveToNext());
        }

        ptr.close();

        return paperMap;
    }

    public Connector queryConnector(){
        Connector connector = null;

        Cursor ptr = database.rawQuery("SELECT * FROM "  + CONNECTOR_TABLE, null);

        if(ptr.moveToFirst()){
            String ipAddress    = ptr.getString(ptr.getColumnIndex(Connector.CONNECT_IP));
            Integer portNumber  = ptr.getInt(ptr.getColumnIndex(Connector.CONNECT_PORT));
            String date         = ptr.getString(ptr.getColumnIndex(Connector.CONNECT_DATE));
            String session      = ptr.getString(ptr.getColumnIndex(Connector.CONNECT_SESSION));

            connector   = new Connector(ipAddress, portNumber, null);
            connector.setDate(connector.parseStringToDate(date));
            connector.setSession(Session.parseSession(session));
        }

        ptr.close();

        return connector;
    }

    public StaffIdentity queryUser(){
        StaffIdentity user = null;

        Cursor ptr = database.rawQuery("SELECT * FROM "  + USER_TABLE, null);

        if(ptr.moveToFirst()){
            String userId       = ptr.getString(ptr.getColumnIndex(StaffIdentity.STAFF_ID_NO));
            String userHPass    = ptr.getString(ptr.getColumnIndex(StaffIdentity.STAFF_HPASS));
            String userName     = ptr.getString(ptr.getColumnIndex(StaffIdentity.STAFF_NAME));
            String userVenue    = ptr.getString(ptr.getColumnIndex(StaffIdentity.STAFF_VENUE));
            String userRole     = ptr.getString(ptr.getColumnIndex(StaffIdentity.STAFF_ROLE));

            user    = new StaffIdentity(userId, true, userName, userVenue);
            user.setHashPass(userHPass);
            user.addRole(userRole);
        }

        ptr.close();

        return user;
    }


    //INTERNAL HIDDEN TOOLS ====================================================================
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    //Save an instance of Candidate into the database
    private void saveAttendance(Candidate cdd){
        database.execSQL(SAVE_ATTENDANCE
                + cdd.getExamIndex()    + "', '"
                + cdd.getRegNum()       + "', "
                + cdd.getTableNumber()  + ", '"
                + cdd.getStatus()       + "', '"
                + cdd.getPaperCode()    + "', '"
                + cdd.getProgramme()    + "')");
    }

    private void savePaper(ExamSubject paper){
        database.execSQL(SAVE_PAPER
                + paper.getPaperCode()      + "', '"
                + paper.getPaperDesc()      + "', "
                + paper.getStartTableNum()  + ", "
                + paper.getNumOfCandidate() + ")");
    }

    private List<Candidate> getCandidatesWithStatus(Status status){

        List<Candidate> candidates= new ArrayList<>();

        Cursor ptr = database.rawQuery("SELECT * FROM "  + ATTENDANCE_TABLE + " WHERE "
                + Candidate.CDD_STATUS +" = ?", new String[]{status.toString()});

        if(ptr.moveToFirst()){
            do{
                Candidate cdd = new Candidate();

                cdd.setExamIndex(ptr.getString(ptr.getColumnIndex(Candidate.CDD_EXAM_INDEX)));
                cdd.setTableNumber(ptr.getInt(ptr.getColumnIndex(Candidate.CDD_TABLE)));
                cdd.setRegNum(ptr.getString(ptr.getColumnIndex(Candidate.CDD_REG_NUM)));
                cdd.setPaperCode(ptr.getString(ptr.getColumnIndex(Candidate.CDD_PAPER)));
                cdd.setStatus(status);
                cdd.setProgramme(ptr.getString(ptr.getColumnIndex(Candidate.CDD_PROG)));
                candidates.add(cdd);
            }while (ptr.moveToNext());
        }
        ptr.close();

        return candidates;
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //==========================================================================================
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //==========================================================================================
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    private class CheckListOpenHelper extends SQLiteOpenHelper {

        CheckListOpenHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + ATTENDANCE_TABLE  + "( "
                    + Candidate.CDD_DB_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Candidate.CDD_REG_NUM     + " TEXT    NOT NULL, "
                    + Candidate.CDD_EXAM_INDEX  + " TEXT    NOT NULL, "
                    + Candidate.CDD_STATUS      + " TEXT    NOT NULL, "
                    + Candidate.CDD_PAPER       + " TEXT    NOT NULL, "
                    + Candidate.CDD_PROG        + " TEXT    NOT NULL, "
                    + Candidate.CDD_TABLE       + " INTEGER NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + PAPERS_TABLE + "( "
                    + ExamSubject.PAPER_DB_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ExamSubject.PAPER_CODE        + " TEXT    NOT NULL, "
                    + ExamSubject.PAPER_DESC        + " TEXT    NOT NULL, "
                    + ExamSubject.PAPER_START_NO    + " INTEGER NOT NULL, "
                    + ExamSubject.PAPER_TOTAL_CDD   + " INTEGER NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + CONNECTOR_TABLE + "( "
                    + Connector.CONNECT_DB_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Connector.CONNECT_IP      + " TEXT    NOT NULL, "
                    + Connector.CONNECT_PORT    + " INTEGER NOT NULL, "
                    + Connector.CONNECT_DATE    + " TEXT    NOT NULL, "
                    + Connector.CONNECT_SESSION + " TEXT    NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + "( "
                    + StaffIdentity.STAFF_DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + StaffIdentity.STAFF_ID_NO + " TEXT    NOT NULL, "
                    + StaffIdentity.STAFF_HPASS + " TEXT    NOT NULL, "
                    + StaffIdentity.STAFF_NAME  + " TEXT    NOT NULL, "
                    + StaffIdentity.STAFF_VENUE + " TEXT    NOT NULL, "
                    + StaffIdentity.STAFF_ROLE  + " TEXT    NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST " + ATTENDANCE_TABLE);
            db.execSQL("DROP TABLE IF EXIST " + PAPERS_TABLE);
            db.execSQL("DROP TABLE IF EXIST " + CONNECTOR_TABLE);
            db.execSQL("DROP TABLE IF EXIST " + USER_TABLE);
            onCreate(db);
        }
    }
}

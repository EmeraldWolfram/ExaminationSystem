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
    private static final int DATABASE_VERSION       = 1;
    public static final String DATABASE_NAME       = "FragListDb";
    private static final String ATTENDANCE_TABLE    = "AttdTable";
    private static final String PAPERS_TABLE        = "PaperTable";

    private static final String SAVE_ATTENDANCE = "INSERT OR REPLACE INTO "     + ATTENDANCE_TABLE
            + " (" + Candidate.CDD_EXAM_INDEX   + ", " + Candidate.CDD_REG_NUM
            + ", " + Candidate.CDD_TABLE        + ", " + Candidate.CDD_STATUS
            + ", " + Candidate.CDD_PAPER        + ", " + Candidate.CDD_PROG
            + ") VALUES ('";

    private static final String SAVE_PAPER = "INSERT OR REPLACE INTO "  + PAPERS_TABLE
            + " (" + ExamSubject.PAPER_CODE     + ", " + ExamSubject.PAPER_DESC
            + ", " + ExamSubject.PAPER_START_NO + ", " + ExamSubject.PAPER_TOTAL_CDD
            + ") VALUES ('";

    private static SQLiteDatabase database;
    private static CheckListOpenHelper openHelper;

    public CheckListLoader(Context context){
        openHelper  = new CheckListOpenHelper(context);
        database    = openHelper.getWritableDatabase();
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

    //Simply clean the database
    public static void clearDatabase(){
        database.execSQL("DELETE FROM " + ATTENDANCE_TABLE);
        database.execSQL("DELETE FROM " + PAPERS_TABLE);
        database.execSQL("VACUUM");
    }

    //Clear the database and save a new set of AttendanceList into the database
    public void saveAttendanceList(AttendanceList attdList){
        clearDatabase();
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

    //Retrieve an attendanceList from the database
    public AttendanceList queryAttendanceList() {
        AttendanceList attdList = new AttendanceList();
        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.PRESENT);
        statusList.add(Status.ABSENT);
        statusList.add(Status.BARRED);
        statusList.add(Status.EXEMPTED);
        statusList.add(Status.QUARANTIZED);

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
                + Candidate.CDD_STATUS +" = ?"+status.toString(), new String[]{status.toString()});

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

        public CheckListOpenHelper(Context context){
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
                    + Candidate.CDD_TABLE       + " INT     NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + PAPERS_TABLE + "( "
                    + ExamSubject.PAPER_DB_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ExamSubject.PAPER_CODE        + " TEXT    NOT NULL, "
                    + ExamSubject.PAPER_DESC        + " TEXT    NOT NULL, "
                    + ExamSubject.PAPER_START_NO    + " INTEGER NOT NULL, "
                    + ExamSubject.PAPER_TOTAL_CDD   + " INTEGER NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST " + ATTENDANCE_TABLE);
            db.execSQL("DROP TABLE IF EXIST " + PAPERS_TABLE);
            onCreate(db);
        }
    }
}

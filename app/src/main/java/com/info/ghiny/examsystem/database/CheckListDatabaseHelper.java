package com.info.ghiny.examsystem.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.info.ghiny.examsystem.database.Candidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GhinY on 23/05/2016.
 */
public class CheckListDatabaseHelper {
    private static final int DATABASE_VERSION       = 5;
    private static final String DATABASE_NAME       = "checkListDB";
    private static final String ATTENDANCE_TABLE    = "AttdTable";

    public static final String TABLE_INFO_ID            = "_id";
    public static final String TABLE_INFO_COLUMN_NAME   = "Name";
    public static final String TABLE_INFO_COLUMN_REGNUM = "RegNum";
    public static final String TABLE_INFO_COLUMN_CODE   = "Code";
    public static final String TABLE_INFO_COLUMN_TABLE  = "TableNo";
    public static final String TABLE_INFO_COLUMN_STATUS = "Status";

    private static final String SAVE_ATTENDANCE = "INSERT INTO " + ATTENDANCE_TABLE
            + " (" + TABLE_INFO_COLUMN_NAME     + ", " + TABLE_INFO_COLUMN_REGNUM
            + ", " + TABLE_INFO_COLUMN_CODE     + ", " + TABLE_INFO_COLUMN_TABLE
            + ", " + TABLE_INFO_COLUMN_STATUS   + ") VALUES ('";

    private SQLiteDatabase database;
    private CheckListOpenHelper openHelper;

    public CheckListDatabaseHelper(Context context){
        openHelper  = new CheckListOpenHelper(context);
        database    = openHelper.getWritableDatabase();
    }

    public void saveAttendanceList(AttendanceList attdList){
        //clearDatabase();
    }

    public void clearDatabase(){
        database.execSQL("DELETE FROM " + ATTENDANCE_TABLE);
        database.execSQL("VACUUM");
    }

    public HashMap<AttendanceList.Status, HashMap<String, HashMap<String, Candidate>>>
    getLastSavedAttendanceList(){
        HashMap<AttendanceList.Status, HashMap<String, HashMap<String, Candidate>>> map;
        map = new HashMap<>();
        map.put(AttendanceList.Status.PRESENT, getPaperMap(AttendanceList.Status.PRESENT));
        map.put(AttendanceList.Status.ABSENT, getPaperMap(AttendanceList.Status.ABSENT));
        map.put(AttendanceList.Status.BARRED, getPaperMap(AttendanceList.Status.BARRED));
        map.put(AttendanceList.Status.EXEMPTED, getPaperMap(AttendanceList.Status.EXEMPTED));

        return map;
    }

    private void saveAttendance(Candidate cdd){
        database.execSQL(SAVE_ATTENDANCE    + cdd.getStudentName()  + "', '"
                + cdd.getRegNum()       + "', '"
                + cdd.getPaperCode()    + "', "
                + cdd.getTableNumber()  + ", '"
                + cdd.getStatus()       + "')");
    }

    private HashMap<String, HashMap<String, Candidate>> getPaperMap(AttendanceList.Status status){

        HashMap<String, HashMap<String, Candidate>> paperMap = new HashMap<>();
        List<String> paperCodeList = getDistinctPaperCode();

        Cursor ptr = database.rawQuery("SELECT * FROM "  + ATTENDANCE_TABLE+ " WHERE "
                + TABLE_INFO_COLUMN_STATUS + " = ?", new String[]{status.toString()});

        for(int i = 0; i < paperCodeList.size(); i++){
            if (ptr.moveToFirst()) {
                do {
                    HashMap<String, Candidate> candidateMap = new HashMap<>();
                    candidateMap = getCandidateList(paperCodeList.get(i), status);

                    paperMap.put(paperCodeList.get(i), candidateMap);
                } while (ptr.moveToNext());
            }
        }
        ptr.close();
        return paperMap;
    }

    private HashMap<String, Candidate> getCandidateList(String paperCode,
                                                        AttendanceList.Status status){
        HashMap<String, Candidate> candidateMap= new HashMap<>();
        Cursor ptr  = database.rawQuery("SELECT * FROM "  + ATTENDANCE_TABLE+ " WHERE "
                + TABLE_INFO_COLUMN_CODE + " = ? AND " + TABLE_INFO_COLUMN_STATUS
                + " = ?", new String[]{paperCode, status.toString()});

        if (ptr.moveToFirst()) {
            do {
                Candidate cdd = new Candidate();

                cdd.setStudentName(ptr.getString(ptr.getColumnIndex(TABLE_INFO_COLUMN_NAME)));
                cdd.setTableNumber(ptr.getInt(ptr.getColumnIndex(TABLE_INFO_COLUMN_TABLE)));
                cdd.setRegNum(ptr.getString(ptr.getColumnIndex(TABLE_INFO_COLUMN_REGNUM)));
                cdd.setPaperCode(paperCode);
                cdd.setStatus(status);

                candidateMap.put(cdd.getRegNum(), cdd);
            } while (ptr.moveToNext());
        }
        ptr.close();
        return candidateMap;
    }

    private List<String> getDistinctPaperCode(){
        List<String> paperCodeList = new ArrayList<>();

        Cursor ptr  = database.rawQuery("SELECT DISTINCT " + TABLE_INFO_COLUMN_CODE  + ", "
                + " FROM " + ATTENDANCE_TABLE , null);

        if (ptr.moveToFirst()) {
            do {
                paperCodeList.add(ptr.getString(ptr.getColumnIndex(TABLE_INFO_COLUMN_CODE)));
            } while (ptr.moveToNext());
        }
        ptr.close();
        return paperCodeList;
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    private class CheckListOpenHelper extends SQLiteOpenHelper{

        public CheckListOpenHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + ATTENDANCE_TABLE + "( "
                    + TABLE_INFO_ID    + " INTEGER PRIMARY KEY, "
                    + TABLE_INFO_COLUMN_NAME    + " TEXT, "
                    + TABLE_INFO_COLUMN_REGNUM  + " TEXT, "
                    + TABLE_INFO_COLUMN_CODE    + " TEXT, "
                    + TABLE_INFO_COLUMN_TABLE   + " INTEGER, "
                    + TABLE_INFO_COLUMN_STATUS  + " TEXT )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST " + ATTENDANCE_TABLE);
            onCreate(db);
        }
    }
}

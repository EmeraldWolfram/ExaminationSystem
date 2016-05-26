package com.info.ghiny.examsystem.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.info.ghiny.examsystem.database.Candidate;

import java.util.ArrayList;
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
    public static final String TABLE_INFO_COLUMN_CODE   = "Code";
    public static final String TABLE_INFO_COLUMN_DESC   = "Desc";
    public static final String TABLE_INFO_COLUMN_TABLE  = "TableNo";
    public static final String TABLE_INFO_COLUMN_STATUS = "Status";

    private SQLiteDatabase database;
    private CheckListOpenHelper openHelper;

    public CheckListDatabaseHelper(Context context){
        openHelper  = new CheckListOpenHelper(context);
        database    = openHelper.getWritableDatabase();
    }

    public void insertCandidateList(Candidate candidate){
       // database.execSQL("INSERT INTO " + ATTENDANCE_TABLE + " ("
       //         + TABLE_INFO_COLUMN_TABLE   + ", "
       //         + TABLE_INFO_COLUMN_NAME    + ", "
       //        + TABLE_INFO_COLUMN_CODE    + ", "
       //         + TABLE_INFO_COLUMN_DESC    + ", "
       //         + TABLE_INFO_COLUMN_STATUS  + ")"
        //        + " VALUES ("+ candidate.getTableNumber()
        //        + ", '"  + candidate.getStudentName()
          //      + "', '" + candidate.getPaperCode()
          //      + "', '" + candidate.getPaperDesc()
            //    + "', '" + candidate.getStatus() + "')");
    }

    public void clearDatabase(){
        database.execSQL("DELETE * FROM " + ATTENDANCE_TABLE);
    }

    public List<Candidate> getCandidatesList(AttendanceList.Status status){
        List<Candidate> candidateList = new ArrayList<Candidate>();
        Cursor ptr  = database.rawQuery("SELECT * FROM "  + ATTENDANCE_TABLE+ " WHERE "
                + TABLE_INFO_COLUMN_STATUS + " = ?", new String[]{status.toString()});

        if (ptr.moveToFirst()) {
            do {
                Candidate cdd = new Candidate();

                //cdd.setStudentName(ptr.getString(ptr.getColumnIndex(TABLE_INFO_COLUMN_NAME)));
                //cdd.setPaperCode(ptr.getString(ptr.getColumnIndex(TABLE_INFO_COLUMN_CODE)));
                //cdd.setPaperDesc(ptr.getString(ptr.getColumnIndex(TABLE_INFO_COLUMN_DESC)));
                //cdd.setTableNumber(ptr.getInt(ptr.getColumnIndex(TABLE_INFO_COLUMN_TABLE)));
                //cdd.setStatus(status);

                candidateList.add(cdd);
            } while (ptr.moveToNext());
        }
        ptr.close();
        return candidateList;
    }

    private class CheckListOpenHelper extends SQLiteOpenHelper{

        public CheckListOpenHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + ATTENDANCE_TABLE + "( "
                    + TABLE_INFO_ID    + " INTEGER PRIMARY KEY, "
                    + TABLE_INFO_COLUMN_NAME    + " TEXT, "
                    + TABLE_INFO_COLUMN_CODE    + " TEXT, "
                    + TABLE_INFO_COLUMN_DESC    + " TEXT, "
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

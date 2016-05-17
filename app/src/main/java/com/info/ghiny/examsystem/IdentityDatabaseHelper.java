package com.info.ghiny.examsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GhinY on 16/05/2016.
 */
public class IdentityDatabaseHelper {
    private static final int DATABASE_VERSION   = 5;
    private static final String DATABASE_NAME   = "identity.db";
    private static final String IDENTITY_TABLE   = "IdentityInfo";

    //STUDENT INFO TABLE
    public static final String IDENTITY_COLUMN_ID       = "_id";
    public static final String IDENTITY_COLUMN_IC       = "ic";
    public static final String IDENTITY_COLUMN_REG      = "regNum";
    public static final String IDENTITY_COLUMN_NAME     = "name";
    public static final String IDENTITY_COLUMN_PASS     = "password";
    public static final String IDENTITY_COLUMN_LEGIT    = "eligible";
    public static final String IDENTITY_COLUMN_EXAM1    = "paper1";
    public static final String IDENTITY_COLUMN_EXAM2    = "paper2";
    public static final String IDENTITY_COLUMN_EXAM3    = "paper3";
    public static final String IDENTITY_COLUMN_EXAM4    = "paper4";
    public static final String IDENTITY_COLUMN_EXAM5    = "paper5";
    public static final String IDENTITY_COLUMN_EXAM6    = "paper6";
    public static final String IDENTITY_COLUMN_EXAM7    = "paper7";
    public static final String IDENTITY_COLUMN_EXAM8    = "paper8";

    private IdentityTrackOpenHelper idOpenHelper;
    private SQLiteDatabase idDatabase;

    public IdentityDatabaseHelper(Context context){
        idOpenHelper    = new IdentityTrackOpenHelper(context);
        idDatabase      = idOpenHelper.getWritableDatabase();
    }

    //TO DO:
    // 1. SELECT * FROM ... WHERE COLUMN_IC = parameter
    // 2. return the paper1, 2... to caller

    public Cursor getEligibility(String icNumber){
        return idDatabase.rawQuery("SELECT " + IDENTITY_COLUMN_LEGIT
                + " FROM "  + IDENTITY_TABLE + " WHERE "
                + IDENTITY_COLUMN_IC + " = ?", new String[]{icNumber});
    }

    public Cursor getStudentExamPaper(){
        return idDatabase.rawQuery("SELECT * FROM " + IDENTITY_TABLE, null);
    }

    private class IdentityTrackOpenHelper extends SQLiteOpenHelper{

        public IdentityTrackOpenHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            idDatabase.execSQL("CREATE TABLE " + IDENTITY_TABLE + "( "
                    + IDENTITY_COLUMN_ID    + " INTEGER PRIMARY KEY, "
                    + IDENTITY_COLUMN_NAME  + " TEXT, "
                    + IDENTITY_COLUMN_REG   + " TEXT, "
                    + IDENTITY_COLUMN_IC    + " TEXT, "
                    + IDENTITY_COLUMN_NAME  + " TEXT, "
                    + IDENTITY_COLUMN_PASS  + " TEXT, "
                    + IDENTITY_COLUMN_LEGIT + " INTEGER, "
                    + IDENTITY_COLUMN_EXAM1 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM2 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM3 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM4 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM5 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM6 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM7 + " TEXT, "
                    + IDENTITY_COLUMN_EXAM8 + " TEXT )");

            idDatabase.execSQL("INSERT INTO " + IDENTITY_TABLE + " ("
                    + IDENTITY_COLUMN_IC    + ", "
                    + IDENTITY_COLUMN_NAME  + ", "
                    + IDENTITY_COLUMN_PASS  + ", "
                    + IDENTITY_COLUMN_LEGIT + ", "
                    + IDENTITY_COLUMN_REG   + ")"
                    + " VALUES ('710808088888', 'DR STEVEN FOONG', '63686689', 1, '66WWW6666')");

            idDatabase.execSQL("INSERT INTO " + IDENTITY_TABLE + " ("
                    + IDENTITY_COLUMN_IC    + ", "
                    + IDENTITY_COLUMN_NAME  + ", "
                    + IDENTITY_COLUMN_PASS  + ", "
                    + IDENTITY_COLUMN_LEGIT + ", "
                    + IDENTITY_COLUMN_REG   + ")"
                    + " VALUES ('921212121212', 'STUDENT AAA', '0123', 0, '15WAU99999')");

            idDatabase.execSQL("INSERT INTO " + IDENTITY_TABLE + " ("
                    + IDENTITY_COLUMN_IC    + ", "
                    + IDENTITY_COLUMN_NAME  + ", "
                    + IDENTITY_COLUMN_PASS  + ", "
                    + IDENTITY_COLUMN_LEGIT + ", "
                    + IDENTITY_COLUMN_REG   + ")"
                    + " VALUES ('951108106303', 'FOONG GHIN YEW', '0123', 0, '15WAU09184')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            idDatabase.execSQL("DROP TABLE IF EXIST " + IDENTITY_TABLE);
            onCreate(idDatabase);
        }
    }
}

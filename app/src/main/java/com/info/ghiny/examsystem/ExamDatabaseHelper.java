package com.info.ghiny.examsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by GhinY on 13/05/2016.
 */
public class ExamDatabaseHelper {

    private static final int DATABASE_VERSION   = 1;
    private static final String DATABASE_NAME   = "examSysDB";
    private static final String EXAM_TABLE      = "ExamPaper";
    private static final String IDENTITY_TABLE   = "IdentityInfo";

    //EXAM INFO TABLE
    public static final String EXAM_INFO_COLUMN_ID      = "_id";
    public static final String EXAM_INFO_COLUMN_CODE    = "Code";
    public static final String EXAM_INFO_COLUMN_DESC    = "Desc";
    public static final String EXAM_INFO_COLUMN_SESSION = "Session";
    public static final String EXAM_INFO_COLUMN_VENUE   = "Venue";
    public static final String EXAM_INFO_COLUMN_DATE    = "Date";

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

    //openHelper to create a database by getWritableDatabase() method
    private ExamTrackOpenHelper openHelper;
    private SQLiteDatabase database;

    public ExamDatabaseHelper(Context context){
        openHelper  = new ExamTrackOpenHelper(context);
        database    = openHelper.getWritableDatabase();
    }

    //FAKE FUNCTION
    //TO DO:
    // 1. iterate calling getStudentExamPaper to get the paper code
    // 2. return the exam paper info using SELECT * FROM EXAM_TABLE WHERE paperCode = '...'
    public Cursor getExamTable(){
        return database.rawQuery("SELECT * FROM " + EXAM_TABLE, null);
    }



    //**********************INTERNAL CLASS***************************
    private class ExamTrackOpenHelper extends SQLiteOpenHelper {

        ExamTrackOpenHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        public void onCreate(SQLiteDatabase database){
            database.execSQL("CREATE TABLE " + EXAM_TABLE + "( "
                    + EXAM_INFO_COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + EXAM_INFO_COLUMN_CODE + " TEXT, "
                    + EXAM_INFO_COLUMN_DESC + " TEXT, "
                    + EXAM_INFO_COLUMN_DATE + " TEXT, "
                    + EXAM_INFO_COLUMN_SESSION + " TEXT, "
                    + EXAM_INFO_COLUMN_VENUE + " TEXT )");


            database.execSQL("INSERT INTO " + EXAM_TABLE + " ("
                    + EXAM_INFO_COLUMN_CODE + ", "
                    + EXAM_INFO_COLUMN_DESC + ", "
                    + EXAM_INFO_COLUMN_DATE + ", "
                    + EXAM_INFO_COLUMN_SESSION + ", "
                    + EXAM_INFO_COLUMN_VENUE + ")"
                    + " VALUES ('BAME2134', 'TEST DRIVEN DEVELOPEMENT', '15 03 2016', 'AM', 'H3')");
            database.execSQL("INSERT INTO " + EXAM_TABLE + " ("
                    + EXAM_INFO_COLUMN_CODE + ", "
                    + EXAM_INFO_COLUMN_DESC + ", "
                    + EXAM_INFO_COLUMN_DATE + ", "
                    + EXAM_INFO_COLUMN_SESSION + ", "
                    + EXAM_INFO_COLUMN_VENUE + ")"
                    + " VALUES ('BAME2014', 'DIGITAL SYSTEM DESIGN', '17 03 2016', 'PM', 'H1')");
            database.execSQL("INSERT INTO " + EXAM_TABLE + " ("
                    + EXAM_INFO_COLUMN_CODE + ", "
                    + EXAM_INFO_COLUMN_DESC + ", "
                    + EXAM_INFO_COLUMN_DATE + ", "
                    + EXAM_INFO_COLUMN_SESSION + ", "
                    + EXAM_INFO_COLUMN_VENUE + ")"
                    + " VALUES ('BAME222', 'ELECTROMAGNETISM', ' 19 03 2016', 'AM', 'PA2')");
        }

        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
            database.execSQL("DROP TABLE IF EXIST " + EXAM_TABLE);
            onCreate(database);
        }

    }
}

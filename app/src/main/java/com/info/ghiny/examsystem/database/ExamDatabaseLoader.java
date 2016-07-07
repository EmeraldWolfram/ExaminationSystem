package com.info.ghiny.examsystem.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GhinY on 13/05/2016.
 */
public class ExamDatabaseLoader {

    private static final int DATABASE_VERSION   = 22;
    private static final String DATABASE_NAME   = "examSysDB";
    private static final String EXAM_TABLE      = "ExamPaper";
    private static final String IDENTITY_TABLE  = "IdentityInfo";

    //EXAM INFO TABLE
    public static final String EXAM_INFO_ID             = "_id";
    public static final String EXAM_INFO_COLUMN_CODE    = "Code";
    public static final String EXAM_INFO_COLUMN_DESC    = "Desc";
    public static final String EXAM_INFO_COLUMN_SESSION = "Session";
    public static final String EXAM_INFO_COLUMN_VENUE   = "Venue";
    public static final String EXAM_INFO_COLUMN_DATE    = "Date";

    //STUDENT INFO TABLE
    public static final String IDENTITY_COLUMN_ID       = "_id";
    public static final String IDENTITY_COLUMN_IC       = "IC";
    public static final String IDENTITY_COLUMN_REG      = "RegNum";
    public static final String IDENTITY_COLUMN_NAME     = "Name";
    public static final String IDENTITY_COLUMN_PASS     = "Password";
    public static final String IDENTITY_COLUMN_LEGIT    = "Eligible";
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

    public ExamDatabaseLoader(Context context){
        openHelper  = new ExamTrackOpenHelper(context);
        database    = openHelper.getWritableDatabase();
    }

    //FAKE FUNCTION
    //TO DO:
    // 1. iterate calling getStudentExamPaper to get the paper code
    // 2. return the exam paper info using SELECT * FROM EXAM_TABLE WHERE paperCode = '...'

    public StaffIdentity getIdentity(String icNumber){
        StaffIdentity staffIdentity = new StaffIdentity();
        boolean legit = false;

        Cursor pointer = database.rawQuery("SELECT * FROM "  + IDENTITY_TABLE + " WHERE "
                + IDENTITY_COLUMN_IC + " = ?", new String[]{icNumber});

        if(pointer.moveToFirst()){
            if(pointer.getInt(pointer.getColumnIndex(IDENTITY_COLUMN_LEGIT)) != 0)
                legit = true;

            staffIdentity.setRegNum(pointer.getString(pointer.getColumnIndex(IDENTITY_COLUMN_REG)));
            staffIdentity.setName(pointer.getString(pointer.getColumnIndex(IDENTITY_COLUMN_NAME)));
            staffIdentity.setEligible(legit);
            staffIdentity.setPassword(pointer.getString(pointer.getColumnIndex(IDENTITY_COLUMN_PASS)));

            pointer.close();
            return staffIdentity;
        } else{
            pointer.close();
            return null;
        }
    }

    public List<String> getPapersExamine(String regNum){
        return new ArrayList<>();
    }

    public ExamSubject getPaperInfo(String paperCode){
        return new ExamSubject();
    }

    //**********************INTERNAL CLASS***************************
    private class ExamTrackOpenHelper extends SQLiteOpenHelper {

        private String CREATE_EXAM_TABLE = "CREATE TABLE " + EXAM_TABLE + "( "
                + EXAM_INFO_ID    + " INTEGER PRIMARY KEY, "
                + EXAM_INFO_COLUMN_CODE + " TEXT, "
                + EXAM_INFO_COLUMN_DESC + " TEXT, "
                + EXAM_INFO_COLUMN_DATE + " TEXT, "
                + EXAM_INFO_COLUMN_SESSION + " TEXT, "
                + EXAM_INFO_COLUMN_VENUE + " TEXT )";

        private String CREATE_IDENTITY_TABLE = "CREATE TABLE " + IDENTITY_TABLE + "( "
                + IDENTITY_COLUMN_ID    + " INTEGER PRIMARY KEY, "
                + IDENTITY_COLUMN_NAME  + " TEXT, "
                + IDENTITY_COLUMN_REG   + " TEXT, "
                + IDENTITY_COLUMN_IC    + " TEXT, "
                + IDENTITY_COLUMN_PASS  + " TEXT, "
                + IDENTITY_COLUMN_LEGIT + " INTEGER, "
                + IDENTITY_COLUMN_EXAM1 + " TEXT, "
                + IDENTITY_COLUMN_EXAM2 + " TEXT, "
                + IDENTITY_COLUMN_EXAM3 + " TEXT, "
                + IDENTITY_COLUMN_EXAM4 + " TEXT, "
                + IDENTITY_COLUMN_EXAM5 + " TEXT, "
                + IDENTITY_COLUMN_EXAM6 + " TEXT, "
                + IDENTITY_COLUMN_EXAM7 + " TEXT, "
                + IDENTITY_COLUMN_EXAM8 + " TEXT )";

        private String INSERT_SUBJECT = "INSERT INTO " + EXAM_TABLE + " ("
                + EXAM_INFO_COLUMN_CODE + ", "
                + EXAM_INFO_COLUMN_DESC + ", "
                + EXAM_INFO_COLUMN_DATE + ", "
                + EXAM_INFO_COLUMN_SESSION + ", "
                + EXAM_INFO_COLUMN_VENUE + ")";

        private String INSERT_INV = "INSERT INTO " + IDENTITY_TABLE + " ("
                + IDENTITY_COLUMN_IC    + ", "
                + IDENTITY_COLUMN_REG   + ", "
                + IDENTITY_COLUMN_NAME  + ", "
                + IDENTITY_COLUMN_LEGIT + ", "
                + IDENTITY_COLUMN_PASS  + ")";

        ExamTrackOpenHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        public void onCreate(SQLiteDatabase database){
            database.execSQL(CREATE_EXAM_TABLE);
            database.execSQL(CREATE_IDENTITY_TABLE);

            database.execSQL(INSERT_SUBJECT + " VALUES ('BAME2134', 'TEST DRIVEN DEVELOPEMENT', '15 03 2016', 'AM', 'H3')");
            database.execSQL(INSERT_SUBJECT + " VALUES ('BAME2014', 'DIGITAL SYSTEM DESIGN', '17 03 2016', 'PM', 'H1')");
            database.execSQL(INSERT_SUBJECT + " VALUES ('BAME2222', 'ELECTROMAGNETISM', '19 03 2016', 'AM', 'PA2')");

            database.execSQL(INSERT_INV + " VALUES ('710808088888', '88WWW88888', 'DR. POH TZE VEN', 1, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('641212121212', '66WWW66666', 'DR. LOKE CHUI FUNG', 1, '0123')");
            database.execSQL(INSERT_INV + " VALUES ('951108106303', '15WAU09184', 'FOONG GHIN YEW', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('921212121212', '15WAD88888', 'NG YEN AENG', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('951111100001', '15WAD00001', 'FGY', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('951111100002', '15WAD00002', 'NYN', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('951111100003', '15WAD00003', 'LHN', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('951111100004', '15WAD00004', 'YZL', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('951111100005', '15WAD00005', 'SYL', 0, '63686689')");
            database.execSQL(INSERT_INV + " VALUES ('951111100006', '15WAD00006', 'WJS', 0, '63686689')");
        }

        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
            database.execSQL("DROP TABLE IF EXIST " + EXAM_TABLE);
            database.execSQL("DROP TABLE IF EXIST " + IDENTITY_TABLE);
            onCreate(database);
        }

    }
}

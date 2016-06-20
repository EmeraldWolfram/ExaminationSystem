package com.info.ghiny.examsystem.database;


import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 20/06/2016.
 */
public class JdbcDatabaseTest extends TestCase {

    protected String driverName = "org.sqldroid.SQLDroidDriver";

    /** Common prefix for creating JDBC URL */
    protected String JDBC_URL_PREFIX = "jdbc:sqlite:";

    /** Package name of this app */
    protected String packageName = "com.info.ghinny.examsystem";

    /** Database file directory for this app on Android */
    protected String DB_DIRECTORY = "/data/data/" + packageName + "/";

    /** Name of an in-memory database */
    protected String dummyDatabase = "dummydatabase.db";

    /** The URL to the in-memory database. */
    protected String databaseURL = JDBC_URL_PREFIX + dummyDatabase;

    /** The table create statement. */
    protected String createTable = "CREATE TABLE dummytable (name VARCHAR(254), value int)";

    /** Some data for the table. */
    protected String[] inserts = {
            "INSERT INTO dummytable(name,value) VALUES('Apple', 100)",
            "INSERT INTO dummytable(name,value) VALUES('Orange', 200)",
            "INSERT INTO dummytable(name,value) VALUES('Banana', 300)",
            "INSERT INTO dummytable(name,value) VALUES('Kiwi', 400)"};

    /** A select statement. */
    protected String select = "SELECT * FROM dummytable WHERE value < 250";

    /** Constructor. */
    public JdbcDatabaseTest (String name) {
        super(name);
    }

    /**
     * Creates the directory structure for the database file and loads the JDBC driver.
     * @param dbFile the database file name
     * @throws Exception
     */
    protected void setupDatabaseFileAndJDBCDriver(String dbFile) throws Exception {
        // If the database file already exists, delete it, else create the parent directory for it.
        File f = new File(dbFile);
        if ( f.exists() ) {
            f.delete();
        } else {
            if (null != f.getParent()) {
                f.getParentFile().mkdirs();
            }
        }
        // Loads and registers the JDBC driver
        DriverManager.registerDriver((Driver)(Class.forName(driverName, true, getClass().getClassLoader()).newInstance()));
    }


    public void testEstaConnection() throws Exception {
        //String dbName = "bolbtest.db";
        //String dbFile = DB_DIRECTORY + dbName;
        //setupDatabaseFileAndJDBCDriver(dbFile);

//        Connection con = DriverManager.getConnection(JDBC_URL_PREFIX + dbFile);

    }


    public void testCreateTableIfNotExist() throws Exception {

    }

    public void testClearDatabase() throws Exception {

    }

    public void testSaveAttendanceList() throws Exception {

    }

    public void testGetLastSavedAttendanceList() throws Exception {

    }

    public void testIsEmpty() throws Exception {

    }
}
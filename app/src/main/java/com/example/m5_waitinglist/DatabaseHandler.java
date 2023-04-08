package com.example.m5_waitinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.LinkedHashMap;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "REGISTRATION";
    private static final String STUDENTS_TABLE = "STUDENTS";
    private static final String COURSES_TABLE = "COURSES";
    private static final String WAITLIST_TABLE = "COURSE_WAITLIST";
    // Hardcode this as I was unable to get resources from strings.xml within non activity class
    String[] priority = { "Freshman", "Sophomore", "Junior", "Senior", "Graduate"};

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create DB and tables if they do not already exist
    // IF NOT EXISTS check is redundant as onCreate should only run if DB does not exist.
    // UNIQUE applied to prevent duplicate entries without needing to check within app logic.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + STUDENTS_TABLE +
                "( ID INTEGER PRIMARY KEY," +
                "STUDENT_ID TEXT NOT NULL UNIQUE," +
                "NAME TEXT NOT NULL," +
                "PRIORITY INTEGER NOT NULL )";
        String CREATE_COURSE_TABLE = "CREATE TABLE IF NOT EXISTS " + COURSES_TABLE +
                "( ID INTEGER PRIMARY KEY," +
                "COURSE_NAME TEXT NOT NULL UNIQUE )";
        String CREATE_WAITLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + WAITLIST_TABLE +
                "( ID INTEGER PRIMARY KEY," +
                "COURSE_ID INTEGER NOT NULL," +
                "STUDENT_ID INTEGER NOT NULL," +
                "UNIQUE(COURSE_ID,STUDENT_ID))";

        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
        db.execSQL(CREATE_WAITLIST_TABLE);
    }

    // Purge and upgrade DB - Not used
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WAITLIST_TABLE);
        onCreate(db);
    }


    // Add Student record
    void addStudentRec(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues record = new ContentValues();

        record.put("STUDENT_ID", student.getStudentID());
        record.put("NAME", student.getName());
        record.put("PRIORITY", student.getPriority());
        db.insert(STUDENTS_TABLE, null, record);
        //Log.d("addStudent().db.insert", "STUDENTS, null, " + record);
        db.close();
    }

    // Update Student record. ID is STUDENTS Primary key
    void editStudentRec(int ID, Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues record = new ContentValues();
        record.put("STUDENT_ID", student.getStudentID());
        record.put("NAME", student.getName());
        record.put("PRIORITY", student.getPriority());
        db.update(STUDENTS_TABLE, record, "ID=?", new String[]{String.valueOf(ID)});
        db.close();
    }

    // Remove student record, along with any wait list reservations they had
    void removeStudentRec(int ID){
        //Log.d("removeStudent","dump waitlist before delete op");
        logdumpDBtable(WAITLIST_TABLE);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WAITLIST_TABLE, "STUDENT_ID=" + ID, null);
        db.delete(STUDENTS_TABLE, "ID=" + ID, null);
        db.close();
        //Log.d("removeStudent","dump waitlist after delete op");
        logdumpDBtable(WAITLIST_TABLE);
    }

    public Student getStudent(int ID) { // Get single student record
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(STUDENTS_TABLE, new String[]{"STUDENT_ID, NAME, PRIORITY"},
                "ID=?", new String[]{String.valueOf(ID)}, null, null, null);
        cursor.moveToFirst();
        //Log.d("DatabaseHandler", "ID: " + cursor.getString(0));
        Student student = new Student(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
        cursor.close();
        db.close();
        return student;
    }

    // Add Course
    void addCourseRec(String course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues record = new ContentValues();
        record.put("COURSE_NAME", course);
        db.insert(COURSES_TABLE, null, record);
        db.close();
    }

    // Remove Course
    void removeCourseRec(int ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WAITLIST_TABLE, "COURSE_ID=" + ID, null);
        db.delete(COURSES_TABLE, "ID=" + ID, null);
        db.close();
    }

    public LinkedHashMap<Integer, String> GetCourseList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(COURSES_TABLE, new String[]{"ID, COURSE_NAME"},
                null, null, null, null, "COURSE_NAME", null);
        LinkedHashMap<Integer, String> course_list = new LinkedHashMap<>();

        while (cursor.moveToNext()) {
            course_list.put(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();
        db.close();
        return course_list;
    }

    // Add to wait list
    void addStudentToWaitList(int courseID, int studentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues record = new ContentValues();
        record.put("COURSE_ID", courseID);
        record.put("STUDENT_ID", studentID);
        db.insert(WAITLIST_TABLE, null, record);
        db.close();
        //Log.d("DBhandler.addStudentToWaitList", "courseID:" + courseID + " studentID:" + studentID);
    }

    public LinkedHashMap<Integer, String> getStudentNames() { // Get list of all students
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(STUDENTS_TABLE, new String[]{"ID, NAME"},
                null, null, null, null, "NAME", null);
        LinkedHashMap<Integer, String> student_list = new LinkedHashMap<>();

        while (cursor.moveToNext()) {
            student_list.put(cursor.getInt(0), cursor.getString(1));
            //Log.d("GetStudents","ID: " + cursor.getInt(0) + "NAME:" + cursor.getString(1) );
        }
        cursor.close();
        db.close();
        return student_list;
    }

    // Return list of students on waitlist for specific course, sorted by priorty, then by order of addition
    public LinkedHashMap<Integer, String> GetStudentsOnWaitList(int courseID) {
        SQLiteDatabase db = this.getReadableDatabase();
        LinkedHashMap<Integer, String> student_list = new LinkedHashMap<>();
        Cursor cursor = db.rawQuery(
                "SELECT COURSE_WAITLIST.ID, STUDENTS.NAME, STUDENTS.PRIORITY" +
                        " FROM STUDENTS INNER JOIN COURSE_WAITLIST " +
                        " ON STUDENTS.ID = COURSE_WAITLIST.STUDENT_ID" +
                        " WHERE COURSE_WAITLIST.COURSE_ID = " + courseID +
                        " ORDER BY STUDENTS.PRIORITY DESC, COURSE_WAITLIST.ID ASC", null);
        while (cursor.moveToNext()) {
            // Append student priorty text to their waitlist display entry.
            student_list.put(cursor.getInt(0), cursor.getString(1) + " - " + priority[cursor.getInt(2)] );
        }
        cursor.close();
        db.close();
        //Log.d("GetStudentOnWL","logdump");
        logdumpDBtable("COURSE_WAITLIST");
        return student_list;
    }

    public void RemoveStudentFromWaitList(int WLID){
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d("DB.RemStudentFromWaitList", "ID: " + WLID);
        db.delete(WAITLIST_TABLE, "ID=" + WLID, null);
        db.close();
    }

    // Test and debug helper methods

    public void logdumpDBtable(String table) {
        //Log.d("logdumpDBtable", "TABLE: " + table);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        //Cursor cursor = db.rawQuery("SELECT * FROM " + table + " ORDER BY " + table + ".PRIORITY DESC", null);
        while (cursor.moveToNext()) {
            Log.d("logdumpDBtable", cursor.getInt(0) + " " + cursor.getInt(1) + " " + cursor.getInt(2));
            //Log.d("logdumpDBtable", cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getInt(3));
        }
        cursor.close();
        db.close();
    }

    public void purgeTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + table);
        String CREATE_WAITLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + WAITLIST_TABLE +
                "( ID INTEGER PRIMARY KEY," +
                "COURSE_ID INTEGER NOT NULL," +
                "STUDENT_ID INTEGER NOT NULL," +
                "UNIQUE(COURSE_ID,STUDENT_ID))";
        db.execSQL(CREATE_WAITLIST_TABLE);
    }
}
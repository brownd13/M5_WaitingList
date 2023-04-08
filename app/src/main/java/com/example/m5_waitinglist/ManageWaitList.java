package com.example.m5_waitinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ManageWaitList extends AppCompatActivity {

    int courseSelection = -1; // indicates no selection made yet
    int studentSelection = -1;
    ArrayList<Integer> courseIDlist = new ArrayList<>();
    ArrayList<String> courseNameList = new ArrayList<>();
    ArrayList<Integer> waitListIDs = new ArrayList<>();
    ArrayList<String> studentNameList = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);

    ArrayAdapter studentAdapter;
    ListView lvStudent;
    ListView lvCourse;
    View prevStudentSelectionView = null; // used to reset list selection color fill.
    View prevCourseSelectionView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_wait_list);
        
        // Present data from key/name hashmap into arrays for ListView display and later DB calls
        LinkedHashMap<Integer,String> courseList = db.GetCourseList();
        Iterator iter = courseList.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            courseIDlist.add((Integer) entry.getKey());
            courseNameList.add((String) entry.getValue());
        }
        
        // ListView for Course list
        ArrayAdapter courseAdapter = new ArrayAdapter<>(this, R.layout.course_list, R.id.courseName, courseNameList);
        lvCourse = findViewById(R.id.course_list);
        lvCourse.setAdapter(courseAdapter);

        // ListView for Students in Wait List
        studentAdapter = new ArrayAdapter<>(this, R.layout.wait_list, R.id.waitList_studentName, studentNameList);
        lvStudent = findViewById(R.id.wait_list);
        lvStudent.setAdapter(studentAdapter);

        // Click handler for Course List
        lvCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                courseSelection = i; // i is index of selected list item
                view.setBackgroundColor(Color.GRAY);
                if ( prevCourseSelectionView != null ){
                    prevCourseSelectionView.setBackgroundColor(Color.WHITE);
                }
                prevCourseSelectionView = view;

                displayWaitList();
            }
        });

        // Click handler for students in Wait List
        lvStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Highlight selected element in listview, and clear previous selection
                if ( prevStudentSelectionView != null ){
                    prevStudentSelectionView.setBackgroundColor(Color.WHITE);
                }
                view.setBackgroundColor(Color.GRAY);
                prevStudentSelectionView = view;
                studentSelection = i;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Redraw student list when restarting activity, after returning from addStudentToWL activity
        displayWaitList();
    }

    private void displayWaitList() {
        // Reset for new list display
        waitListIDs.clear();
        studentNameList.clear();
        if ( prevStudentSelectionView != null ){
            prevStudentSelectionView.setBackgroundColor(Color.WHITE);
        }
        studentSelection = -1;

        // Get list of students in selected course waitlist and display in its own ListView
        LinkedHashMap<Integer, String> waitListStudents = db.GetStudentsOnWaitList(courseIDlist.get(courseSelection));
        Iterator iter = waitListStudents.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            waitListIDs.add((Integer) entry.getKey());
            studentNameList.add((String) entry.getValue());
        }
        // refresh Student ListView/adapter with updated list for the course
        studentAdapter.notifyDataSetChanged();
    }


    public void addStudentToWLAct(View view) { // setup to call activity to handle waitlist addition
         if (courseSelection == -1){
            Toast.makeText(this, R.string.toMakeSelection, Toast.LENGTH_LONG).show();
        } else {
             //Log.d("ML.addStudent", "courseSelection: " + courseSelection);
            int courseID = courseIDlist.get(courseSelection);
            String courseName = courseNameList.get(courseSelection);
            Intent intent = new Intent(this, addStudentToWL.class);
            intent.putExtra("COURSEID", courseID);
            intent.putExtra("COURSENAME", courseName);
            startActivity(intent);
        }
    }

    public void remStudentFromWaitList(View view) { // performed without additional activity
        if (studentSelection == -1){
            Toast.makeText(this, R.string.toMakeSelection, Toast.LENGTH_LONG).show();
        } else {
            db.RemoveStudentFromWaitList(waitListIDs.get(studentSelection));
            // Remove entry from index/name list array to allow multiple delete operations to occur
            // from the same course waiting list, without reloading list from DB
            waitListIDs.remove(studentSelection);
            studentNameList.remove(studentSelection);
            studentSelection = -1;
            // Update on screen list after delete operation.
            prevStudentSelectionView.setBackgroundColor(Color.WHITE);
            studentAdapter.notifyDataSetChanged();
        }
    }

    public void back(View view) { finish(); }
}
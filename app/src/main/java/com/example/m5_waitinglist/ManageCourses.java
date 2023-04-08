package com.example.m5_waitinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ManageCourses extends AppCompatActivity {

    int selection = -1; // indicates no selection made yet
    View prevCourseSelectionView = null; // used to reset list selection color fill.
    ArrayList<Integer> courseIDlist = new ArrayList<>();
    ArrayList<String> courseNameList = new ArrayList<>();
    ArrayAdapter adapter;
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);


        adapter = new ArrayAdapter<>(this, R.layout.course_list, R.id.courseName, courseNameList);
        ListView lv = findViewById(R.id.course_list);
        lv.setAdapter(adapter);

        displayCourseList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Highlight selected element in listview
                selection = i;
                view.setBackgroundColor(Color.GRAY);
                if ( prevCourseSelectionView != null) { // reset to white only if previous selection view has been set
                    prevCourseSelectionView.setBackgroundColor(Color.WHITE);
                }
                prevCourseSelectionView = view;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Redraw course list when restarting activity
        displayCourseList();

    }

    private void displayCourseList() {
        HashMap<Integer,String> courseList =  db.GetCourseList();
        Iterator iter = courseList.entrySet().iterator();
        courseIDlist.clear();
        courseNameList.clear();
        // Present data from key/name hashmap into arrays for ListView display and later DB call
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            courseIDlist.add((Integer) entry.getKey());
            courseNameList.add((String) entry.getValue());
        }
        adapter.notifyDataSetChanged();
    }

    public void addCourse(View view) {
        Intent intent = new Intent(this, addCourseAct.class);
        startActivity(intent);
    }

    public void removeCourse(View view) {
        if (selection == -1){
            Toast.makeText(this, R.string.toMakeSelection, Toast.LENGTH_LONG).show();
        } else {
            db.removeCourseRec(courseIDlist.get(selection));
            displayCourseList();
        }
    }



    public void back(View view) { finish(); }
}
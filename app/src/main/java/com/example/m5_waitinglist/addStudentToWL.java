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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class addStudentToWL extends AppCompatActivity {

    int courseID = 0; // To be passed in via intent
    int selection = -1; // indicates no selection made yet
    View prevStudentWLSelectionView = null; // used to reset list selection color fill.
    ArrayList<Integer> studentIDlist = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_wl);

        // Get ID KEY from user selection
        Intent intent = getIntent();
        courseID = intent.getIntExtra("COURSEID", 0);
        String courseName = intent.getStringExtra("COURSENAME");
        TextView tvCourseName = findViewById(R.id.tvCourseName);
        tvCourseName.setText( courseName);

        // Present data from key/name hashmap into arrays for ListView display and later DB call
        LinkedHashMap<Integer,String> student_names =  db.getStudentNames();
        Iterator iter = student_names.entrySet().iterator();
        ArrayList<String> nameList = new ArrayList<>();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            studentIDlist.add((Integer) entry.getKey());
            nameList.add((String) entry.getValue());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.student_list, R.id.studentName, nameList);
        ListView lv = findViewById(R.id.student_list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Highlight selected element in listview
                view.setBackgroundColor(Color.GRAY);
                if(prevStudentWLSelectionView != null) { // reset to white only if previous selection view has been set
                    prevStudentWLSelectionView.setBackgroundColor(Color.WHITE);
                }
                prevStudentWLSelectionView = view;
                selection = i;
            }
        });
    }

    public void addStudentToWLonClick(View view) {
        if( selection == -1 ){
            Toast.makeText(this, R.string.toMakeSelection, Toast.LENGTH_LONG).show();
        } else {
            //Log.d("addStudentToWL.addStudent", "courseID:" + courseID + " studentID:" + studentIDlist.get(selection));
            db.addStudentToWaitList(courseID, studentIDlist.get(selection));
            finish();
        }
    }

    public void back(View view) { finish(); }
}
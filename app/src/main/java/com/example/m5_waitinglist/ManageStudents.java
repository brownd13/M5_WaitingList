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

public class ManageStudents extends AppCompatActivity {

    int selection = -1; // -1 indicates no selection made
    View prevStudentSelectionView = null; // used to reset list selection color fill.
    ArrayAdapter adapter;
    ArrayList<Integer> IDlist = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        // Set ListView and array adapters for student list display
        adapter = new ArrayAdapter<>(this, R.layout.student_list, R.id.studentName, nameList);
        ListView lv = findViewById(R.id.student_list);
        lv.setAdapter(adapter);
        displayStudentList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selection = i;
                // Highlight selected element in listview
                view.setBackgroundColor(Color.GRAY);
                if(prevStudentSelectionView != null) { // reset to white only if previous selection view has been set
                    prevStudentSelectionView.setBackgroundColor(Color.WHITE);
                }
                prevStudentSelectionView = view;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Redraw student list when restarting activity, after returning from addStudentToWL activity
        displayStudentList();

    }

    private void displayStudentList() {
        // Present data from key/name hashmap into cleared arrays for ListView display and later DB call
        HashMap<Integer, String> student_names = db.getStudentNames();
        Iterator iter = student_names.entrySet().iterator();
        IDlist.clear();
        nameList.clear();
        if(prevStudentSelectionView != null) { // reset to white only if previous selection view has been set
            prevStudentSelectionView.setBackgroundColor(Color.WHITE);
        }
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            IDlist.add((Integer) entry.getKey());
            nameList.add((String) entry.getValue());
        }
        adapter.notifyDataSetChanged();
    }


    public void addStudent(View view) {
        Intent intent = new Intent(this, addStudentAct.class);
        startActivity(intent);
    }
    public void editStudent(View view) {
        if (selection == -1){
            Toast.makeText(this, R.string.toMakeSelection, Toast.LENGTH_LONG).show();
        } else {
            int ID = IDlist.get(selection);
            Intent intent = new Intent(this, editStudentAct.class);
            intent.putExtra("ID", ID);
            startActivity(intent);
        }
    }

    public void removeStudentRec(View view) {
        if (selection == -1){
            Toast.makeText(this, R.string.toMakeSelection, Toast.LENGTH_LONG).show();
        } else {
            db.removeStudentRec(IDlist.get(selection));
            displayStudentList();
        }
    }

    public void back(View view) { finish(); }
}
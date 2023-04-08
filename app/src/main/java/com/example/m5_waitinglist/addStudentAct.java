package com.example.m5_waitinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class addStudentAct extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    int priority = 0;
    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_student);


        // Setup spinner for grade level priority selection
        Spinner spinner = findViewById(R.id.spPriority);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                ( this, R.array.priority_selection, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        //Log.d("onItemSelected", "pos" + position);
        priority = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Auto-generated method stub
    }

    public void addStudent(View view) {
        EditText etStudentName = findViewById(R.id.etStudentName);
        EditText etStudentID = findViewById(R.id.etStudentID);
        String studentName = etStudentName.getText().toString();
        String studentID = etStudentID.getText().toString();

        // Check for empty fields
        if( studentName.isEmpty() || studentID.isEmpty()) {
            Toast.makeText(this, R.string.toMissingField, Toast.LENGTH_LONG).show();
        } else { // Add student record
            db.addStudentRec(new Student(studentID, studentName, priority));
            finish();
        }
    }

    public void back(View view) { finish(); }
}
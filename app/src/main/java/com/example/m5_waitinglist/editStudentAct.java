package com.example.m5_waitinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class editStudentAct extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    int priority = 0;
    int ID = 0;
    private DatabaseHandler db = new DatabaseHandler(this);
    EditText etStudentName;
    EditText etStudentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        // Setup spinner for grade level priority selection
        Spinner spinner = findViewById(R.id.spPriority);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                ( this, R.array.priority_selection, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Get ID KEY from user selection from prev activity. Get current student info from DB
        Intent intent = getIntent();
        ID = intent.getIntExtra("ID", 0);
        Student student = db.getStudent(ID);
        etStudentName = findViewById(R.id.etStudentName);
        etStudentID = findViewById(R.id.etStudentID);
        etStudentName.setText(student.getName());
        etStudentID.setText(student.getStudentID());
        spinner.setSelection(student.getPriority());
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

    public void editStudentRec(View view) {
        String studentName = etStudentName.getText().toString();
        String studentID = etStudentID.getText().toString();

        // Check for empty fields
        if( studentName.isEmpty() || studentID.isEmpty()) {
            Toast.makeText(this, R.string.toMissingField, Toast.LENGTH_LONG).show();
        } else { // Add student record
            db.editStudentRec(ID, new Student( studentID, studentName, priority));
            finish();
        }
    }

   public void back(View view) { finish(); }
}
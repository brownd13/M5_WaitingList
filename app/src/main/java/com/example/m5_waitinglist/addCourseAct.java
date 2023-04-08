package com.example.m5_waitinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class addCourseAct extends AppCompatActivity {

    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
    }

    public void addCourse(View view) {

        EditText etCourseID = findViewById(R.id.etCourseID);
        String courseID = etCourseID.getText().toString();

        // Check for empty fields
        if( courseID.isEmpty() ) {
            Toast.makeText(this, R.string.toMissingField, Toast.LENGTH_LONG).show();
        } else { // Add course record
            db.addCourseRec(courseID);
            finish();
        }
    }

    public void back(View view) { finish(); }

}
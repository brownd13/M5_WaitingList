package com.example.m5_waitinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void manageStudents(View view) {
        Intent intent = new Intent(this, ManageStudents.class);
        startActivity(intent);
    }

    public void manageCourses(View view) {
        Intent intent = new Intent(this, ManageCourses.class);
        startActivity(intent);
    }

    public void manageWaitList(View view) {
        Intent intent = new Intent(this, ManageWaitList.class);
        startActivity(intent);
    }
}
package com.example.m5_waitinglist;

public class Student {
    private String _studentID;
    private String _name;
    private int _priority;

    //public Student(){}
    public Student(String studentID, String name, int priority){
        _studentID = studentID;
        _name = name;
        _priority = priority;
    }

    // Getter methods. No setters used. New instances always build from constructor
    public String getStudentID(){ return _studentID; }
    public String getName(){ return _name; }
    public int getPriority() { return _priority; }
}

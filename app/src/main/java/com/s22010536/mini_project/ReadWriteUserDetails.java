package com.s22010536.mini_project;

public class ReadWriteUserDetails {
    public String fullName, program, location, sid, role, department, tid;

    // Default constructor
    public ReadWriteUserDetails() {}

    // Constructor for non-teachers
    public ReadWriteUserDetails(String textName, String textProgram, String textLocation, String textSid) {
        this.fullName = textName;
        this.program = textProgram;
        this.location = textLocation;
        this.sid = textSid;

    }

    // Constructor for teachers
    public ReadWriteUserDetails(String textName, String textDepartment, String textLocation, String textTid, String textRole) {
        this.fullName = textName;
        this.department = textDepartment;
        this.location = textLocation;
        this.tid = textTid;
        this.role = textRole;
    }
}

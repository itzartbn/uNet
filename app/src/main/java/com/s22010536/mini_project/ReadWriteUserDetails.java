package com.s22010536.mini_project;

public class ReadWriteUserDetails {
    public String email, program, location, sid;

    //constructor
    public ReadWriteUserDetails(){};


    public ReadWriteUserDetails(String textEmail, String textProgram, String textLocation, String textSid){

        this.email = textEmail;
        this.program = textProgram;
        this.location = textLocation;
        this.sid = textSid;
    }
}

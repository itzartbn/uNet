package com.s22010536.mini_project;

public class Teacher {
    private String email;
    private String role;

    public Teacher() {
        // Public no-argument constructor needed
    }

    public Teacher(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

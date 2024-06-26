// Task.java
package com.s22010536.mini_project;

public class Task {
    private String taskId;
    private String taskTitle;
    private String taskDateTime;
    private String taskDescription;
    private String department;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    }

    public Task(String taskId, String taskTitle, String taskDateTime, String taskDescription, String department) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDateTime = taskDateTime;
        this.taskDescription = taskDescription;
        this.department = department;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDateTime() {
        return taskDateTime;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

}

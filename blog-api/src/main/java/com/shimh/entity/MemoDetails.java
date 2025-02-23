package com.shimh.entity;

import java.util.UUID;

public class MemoDetails {

    private String id;  // Unique ID
    private String title;
    private String body;
    private String createdDate;
    private String dueDate;

    // Constructors, getters, and setters

    public MemoDetails(String title, String body, String createdDate, String dueDate) {
        this.id = UUID.randomUUID().toString();  // Automatically generate ID
        this.title = title;
        this.body = body;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
// Other getters and setters
}

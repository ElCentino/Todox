package com.example.todox.models;

import java.util.Date;

public class TodoItem {

    private String Id;
    private String Title;
    private String Description;
    private boolean IsCompleted;
    private Date CreatedAt;
    private Date CompletedAt;

    public Date getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        UpdatedAt = updatedAt;
    }

    private Date UpdatedAt;

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        CreatedAt = createdAt;
    }

    public Date getCompletedAt() {
        return CompletedAt;
    }

    public void setCompletedAt(Date completedAt) {
        CompletedAt = completedAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean getIsCompleted() {
        return IsCompleted;
    }

    public void setCompleted(boolean completed) {
        IsCompleted = completed;
    }

    public TodoItem(String id, String title, String description, boolean isCompleted) {
        Id = id;
        Title = title;
        Description = description;
        IsCompleted = isCompleted;
    }

    public TodoItem() {}

}

package com.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

public class TodoRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
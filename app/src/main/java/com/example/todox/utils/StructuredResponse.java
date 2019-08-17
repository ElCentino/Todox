package com.example.todox.utils;

public class StructuredResponse {

    public StructuredResponse(int status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public int status;
    public String message;
    public Object payload;

    public StructuredResponse() {

    }
}

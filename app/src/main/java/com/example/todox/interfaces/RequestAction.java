package com.example.todox.interfaces;

import com.example.todox.services.ApplicationServices;

public interface RequestAction {
    void onRequestStart(Object object);
    void onRequestEnd(Object object);
    void onRequestComplete(ApplicationServices.Constants constant);
}

package com.example.todox.services;

import com.example.todox.interfaces.StructuredCallback;
import com.example.todox.models.TodoItem;

import java.util.ArrayList;
import java.util.UUID;

public class TodoServicesDummy {

    private static TodoServicesDummy mTodoService;

    private TodoServicesDummy() {}

    public static TodoServicesDummy getInstance() {

        if(mTodoService == null) {
            mTodoService = new TodoServicesDummy();
        }

        return mTodoService;
    }

    public ArrayList<TodoItem> getTodoList(StructuredCallback structuredCallback) {

        ArrayList<TodoItem> todos = new ArrayList<>();
        todos.add(new TodoItem(UUID.randomUUID().toString(), "Pickup Sally", "Just get her up by 3:00PM", false));
        todos.add(new TodoItem(UUID.randomUUID().toString(), "Find a football for Harry", "Get it from Frankie's", false));
        todos.add(new TodoItem(UUID.randomUUID().toString(), "Get Food", "Just Buy rice", false));


        structuredCallback.OnStructuredCallbackResolveItems(todos);

        return todos;
    }
}

package com.example.todox.interfaces;

import com.example.todox.models.TodoItem;

public interface TodoViewed {
    void onTodoViewed(TodoItem todoItem);
    void onTodoDeleted(TodoItem todoItem);
}

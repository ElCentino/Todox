package com.example.todox.services;

import com.example.todox.models.TodoItem;
import com.example.todox.utils.StructuredResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface TodoServices {

    @GET("/mobile/api/todos/get")
    Call<List<TodoItem>> GetTodos(@Header("Authorization") String authkey);

    @POST("/mobile/api/todos/add")
    Call<StructuredResponse> AddTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);

    @PUT("/mobile/api/todos/complete")
    Call<StructuredResponse> CompleteTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);

    @PUT("/mobile/api/todos/uncomplete")
    Call<StructuredResponse> UncompleteTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);
}

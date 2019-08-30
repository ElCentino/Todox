package com.example.todox.services;

import com.example.todox.models.TodoItem;
import com.example.todox.utils.StructuredResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TodoServices {

    @GET("/mobile/api/todos/get/{UserId}")
    Call<List<TodoItem>> GetTodos(@Header("Authorization") String authkey, @Path("UserId") String UserId);

    @GET("/mobile/api/todos/get/completed/{UserId}")
    Call<List<TodoItem>> GetCompletedTodods(@Header("Authorization") String authkey, @Path("UserId") String UserId);

    @POST("/mobile/api/todos/add")
    Call<StructuredResponse> AddTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);

    @PUT("/mobile/api/todos/complete")
    Call<StructuredResponse> CompleteTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);

    @PUT("/mobile/api/todos/uncomplete")
    Call<StructuredResponse> UncompleteTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);

    @PUT("/mobile/api/todos/update")
    Call<StructuredResponse> UpdateTodo(@Header("Authorization") String authkey, @Body TodoItem todoItem);

    @DELETE("/mobile/api/todos/delete/{todoId}/{UserId}")
    Call<StructuredResponse> DeleteTodo(@Header("Authorization") String authKey, @Path("todoId") String todoId, @Path("UserId") String UserId);
}

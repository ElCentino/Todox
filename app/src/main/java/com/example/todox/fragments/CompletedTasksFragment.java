package com.example.todox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todox.R;
import com.example.todox.adapters.CompletedListAdapter;
import com.example.todox.adapters.TodoListAdapter;
import com.example.todox.interfaces.RequestAction;
import com.example.todox.interfaces.TodoViewed;
import com.example.todox.models.TodoItem;

import java.util.ArrayList;

public class CompletedTasksFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CompletedListAdapter mCompletedListAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private TodoViewed mTodoViewed;
    private ArrayList<TodoItem> mTodoItems;

    private RequestAction mRequestAction;

    public CompletedTasksFragment(ArrayList<TodoItem> todoItems) {
        mTodoItems = todoItems;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.rv_todos_completed);
        mCompletedListAdapter = new CompletedListAdapter(mTodoItems);

        mCompletedListAdapter.setOnTodoItemViewed(mTodoViewed);
        mCompletedListAdapter.setRequestAction(mRequestAction);

        mLinearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mCompletedListAdapter);
    }

    public void setRequestAction(RequestAction requestAction) {
        mRequestAction = requestAction;
    }

    public void setTodoViewed(TodoViewed todoViewed) {
        mTodoViewed = todoViewed;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mRequestAction = (RequestAction) context;
    }
}

package com.example.todox.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todox.R;
import com.example.todox.interfaces.RequestAction;
import com.example.todox.interfaces.TodoViewed;
import com.example.todox.models.TodoItem;
import com.example.todox.services.ApplicationServices;
import com.example.todox.services.TodoServices;
import com.example.todox.utils.StructuredResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    private ArrayList<TodoItem> mTodoItems = new ArrayList<>();
    private TodoViewed mOnTodoItemViewed;
    private RequestAction mRequestAction;

    public TodoListAdapter(ArrayList<TodoItem> todoItems) { mTodoItems = todoItems; }

    public void setOnTodoItemViewed(TodoViewed onTodoItemViewed) {
        mOnTodoItemViewed = onTodoItemViewed;
    }

    public void setRequestAction(RequestAction requestAction) {
        mRequestAction = requestAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_todolist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TodoItem todoItem = mTodoItems.get(position);

        holder.bind(todoItem);

    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private ImageView mImageView;
        private TodoItem mTodoItem;
        private final TextView mTodo_title;
        private final TextView mTodo_description;
        private final TextView mTv_todo_createdAt;
        private TodoServices mTodoServices;

        public ViewHolder(@NonNull final View itemView) {

            super(itemView);

            mTodoServices = ApplicationServices.WebService.getInstance().create(TodoServices.class);

            mTodo_title = itemView.findViewById(R.id.todo_title);
            mTodo_description = itemView.findViewById(R.id.tv_todo_description);
            mTv_todo_createdAt = itemView.findViewById(R.id.tv_todo_createdAt);


            mCheckBox = itemView.findViewById(R.id.todo_checkbox);
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CheckBox checkBox = (CheckBox) view;

                    if(checkBox.isChecked()) {

                        mRequestAction.onRequestStart(null);
                        mTodo_title.setPaintFlags(mTodo_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mTodo_description.setPaintFlags(mTodo_description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mTv_todo_createdAt.setPaintFlags(mTv_todo_createdAt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        TodoItem todoItem = new TodoItem();
                        todoItem.setId(mTodoItem.getId());
                        todoItem.setUserId(ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(itemView.getContext()));
                        todoItem.setCompleted(true);

                        completeTodo(todoItem);


                    } else {

                        mRequestAction.onRequestStart(null);
                        mTodo_title.setPaintFlags(mTodo_title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        mTodo_description.setPaintFlags(mTodo_description.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        mTv_todo_createdAt.setPaintFlags(mTv_todo_createdAt.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                        TodoItem todoItem = new TodoItem();
                        todoItem.setId(mTodoItem.getId());
                        todoItem.setUserId(ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(itemView.getContext()));
                        todoItem.setCompleted(false);

                        uncompleteTodo(todoItem);
                    }
                }
            });

            mTodo_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnTodoItemViewed.onTodoViewed(mTodoItem);
                }
            });

            mImageView = itemView.findViewById(R.id.iv_trash);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(view.getContext(), mImageView);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.menu_todolist_options, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.menu_view_todo:
                                    mOnTodoItemViewed.onTodoViewed(mTodoItem);
                                    return true;

                                case R.id.menu_delete_todo:
                                    mOnTodoItemViewed.onTodoDeleted(mTodoItem);
                                    return true;

                                default:
                                    return true;
                            }
                        }
                    });

                    popup.show(); //showing popup menu
                }
            });

        }

        private String limitText(String text, int length) {

            if(text.length() >= length)
                return text.substring(0, length) + "....";

            return text;
        }

        private void completeTodo(TodoItem todoItem) {

            mTodoServices.CompleteTodo(ApplicationServices.WebService.getAuthToken(), todoItem).enqueue(new Callback<StructuredResponse>() {
                @Override
                public void onResponse(Call<StructuredResponse> call, Response<StructuredResponse> response) {

                    mRequestAction.onRequestEnd(null);

                    if(response == null) return ;

                    StructuredResponse structuredResponse = null;

                    try {

                        structuredResponse = response.body();

                        if(structuredResponse.status == (int)ApplicationServices.Constants.SUCCESS.getValue()) {

                            mRequestAction.onRequestComplete(ApplicationServices.Constants.REQUESTEND_RELOAD);
                            mRequestAction.onRequestCompleteMessage("Todo has been completed");
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<StructuredResponse> call, Throwable t) {
                    mRequestAction.onRequestCompleteMessage("Action Failed");
                }
            });
        }

        private void uncompleteTodo(TodoItem todoItem) {

            mTodoServices.UncompleteTodo(ApplicationServices.WebService.getAuthToken(), todoItem).enqueue(new Callback<StructuredResponse>() {
                @Override
                public void onResponse(Call<StructuredResponse> call, Response<StructuredResponse> response) {

                    mRequestAction.onRequestEnd(null);

                    if(response == null) return ;

                    StructuredResponse structuredResponse = null;

                    try {

                        structuredResponse = response.body();

                        if(structuredResponse.status == (int)ApplicationServices.Constants.SUCCESS.getValue()) {
                            mRequestAction.onRequestComplete(ApplicationServices.Constants.REQUESTEND_RELOAD);
                            mRequestAction.onRequestCompleteMessage("Todo has been uncompleted");
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<StructuredResponse> call, Throwable t) {
                    mRequestAction.onRequestCompleteMessage("Action Failed");
                }
            });
        }

        public void bind(TodoItem todoItem) {
            mTodoItem = todoItem;

            mTodo_title.setText(limitText(todoItem.getTitle(), 60));
            mTodo_description.setText(limitText(todoItem.getDescription(), 100));
            mCheckBox.setChecked(todoItem.getIsCompleted());

            if(todoItem.getIsCompleted()) {
                mTodo_title.setPaintFlags(mTodo_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mTodo_description.setPaintFlags(mTodo_description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mTv_todo_createdAt.setPaintFlags(mTv_todo_createdAt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            Date date = todoItem.getCreatedAt();

            SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMMMM yyyy hh:mm:ss");
            String strDate = formatter.format(date);

            mTv_todo_createdAt.setText(strDate);
        }
    }
}

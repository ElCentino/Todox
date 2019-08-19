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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CompletedListAdapter extends RecyclerView.Adapter<CompletedListAdapter.ViewHolder> {

    private ArrayList<TodoItem> mTodoItems = new ArrayList<>();
    private TodoViewed mOnTodoItemViewed;
    private RequestAction mRequestAction;

    public CompletedListAdapter(ArrayList<TodoItem> todoItems) {
        mTodoItems = todoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_todolist, parent, false);
        return new ViewHolder(layout);
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


    public void setOnTodoItemViewed(TodoViewed onTodoItemViewed) {
        mOnTodoItemViewed = onTodoItemViewed;
    }

    public void setRequestAction(RequestAction requestAction) {
        mRequestAction = requestAction;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private ImageView mImageView;
        private TodoItem mTodoItem;
        private TextView mTodo_title;
        private TextView mTodo_description;
        private TextView mTv_todo_createdAt;
        private TodoServices mTodoServices;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            mTodoServices = ApplicationServices.WebService.getInstance().create(TodoServices.class);

            mTodo_title = itemView.findViewById(R.id.todo_title);
            mTodo_description = itemView.findViewById(R.id.tv_todo_description);
            mTv_todo_createdAt = itemView.findViewById(R.id.tv_todo_createdAt);
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

            mCheckBox = itemView.findViewById(R.id.todo_checkbox);
            mCheckBox.setClickable(false);
        }

        public void bind(TodoItem todoItem) {

            mTodoItem = todoItem;

            mTodo_title.setText(limitText(todoItem.getTitle(), 60));
            mTodo_description.setText(limitText(todoItem.getDescription(), 100));
            mCheckBox.setChecked(todoItem.getIsCompleted());

            Date date = todoItem.getCompletedAt();

            SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMMMM yyyy hh:mm:ss");
            String strDate = formatter.format(date);

            mTv_todo_createdAt.setText("Completed at: " + strDate);
            mTv_todo_createdAt.setTextColor(itemView.getResources().getColor(R.color.colorAmbient));
        }

        private String limitText(String text, int length) {

            if(text.length() >= length)
                return text.substring(0, length) + "....";

            return text;
        }

    }
}

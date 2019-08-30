package com.example.todox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.todox.fragments.CompletedTasksFragment;
import com.example.todox.fragments.TasksFragment;
import com.example.todox.interfaces.RequestAction;
import com.example.todox.interfaces.TodoViewed;
import com.example.todox.models.TodoItem;
import com.example.todox.services.ApplicationServices;
import com.example.todox.services.TodoServices;
import com.example.todox.utils.StructuredResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, TodoViewed, NavigationView.OnNavigationItemSelectedListener, RequestAction {

    private Toolbar toolbar;
    private Advance3DDrawerLayout mDrawerLayout;
    private BottomNavigationView add_todo_layout_bottom_nav;
    private List<TodoItem> mTodoItems;
    private List<TodoItem> mCompletedTodoItems;
    private RelativeLayout fragment_container_content;

    private FrameLayout add_todo_layout;
    private FrameLayout view_todo_layout;
    private BottomSheetBehavior add_todo_layout_bottom_sheet;
    private BottomSheetBehavior view_todo_layout_bottom_sheet;
    private MaterialButton mClear_add_todo_controls;
    private MaterialProgressBar header_progressbar;
    private TextInputEditText mTv_todo_title;
    private TextInputLayout mTv_todo_title_border;
    private TextInputEditText mTv_todo_description;
    private TextInputLayout mTv_todo_description_border;
    private TextInputEditText mTv_view_title;
    private TextInputLayout mTv_view_title_border;
    private TextInputEditText mTv_view_description;
    private TextInputLayout mTv_view_description_border;
    private LinearLayout view_todo_edit_pane;
    private boolean editMode;
    private NavigationView mNav_view_main;

    private TodoServices mTodoServices;
    private ImageView mView_todo_layout_edit;

    private String tempTitle;
    private String tempDescription;
    private String tempId;
    private TasksFragment mTasksFragment;
    private InputMethodManager mImm;
    private CompletedTasksFragment mCompletedTasksFragment;
    private View mCompleted_pane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initDrawer();
        initAddTodoLayout();
        initViewEditTodoLayout();

        header_progressbar = findViewById(R.id.header_progressbar);
        header_progressbar.getShowProgressBackground();

        add_todo_layout_bottom_nav = findViewById(R.id.bottom_nav);
        add_todo_layout_bottom_nav.setOnNavigationItemSelectedListener(this);

        fragment_container_content = findViewById(R.id.fragment_container_content);

        mNav_view_main = findViewById(R.id.nav_view_main);
        mNav_view_main.setNavigationItemSelectedListener(this);

        mTodoServices = ApplicationServices.WebService.getInstance().create(TodoServices.class);

        if(ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(this) == "") {
            ApplicationServices.SharedPreferenceHelper.getInstance().saveUserId(this);
        }

        getTodos();
        getCompletedTodos();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(view_todo_layout_bottom_sheet.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    view_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

                if(add_todo_layout_bottom_sheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    return;
                }

                add_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });

    }

    private void getTodos() {

        onRequestStart(null);

        mTodoServices.GetTodos(ApplicationServices.WebService.getAuthToken(), ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(this)).enqueue(new Callback<List<TodoItem>>() {

            @Override
            public void onResponse(Call<List<TodoItem>> call, Response<List<TodoItem>> response) {

                onRequestEnd(null);

                if(response == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                    return;
                }

                List<TodoItem> items = response.body();
                mTodoItems = items;

                if(items == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                    items = new ArrayList<>();
                }

                TasksFragment tasksFragment = new TasksFragment((ArrayList<TodoItem>) items);
                tasksFragment.setTodoViewed(MainActivity.this);
//                tasksFragment.setRequestAction(MainActivity.this);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tasksFragment).commit();

                if(items.size() == 0) {
                    fragment_container_content.setVisibility(View.VISIBLE);
                } else {
                    fragment_container_content.setVisibility(View.INVISIBLE);
                }

                header_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<TodoItem>> call, Throwable t) {

            }
        });
    }

    private void getCompletedTodos() {

        onRequestStart(null);

        mTodoServices.GetCompletedTodods(ApplicationServices.WebService.getAuthToken(), ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(this)).enqueue(new Callback<List<TodoItem>>() {

            @Override
            public void onResponse(Call<List<TodoItem>> call, Response<List<TodoItem>> response) {

                onRequestEnd(null);

                if(response == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                    return;
                }

                List<TodoItem> items = response.body();
                mCompletedTodoItems = items;

                if(items == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                    items = new ArrayList<>();
                }

                CompletedTasksFragment completedTasksFragment = new CompletedTasksFragment((ArrayList<TodoItem>) items);
                completedTasksFragment.setTodoViewed(MainActivity.this);
//                tasksFragment.setRequestAction(MainActivity.this);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, completedTasksFragment).commit();

                if(items.size() == 0) {
                    fragment_container_content.setVisibility(View.VISIBLE);
                } else {
                    fragment_container_content.setVisibility(View.INVISIBLE);
                }

                header_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<TodoItem>> call, Throwable t) {

            }
        });
    }

    private void addTodo(TodoItem todoItem) {

        mTodoServices.AddTodo(ApplicationServices.WebService.getAuthToken(), todoItem).enqueue(new Callback<StructuredResponse>() {
            @Override
            public void onResponse(Call<StructuredResponse> call, Response<StructuredResponse> response) {

                if(response == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                    return;
                }

                try {
                    StructuredResponse structuredResponse = response.body();

                    if(structuredResponse.status == (int)ApplicationServices.Constants.SUCCESS.getValue()) {
                        getTodos();
                        header_progressbar.setVisibility(View.INVISIBLE);

                        mImm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        mImm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);

                        add_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                        Snackbar.make(findViewById(android.R.id.content), "Todo added :D", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    }
                }
                catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<StructuredResponse> call, Throwable t) {

            }
        });

    }

    private void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_black_24dp, null);
            if (drawable == null) return;

            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);

            getSupportActionBar().setHomeAsUpIndicator(drawable);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawer() {

        mDrawerLayout = findViewById(R.id.drawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mDrawerLayout.setViewScale(Gravity.START, 0.9f);
        mDrawerLayout.setViewElevation(Gravity.START, 20);
        mDrawerLayout.setRadius(Gravity.START, 25);//set end container's corner radius (dimension)
//        drawer.setViewRotation(Gravity.START, 15); // MAKES IT 3D

        mDrawerLayout.useCustomBehavior(Gravity.START); //assign custom behavior for "Left" drawer

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_clear_todos:
                ApplicationServices.SharedPreferenceHelper.getInstance().clearSharedPreference(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                if(mTodoItems == null) return false;
                mTasksFragment = new TasksFragment((ArrayList<TodoItem>)mTodoItems);
                mTasksFragment.setTodoViewed(this);
                selectedFragment = mTasksFragment;
                getTodos();
                break;

            case R.id.nav_completed:
                if(mCompletedTodoItems == null) return false;
                mCompletedTasksFragment = new CompletedTasksFragment((ArrayList<TodoItem>) mCompletedTodoItems);
                mCompletedTasksFragment.setTodoViewed(this);
                selectedFragment = mCompletedTasksFragment;
                getCompletedTodos();
                break;

        }

        if(selectedFragment == null) return false;

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    }


    private void initViewEditTodoLayout() {

        mTv_view_title = findViewById(R.id.tv_view_title);
        mTv_view_title.setEnabled(false);
        mTv_view_title_border = findViewById(R.id.tv_view_title_border);

        mTv_view_description = findViewById(R.id.tv_view_description);
        mTv_view_description.setEnabled(false);
        mTv_view_description_border = findViewById(R.id.tv_view_description_border);

        mView_todo_layout_edit = findViewById(R.id.view_todo_layout_edit);


        view_todo_layout = findViewById(R.id.view_todo_layout);
        view_todo_layout_bottom_sheet = BottomSheetBehavior.from(view_todo_layout);
        view_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        view_todo_edit_pane = findViewById(R.id.view_todo_edit_pane);

        mCompleted_pane = findViewById(R.id.completed_pane);
        mCompleted_pane.setVisibility(View.INVISIBLE);

        view_todo_layout_bottom_sheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                if(i == BottomSheetBehavior.STATE_HIDDEN) {
                    findViewById(R.id.bottom_sheet_shadow_view).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.bottom_sheet_shadow_view).setVisibility(View.VISIBLE);
                }

                if(i == BottomSheetBehavior.STATE_EXPANDED) {

                    view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);

                } else {
                    view.findViewById(R.id.shadow_view).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        findViewById(R.id.view_todo_layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        findViewById(R.id.edit_view_todo_controls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onRequestStart(null);

                TodoItem todoItem = new TodoItem();
                todoItem.setId(tempId);
                todoItem.setTitle(mTv_view_title.getText().toString());
                todoItem.setUserId(ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(MainActivity.this));
                todoItem.setDescription(mTv_view_description.getText().toString());

                mTodoServices.UpdateTodo(ApplicationServices.WebService.getAuthToken(), todoItem).enqueue(new Callback<StructuredResponse>() {
                    @Override
                    public void onResponse(Call<StructuredResponse> call, Response<StructuredResponse> response) {

                        onRequestEnd(null);
                        onRequestComplete(ApplicationServices.Constants.REQUESTEND_RELOAD);

                        view_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                        onRequestCompleteMessage("Todo Edited");
                    }

                    @Override
                    public void onFailure(Call<StructuredResponse> call, Throwable t) {
                        onRequestCompleteMessage("Action Failed");
                    }
                });
            }
        });

        mView_todo_layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTodoEditMode();
            }
        });
    }

    private void initAddTodoLayout() {

        add_todo_layout = findViewById(R.id.add_todo_layout);
        add_todo_layout_bottom_sheet = BottomSheetBehavior.from(add_todo_layout);
        add_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        mTv_todo_title = findViewById(R.id.tv_todo_title);
        mTv_todo_title_border = findViewById(R.id.tv_todo_title_border);

        mTv_todo_description = findViewById(R.id.tv_todo_description);
        mTv_todo_description_border = findViewById(R.id.tv_todo_description_border);

        mClear_add_todo_controls = findViewById(R.id.clear_add_todo_controls);
        mClear_add_todo_controls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTv_todo_title.setText("");
                mTv_todo_description.setText("");
                mTv_todo_title_border.clearFocus();
                mTv_todo_description_border.clearFocus();
            }
        });

        add_todo_layout_bottom_sheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                if(i == BottomSheetBehavior.STATE_HIDDEN) {
                    findViewById(R.id.bottom_sheet_shadow).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.bottom_sheet_shadow).setVisibility(View.VISIBLE);
                }

                if(i == BottomSheetBehavior.STATE_EXPANDED) {

                    view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);

                } else {
                    view.findViewById(R.id.shadow_view).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        findViewById(R.id.add_todo_layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        findViewById(R.id.btn_add_todo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Do you want to add this to your list of todos ?")
                        .setTitleText("ADD TODO ?")
                        .setCancelText("Cancel")
                        .setConfirmText("Add")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                String title = mTv_todo_title.getText().toString();
                                String description = mTv_todo_description.getText().toString();

                                if(title.isEmpty()) {
                                    mTv_todo_title_border.setError("Title is required");
                                    return;
                                }

                                if(description.isEmpty()) {
                                    mTv_todo_description_border.setError("Description is required");
                                    return;
                                }
                                header_progressbar.setVisibility(View.VISIBLE);

                                TodoItem todoItem = new TodoItem();
                                todoItem.setId(UUID.randomUUID().toString());
                                todoItem.setUserId(ApplicationServices.SharedPreferenceHelper.getInstance().getUserId(MainActivity.this));
                                todoItem.setTitle(title);
                                todoItem.setDescription(description);

                                addTodo(todoItem);
                                sweetAlertDialog.dismiss();

                                mTv_todo_title.setText("");
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });

                dialog.show();
            }
        });
    }

    private void toggleTodoEditMode() {

        editMode = !editMode;

        if(editMode) {

            tempTitle = mTv_view_title.getText().toString();
            tempDescription = mTv_view_description.getText().toString();

            mTv_view_title.setEnabled(true);
            mTv_view_description.setEnabled(true);
            view_todo_edit_pane.setVisibility(View.VISIBLE);
            mView_todo_layout_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_white_24dp));

        } else {
            mTv_view_title.setText(tempTitle);
            mTv_view_description.setText(tempDescription);

            tempTitle = "";
            tempDescription = "";

            mTv_view_title.setEnabled(false);
            mTv_view_description.setEnabled(false);
            view_todo_edit_pane.setVisibility(View.INVISIBLE);
            mView_todo_layout_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        }
    }

    @Override
    public void onTodoViewed(TodoItem todoItem) {

        if(add_todo_layout_bottom_sheet.getState() != BottomSheetBehavior.STATE_HIDDEN) {

            add_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        mTv_view_title.setText(todoItem.getTitle());
        mTv_view_description.setText(todoItem.getDescription());
        tempId = todoItem.getId();

        if(todoItem.getIsCompleted()) {
            mCompleted_pane.setVisibility(View.VISIBLE);
        } else {
            mCompleted_pane.setVisibility(View.INVISIBLE);
        }
        view_todo_layout_bottom_sheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onTodoDeleted(final TodoItem todoItem) {



        SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Do you want to delete todo ?")
                .setTitleText("DELETE TODO ?")
                .setCancelText("Cancel")
                .setConfirmText("Delete")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {

                        onRequestStart(null);

                        mTodoServices.DeleteTodo(ApplicationServices.WebService.getAuthToken(), todoItem.getId(), todoItem.getUserId()).enqueue(new Callback<StructuredResponse>() {
                            @Override
                            public void onResponse(Call<StructuredResponse> call, Response<StructuredResponse> response) {

                                onRequestEnd(null);
                                onRequestComplete(ApplicationServices.Constants.REQUESTEND_RELOAD);
                                onRequestCompleteMessage("Todo Deleted");
                                sweetAlertDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<StructuredResponse> call, Throwable t) {
                                onRequestCompleteMessage("Action Failed");
                                sweetAlertDialog.dismiss();
                            }
                        });
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });

        dialog.show();
    }

    @Override
    public void onRequestStart(Object object) {
        header_progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestEnd(Object object) {
        header_progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestComplete(ApplicationServices.Constants constant) {

        switch (constant) {

            case REQUESTEND_RELOAD:
                getTodos();
                break;
        }
    }

    @Override
    public void onRequestCompleteMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}

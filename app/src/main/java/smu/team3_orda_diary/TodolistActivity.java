package smu.team3_orda_diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class TodolistActivity extends AppCompatActivity {
    
    private RecyclerView mRv_todo;
    private FloatingActionButton mBtn_Write;
    private ArrayList<TodoItem> mTodoItems;
    private DBHelper mDBHelper;
    private TodoCustomAdapter mAdapter;
    String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString(); // 현재 시간 연월일 받아오기



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        mRv_todo= findViewById(R.id.rv_todo);
        mBtn_Write= findViewById(R.id.todo_btn_write);
        mDBHelper = new DBHelper(this);



        // 날짜 기초값 세팅
        final TextView today = findViewById(R.id.todo_today);
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int dayOfMonth = now.getDayOfMonth();
        today.setText(String.format("%d년 %d월 %d일",year,month,dayOfMonth));
        // 달력
        CalendarView calendar = findViewById(R.id.todo_calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                month += 1;
                today.setText(String.format("%d년 %d월 %d일",year,month,dayOfMonth));
                String changeDate = String.format("%d-%d-%d",year,month,dayOfMonth);
                Toast.makeText(TodolistActivity.this, changeDate, Toast.LENGTH_LONG).show();
                currentTime = changeDate;
                loadRecentDB(currentTime);
            }
        });

        setInit();



    }
    private void setInit(){
        mDBHelper = new DBHelper(this);
        mRv_todo = findViewById(R.id.rv_todo);
        mBtn_Write = findViewById(R.id.todo_btn_write);
        mTodoItems = new ArrayList<>();
        loadRecentDB(currentTime);

        mBtn_Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //팝업창 띄우기
                Dialog dialog = new Dialog(TodolistActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.dialog_edit_todo);
                EditText et_title = dialog.findViewById(R.id.et_title);
                EditText et_content = dialog.findViewById(R.id.et_content);
                Button btn_ok = dialog.findViewById(R.id.todo_dialog_btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //update database
                        String title = et_title.getText().toString();
                        String content = et_content.getText().toString();
                        mDBHelper.insertTodo(title, content, currentTime);

                        //Insert UI
                        TodoItem item = new TodoItem();

                        item.setTitle(title);
                        item.setContent(content);
                        item.setWriteDate(currentTime);

                        mAdapter.addItem(item);
                        mRv_todo.smoothScrollToPosition(0);
                        dialog.dismiss();
                        Toast.makeText(TodolistActivity.this, "목록이 추가되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.show();
            }
        });

    }
    private  void loadRecentDB(String writeDate){
        //저장되어있던 DB 를 가져온다.

        mTodoItems = mDBHelper.getTodoList(writeDate);
        if(mAdapter == null){
            mAdapter = new TodoCustomAdapter(mTodoItems, this);
            mRv_todo.setHasFixedSize(true);
            mRv_todo.setAdapter(mAdapter);

        }
    }
}
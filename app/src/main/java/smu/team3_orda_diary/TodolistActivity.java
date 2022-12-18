package smu.team3_orda_diary;

// 필요한 것
import static smu.team3_orda_diary.MainActivity.mDBHelper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// 기본적인 것
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class TodolistActivity extends AppCompatActivity {
    
    private RecyclerView mRv_todo;
    private FloatingActionButton mBtn_Write;
    private ArrayList<TodoItem> mTodoItems;
    private TodoCustomAdapter mAdapter;
    private String currentTime = "";

     TextView today;
    LocalDate now;
    int year, month, dayOfMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        mRv_todo= findViewById(R.id.rv_todo);
        mBtn_Write= findViewById(R.id.todo_btn_write);

        today = findViewById(R.id.todo_today);
        now = LocalDate.now();
        year = now.getYear();
        month = now.getMonthValue();
        dayOfMonth = now.getDayOfMonth();
        today.setText(String.format("%d년 %d월 %d일",year,month,dayOfMonth));
        currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        setInit();
        // 달력
        CalendarView calendar = findViewById(R.id.todo_calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                month += 1;
                today.setText(String.format("%d년 %d월 %d일",year,month,dayOfMonth));
                String changeDate = String.format("%d-%d-%d",year,month,dayOfMonth);
                currentTime = changeDate;
                Toast.makeText(TodolistActivity.this, currentTime, Toast.LENGTH_LONG).show();
                loadRecentDB(currentTime);

            }
        });

    }
    private void setInit(){
        //mDBHelper = new DBHelper(this);
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

                        dialog.dismiss();
                        Toast.makeText(TodolistActivity.this, "목록이 추가되었습니다.", Toast.LENGTH_LONG).show();
                        // 화면을 refresh해줘야 함
                        mAdapter.notifyDataSetChanged();
                        mRv_todo.invalidate();
                        Intent intentRefresh = getIntent();
                        finish();
                        startActivity(intentRefresh);
                    }
                });
                dialog.show();
            }
        });

    }
    private  void loadRecentDB(String writeDate){
        //저장되어있던 DB 를 가져온다.
        Log.d("wirteDate 들어온 것 : " , writeDate);

        //writeDate = "2022-12-16";
        mTodoItems = mDBHelper.getTodoList(writeDate);
        if(mTodoItems!=null){
            mAdapter = new TodoCustomAdapter(mTodoItems, this);
            mRv_todo.setHasFixedSize(true);
            mRv_todo.setAdapter(mAdapter);

        }else if(mTodoItems==null){
            // 아무 일정 없을 때는 아무것도 안 보이게 함
            Log.d("일정 있나> ", "없음--------------");
        }
    }
}
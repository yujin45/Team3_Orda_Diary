package smu.team3_orda_diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

public class TodolistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        final TextView today = findViewById(R.id.todo_today);
        CalendarView calendar = findViewById(R.id.todo_calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                month += 1;
                today.setText(String.format("%d년 %d월 %d일",year,month,dayOfMonth));
            }
        });

    }
}
package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button scheduleBtn, accountBookBtn, diaryBtn, alarmBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleBtn = findViewById(R.id.button1);
        accountBookBtn = findViewById(R.id.button2);
        diaryBtn = findViewById(R.id.button3);
        alarmBtn = findViewById(R.id.button4);

        // 버튼 클릭시
        // 스케줄 관리 버튼
        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                startActivity(intent);

            }
        });
        // 가계부 버튼
        accountBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // 일기장 버튼
        diaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryListActivity.class);
                startActivity(intent);
            }
        });
        // 알람 버튼
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
            }
        });
    }
}
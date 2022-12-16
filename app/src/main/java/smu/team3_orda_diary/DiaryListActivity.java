package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DiaryListActivity extends AppCompatActivity {
    Button writeDiaryButton;
    RecyclerView recyclerView;
    DiaryRecyclerViewAdapter adapter;
    public static ArrayList<OnePageDiary> diaryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        recyclerView = findViewById(R.id.recyclerView);
        writeDiaryButton = findViewById(R.id.writeDiaryButton);

        // 일기 쓰기 버튼 눌렀을 때
        writeDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryWritingPageActivity.class);
                startActivity(intent);
            }
        });
        /*

        diaryList.add(null);
        adapter = new DiaryRecyclerViewAdapter(this, diaryList);
        recyclerView.setAdapter(adapter);
        //  리사이클러 뷰 불규칙 레이아웃인데 2개씩 넣어줌
        StaggeredGridLayoutManager staggeredGridLayoutManager
                = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //레이아웃 매니저 연결
        recyclerView.setLayoutManager(staggeredGridLayoutManager);*/
    }
}
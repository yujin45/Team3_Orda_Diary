package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;
// DB관리를 위해 Main에서 생성한 mDBHelper 가져옴
import static smu.team3_orda_diary.MainActivity.mDBHelper;
// 목록 보여주기 위해 리사이클러 뷰와 그리드로 보여주기 위한 매니저
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
// 그 외 버튼, 로그 등에 필요한 것들
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
// 일기장들을 넣어둘 리스트를 만들기 위한 것
import java.util.ArrayList;

public class DiaryListActivity extends AppCompatActivity {
    Button writeDiaryButton;
    RecyclerView recyclerView;
    public static DiaryRecyclerViewAdapter adapter;
    public static ArrayList<OnePageDiary> diaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        
        recyclerView = findViewById(R.id.recyclerView);
        writeDiaryButton = findViewById(R.id.writeDiaryButton);
        
        // 현재 데이터베이스의 DIARY_TB 내의 일기들을 가져옴
        diaryList = mDBHelper.getResult();
        
        // 일기 쓰기 버튼 눌렀을 때
        writeDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryWritingPageActivity.class);
                startActivity(intent);
            }
        });

        if (diaryList!=null){
            // 일기가 있을 때
            adapter = new DiaryRecyclerViewAdapter(this, diaryList);
            recyclerView.setAdapter(adapter);
            //  리사이클러 뷰 불규칙 그리드 레이아웃인데 2개씩 넣어줌
            StaggeredGridLayoutManager staggeredGridLayoutManager
                    = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            //레이아웃 매니저 연결
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
        }else if(diaryList==null){
            // 아무 일기도 없을 때는 아무것도 안 보이게 함
            Log.d("일기 있나> ", "없음--------------");
        }

    }
    // 일기 계속 쓰다가 완료 후 뒤로 가면 리스트에서 바로 다시 메인으로 갈 수 있게 처리리
   @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;
// 일기들이 저장되어 있는 diaryList에 접근하는 용도
import static smu.team3_orda_diary.DiaryListActivity.diaryList;
// 텍스트뷰의 스크롤을 위한 것
import android.text.method.ScrollingMovementMethod;
// 그 외 기본적인 필요한 것들
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DiarySelectedActivity extends AppCompatActivity {
    int clickPosition;
    OnePageDiary clickDiary;
    ImageView photoImageView;
    TextView titleTextView, dateTextView, feelTextView, textTextView;
    
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_selected);

        Intent intent = getIntent();
        clickPosition = intent.getIntExtra("clickPosition", 0);
        clickDiary =  diaryList.get(clickPosition);
        
        photoImageView = findViewById(R.id.imageView2);
        titleTextView = findViewById(R.id.textViewTitle);
        dateTextView = findViewById(R.id.textViewDate);
        feelTextView = findViewById(R.id.textViewFeel);
        textTextView = findViewById(R.id.textViewText);
        // 텍스트뷰에 스크롤 설정
        textTextView.setMovementMethod(new ScrollingMovementMethod());
        // 일기 정보 가져와서 보여주기
        photoImageView.setImageURI(Uri.parse(clickDiary.getPicture_uri()));
        titleTextView.setText(clickDiary.getTitle());
        dateTextView.setText(clickDiary.getDate());
        feelTextView.setText(clickDiary.getFeel());
        textTextView.setText(clickDiary.getText());

    }
}
package smu.team3_orda_diary;

import static smu.team3_orda_diary.DiaryListActivity.diaryList;
import static smu.team3_orda_diary.MainActivity.mDBHelper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
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
        //
        textTextView.setMovementMethod(new ScrollingMovementMethod());

        photoImageView.setImageURI(Uri.parse(clickDiary.getPicture_uri()));
        titleTextView.setText(clickDiary.getTitle());
        dateTextView.setText(clickDiary.getDate());
        feelTextView.setText(clickDiary.getFeel());
        textTextView.setText(clickDiary.getText());

    }
}
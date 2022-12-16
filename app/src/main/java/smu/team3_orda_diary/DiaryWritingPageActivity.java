package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class DiaryWritingPageActivity extends AppCompatActivity {
    Button insertPictureButton, selectBackColorButton, saveButton, feelButton, changeDateButton;
    EditText titleEditText, dateEditText, edittText;
    ImageView diaryImageView;
    DiaryDBHelper diaryDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_writing_page);

        insertPictureButton = findViewById(R.id.insertPictureButton);
        selectBackColorButton = findViewById(R.id.selectBackColorButton);
        saveButton = findViewById(R.id.saveButton);
        feelButton = findViewById(R.id.feelButton);
        changeDateButton = findViewById(R.id.changeDateButton);

        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        edittText = findViewById(R.id.editText);

        diaryImageView = findViewById(R.id.diaryImageView);

        diaryDBHelper = new DiaryDBHelper(this, 1);

        //https://crazykim2.tistory.com/648
        // https://m.blog.naver.com/yoon141122/220832906240  이거봐라!!!!!!!!!
        // https://cool-developer.tistory.com/11?category=943315
        // https://aristatait.tistory.com/14
        // https://aries574.tistory.com/265
        // https://velog.io/@jinny_0422/%EA%B0%84%EB%8B%A8%ED%95%9C-ToDo-List%EC%95%B1-%EB%A7%8C%EB%93%A4%EA%B8%B0
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내일 일어나서 diary_id 이거 primary key인데 추가할 때마다 바꿔주기 귀찮으니까 없애버릴거임
                diaryDBHelper.insert(5,titleEditText.getText().toString(), dateEditText.getText().toString(),
                        "기분", null, edittText.getText().toString());
                ArrayList<OnePageDiary> onePageDiaries = diaryDBHelper.getResult();

                for(int i =0; i<onePageDiaries.size(); i++){
                    Log.d("-------\n제목 :" , onePageDiaries.get(i).getTitle());
                    Log.d("날짜 :" , onePageDiaries.get(i).getDate());
                    Log.d("내용 :" , onePageDiaries.get(i).getText());
                }

            }
        });


    }
}
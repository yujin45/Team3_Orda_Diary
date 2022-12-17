package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class DiaryWritingPageActivity extends AppCompatActivity {
    Button insertPictureButton, selectBackColorButton, saveButton, feelButton, changeDateButton;
    EditText titleEditText, dateEditText, edittText;
    ImageView diaryImageView;
    DiaryDBHelper diaryDBHelper;

    // 날짜 관련
    DatePickerDialog datePickerDialog;
    Calendar dirayCalendar;
    int diaryYear, diaryMonth, diaryDay, diaryWeek;
    String weekList[] = {"", "일", "월", "화", "수", "목", "금", "토"};
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



        // 현재 날짜로 자동 설정 (버튼으로 날짜 수정은 아래에)
        dirayCalendar = Calendar.getInstance();
        diaryYear = dirayCalendar.get(Calendar.YEAR);
        diaryMonth = dirayCalendar.get(Calendar.MONTH)+1;
        diaryDay = dirayCalendar.get(Calendar.DAY_OF_MONTH);
        diaryWeek = dirayCalendar.get(Calendar.DAY_OF_WEEK);
        dateEditText.setText(diaryYear + "년" +  diaryMonth + "월"  + diaryDay +"일");

        datePickerDialog = new DatePickerDialog(this, datePickerListener, diaryYear, diaryMonth-1, diaryDay);
        // 날짜 변경
        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        //https://crazykim2.tistory.com/648
        // https://m.blog.naver.com/yoon141122/220832906240  이거봐라!!!!!!!!!
        // https://cool-developer.tistory.com/11?category=943315
        // https://aristatait.tistory.com/14
        // https://aries574.tistory.com/265
        // https://velog.io/@jinny_0422/%EA%B0%84%EB%8B%A8%ED%95%9C-ToDo-List%EC%95%B1-%EB%A7%8C%EB%93%A4%EA%B8%B0


        // DB관련 부분
        diaryDBHelper = new DiaryDBHelper(this, 1);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    /////////ONCREATE

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(getApplicationContext(), year + "년" + (monthOfYear+1) + "월"
                    + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
            dateEditText.setText(year + "년" +  (monthOfYear+1) + "월"  + dayOfMonth +"일");
        }
    };

}
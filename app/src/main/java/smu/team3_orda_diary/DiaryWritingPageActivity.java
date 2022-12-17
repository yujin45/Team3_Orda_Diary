package smu.team3_orda_diary;

import static smu.team3_orda_diary.DiaryListActivity.adapter;
import static smu.team3_orda_diary.DiaryListActivity.diaryDBHelper;
import static smu.team3_orda_diary.DiaryListActivity.diaryList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryWritingPageActivity extends AppCompatActivity {
    Button insertPictureButton, insertCameraButton, selectBackColorButton, saveButton, feelButton, changeDateButton;
    EditText titleEditText, dateEditText, edittText;
    ImageView diaryImageView;
    //DiaryDBHelper diaryDBHelper;
    int count=0;
    // 날짜 관련
    DatePickerDialog datePickerDialog;
    Calendar dirayCalendar;
    int diaryYear, diaryMonth, diaryDay, diaryWeek;
    String weekList[] = {"", "일", "월", "화", "수", "목", "금", "토"};

    // 이미지 관련
    Uri imageUri;

    // 카메라 관련
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    //private Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_writing_page);

        insertPictureButton = findViewById(R.id.insertPictureButton);
        insertCameraButton = findViewById(R.id.insertCameraButton);
        selectBackColorButton = findViewById(R.id.selectBackColorButton);
        saveButton = findViewById(R.id.saveButton);
        feelButton = findViewById(R.id.feelButton);
        changeDateButton = findViewById(R.id.changeDateButton);

        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        edittText = findViewById(R.id.editText);

        diaryImageView = findViewById(R.id.diaryImageView);


        /* 날짜 */
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

        /* 갤러리 이미지 넣기 */
        insertPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트를 사용하여 갤러리에서 뭘 가져올건지 수행함
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });


        /* 카메라 이미지 넣기 */
        insertCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    if (photoFile != null) {
                        //photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        imageUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });


        /* 저장 */
        //diaryDBHelper = new DiaryDBHelper(this);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //count+=1;
                if (imageUri==null){
                    Toast.makeText(getApplicationContext(), "이미지를 삽입해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    diaryDBHelper.insert( titleEditText.getText().toString(), dateEditText.getText().toString(),
                            "기분", imageUri.toString(), edittText.getText().toString());
                    //ArrayList<OnePageDiary> onePageDiaries = diaryDBHelper.getResult();
                    diaryList = diaryDBHelper.getResult();
                    for(int i =0; i<diaryList.size(); i++){
                        Log.d("-------\n제목 :" ,diaryList.get(i).getTitle());
                        Log.d("날짜 :" , diaryList.get(i).getDate());
                        Log.d("내용 :" , diaryList.get(i).getText());
                    }

                    adapter.notifyDataSetChanged();
                    //recyclerview.invalidate();
                    Intent intent = new Intent(getApplicationContext(), DiaryListActivity.class);
                    startActivity(intent);
                }
                
                // 안녕하세요

            }
        });



    }
    /////////ONCREATE

    // 날짜 선택했을 때
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(getApplicationContext(), year + "년" + (monthOfYear+1) + "월"
                    + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
            dateEditText.setText(year + "년" +  (monthOfYear+1) + "월"  + dayOfMonth +"일");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 위에서 버튼을 눌렀을 때 이쪽으로 와서 case 1일 때로 들어가게 됨.
        // 이때 선택한 이미지 uri를 넣어주기
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    diaryImageView.setImageURI(imageUri);
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    ExifInterface exif = null;

                    try {
                        exif = new ExifInterface(imageFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int exifOrientation;
                    int exifDegree;

                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegrees(exifOrientation);
                    } else {
                        exifDegree = 0;
                    }

                    ((ImageView)findViewById(R.id.diaryImageView)).setImageBitmap(rotate(bitmap, exifDegree));
                }
        }
    }

    // 카메라 관련
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

}
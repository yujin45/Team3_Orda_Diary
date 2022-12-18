package smu.team3_orda_diary;

import androidx.appcompat.app.AppCompatActivity;
// 각종 필요한 것들 불러오기
import static smu.team3_orda_diary.DiaryListActivity.adapter;
import static smu.team3_orda_diary.DiaryListActivity.diaryList;
import static smu.team3_orda_diary.MainActivity.mDBHelper;
/* 카메라 촬영 관련 */
// 카메라 촬영하여 uri 얻고 제대로 화면에 표시하는 등에 필요한 것들
import androidx.core.content.FileProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
/* 기분 선택 관련 다이얼로그 */
import android.app.AlertDialog;
/* 날짜 선택 관련 */
// 다이얼로그, Date, Calendar 사용해 선택 날짜 가져옴
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/* 음성인식 STT 관련 */
// 안드로이드에서 제공하는 STT 방법 사용
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
// 기본적인 것들
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;


public class DiaryWritingPageActivity extends AppCompatActivity {
    Button insertPictureButton, insertCameraButton, recordButton, saveButton, feelButton, changeDateButton;
    EditText titleEditText, dateEditText, editText;
    ImageView diaryImageView;
    // 날짜 관련
    DatePickerDialog datePickerDialog;
    Calendar dirayCalendar;
    int diaryYear, diaryMonth, diaryDay, diaryWeek;
    // 기분 관련
    String []items= {"기쁨", "슬픔", "화남", "우울", "복잡미묘", "모르겠음", "최고로 행복"};
    // 이미지 관련
    Uri imageUri;
    // 카메라 관련
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    // 녹음 관련
    SpeechRecognizer speechRecognizer;
    Intent recordIntent;

    boolean recording = false;  //현재 녹음중인지 여부
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_writing_page);

        insertPictureButton = findViewById(R.id.insertPictureButton);
        insertCameraButton = findViewById(R.id.insertCameraButton);
        recordButton = findViewById(R.id.recordButton);
        saveButton = findViewById(R.id.saveButton);
        feelButton = findViewById(R.id.feelButton);
        changeDateButton = findViewById(R.id.changeDateButton);
        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        editText = findViewById(R.id.editText);
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

        /* 기분 */
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("기분 선택")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
                        feelButton.setText(items[which]);
                    }
                })
                .create();
        feelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.show();
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
                        imageUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        /* STT  */
        recordIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recordIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        recordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {   //녹음 시작
                    startRecord();
                    Toast.makeText(getApplicationContext(), "지금부터 음성으로 기록합니다.", Toast.LENGTH_SHORT).show();
                }
                else {  //이미 녹음 중이면 녹음 중지
                    stopRecord();
                }
            }
        });

        /* 저장 */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //count+=1;
                if (imageUri==null){
                    Toast.makeText(getApplicationContext(), "이미지를 삽입해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    mDBHelper.insert( titleEditText.getText().toString(), dateEditText.getText().toString(),
                            feelButton.getText().toString(), imageUri.toString(), editText.getText().toString());
                    diaryList = mDBHelper.getResult();
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent(getApplicationContext(), DiaryListActivity.class);
                    startActivity(intent);
                }
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

    // 녹음 관련
    // 녹음 시작
    void startRecord() {
        recording = true;

        recordButton.setText("음성 녹음 변환 중지");

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(recordIntent);
    }

    //녹음 중지
    void stopRecord() {
        recording = false;

        recordButton.setText("음성 녹음 변환 시작");

        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
            //사용자가 말을 멈추면 호출
            //인식 결과에 따라 onError나 onResults가 호출됨
        }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        startRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);	//인식 결과를 담은 ArrayList
            String originText = editText.getText().toString();  //기존 text

            //인식 결과
            String newText="";
            for (int i = 0; i < matches.size() ; i++) {
                newText += matches.get(i);
            }

            editText.setText(originText + newText + " ");	//기존의 text에 인식 결과를 이어붙임
            speechRecognizer.startListening(recordIntent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

}
package smu.team3_orda_diary;

// PendingIntent 사용을 위한 flag
import static android.app.PendingIntent.FLAG_IMMUTABLE;
// DB 관리를 위해 가져옴
import static smu.team3_orda_diary.MainActivity.mDBHelper;
import androidx.appcompat.app.AppCompatActivity;
/* 알람 관련 */
import androidx.fragment.app.DialogFragment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
/* DB에서는 time을 string으로 저장하여 Date, Calendar 등 변환 필요 */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/* 플래시 관련 */
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
// 기본적인 것
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class AlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private AlarmManager alarmManager;
    ImageButton makeButton, deleteButton;
    TextView mTextView;
    private static CameraManager mCameraManager;
    private static boolean mFlashOn = false;
    public static String mCameraId;
    Calendar alarmC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        // 객체 생성
        mTextView =  findViewById(R.id.textView);
        makeButton = findViewById(R.id.makeButton);
        deleteButton = findViewById(R.id.deleteButton);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        
        // 알람 이미 있는 경우
        String alarmTime= mDBHelper.getAlarmTime();
        Log.d("alarmTime", alarmTime);
        if (alarmTime!= null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(alarmTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                updateTimeText(cal);
                startAlarm(cal);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            // 처음 맞추는거면 아래에서 진행될 것임
        }


        // 알람 만드는 부분
        makeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "설정 누름", Toast.LENGTH_SHORT);
                toast.show();
                // DialogFragment로 타임피커 보여줌
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        // 알람 취소하는 부분
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        // 리시버에서 바로 플래시 킬 수 있게 여기에서 플래시 관련 설정
        // 플래시
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            // .. 플래시 켜기
            mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            if (mCameraId == null) {
                try {
                    for (String id : mCameraManager.getCameraIdList()) {
                        CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                        Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                        Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                        if (flashAvailable != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                            mCameraId = id;
                            break;
                        }
                    }
                } catch (CameraAccessException e) {
                    mCameraId = null;
                    e.printStackTrace();
                }
            }
        }
        else {
            // 플래쉬 지원하지 않음.
        }
    }

    // 타임피커로 시간 설정하면 아래가 불러져서 시간이 설정됨
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        updateTimeText(c);
        startAlarm(c);
        Log.d("onTimeSet 맞춰짐", "타임피커 설정시");

    }
    // 알람을 몇시에 맞춰뒀는지 보여줌
    private void updateTimeText(Calendar c){
        Log.d("updateTimeText", "알람 업데이트");
        String timeText = "알람 맞춰짐 \n";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        mTextView.setText(timeText);
    }
    // 알람 진행하기
    private void startAlarm(Calendar c){
        Log.d("startAlarm", "알람 서비스에 연결 실행 부분");


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent;
        // 오레오 버전 등등 flag처리를 해줘야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if(c.before((Calendar.getInstance()))){
            c.add(Calendar.DATE, 1);
        }
        // db접근
        Date date = new Date(c.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        String alarmTime= mDBHelper.getAlarmTime();
        if (alarmTime!= null){
            mDBHelper.updateAlarm(dateStr);
            Log.d("업데이트", "이전 설정 있어서 업데이트");
        }else{
            mDBHelper.insertAlarm(dateStr);
            Log.d("삽입", "이전 설정 없어서 삽입");
        }

        // 아까 타임피커로 설정한 시간으로 알람을 설정
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    // 알람 설정해뒀던 것 취소하는 부분
    private void cancelAlarm(){
        String alarmTime= mDBHelper.getAlarmTime();
        mDBHelper.deleteAlarm(alarmTime);
        Log.d("cancelAlarm", "알람 취소하는 부분");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        mTextView.setText("알람 취소됨");
    }
    // 플래시 관련
    public static void flashLightOn() {
        mFlashOn = true;
        try {
            mCameraManager.setTorchMode(mCameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static void flashLightOff() {
        mFlashOn = false;
        try {
            mCameraManager.setTorchMode(mCameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    // 이후 깜빡임을 원할 때 사용 가능한 것
    public void sleep(int time){
        try{
            Thread.sleep(time);
        } catch (InterruptedException e){
        }
    }
}
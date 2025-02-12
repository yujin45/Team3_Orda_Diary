package smu.team3_orda_diary;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static smu.team3_orda_diary.MainActivity.mDBHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private AlarmManager alarmManager;
    ImageButton makeButton, deleteButton;
    TextView mTextView;
    private static CameraManager mCameraManager;
    private static boolean mFlashOn = false;
    public static String mCameraId;
    Calendar alarmC;
    String alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // UI 요소 초기화
        mTextView = findViewById(R.id.textView);
        makeButton = findViewById(R.id.makeButton);
        deleteButton = findViewById(R.id.deleteButton);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 이전 알람 시간 불러오기 (예외 방지)
        alarmTime = mDBHelper.getAlarmTime();
        if (alarmTime != null && !alarmTime.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(alarmTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                updateTimeText(cal);
                startAlarm(cal);
            } catch (Exception e) {
                Log.e("AlarmError", "Error parsing alarm time: " + e.toString());
            }
        }

        // 알람 설정 버튼
        makeButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "설정 누름", Toast.LENGTH_SHORT).show();
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        });

        // 알람 취소 버튼
        deleteButton.setOnClickListener(v -> cancelAlarm());

        // 플래시 기능 초기화
        initializeFlashlight();
    }

    // 타임피커 설정 후 알람 시간 업데이트
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        updateTimeText(c);
        startAlarm(c);
    }

    // 알람 시간 UI 업데이트
    private void updateTimeText(Calendar c) {
        String timeText = "알람 맞춰짐 \n" + DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        mTextView.setText(timeText);
    }

    // 알람 설정
    private void startAlarm(Calendar c) {
        if (alarmManager == null) return;

        Intent intent = new Intent(this, AlarmReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) flags |= FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, flags);

        if (c.before(Calendar.getInstance())) c.add(Calendar.DATE, 1);

        // 데이터베이스 업데이트
        Date date = new Date(c.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        String alarmTime = mDBHelper.getAlarmTime();
        if (alarmTime != null) {
            mDBHelper.updateAlarm(dateStr);
        } else {
            mDBHelper.insertAlarm(dateStr);
        }

        // 알람 설정
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    // 알람 취소
    private void cancelAlarm() {
        String alarmTime = mDBHelper.getAlarmTime();
        if (alarmTime != null) {
            mDBHelper.deleteAlarm(alarmTime);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        mTextView.setText("알람 취소됨");
    }

    // 플래시 기능 초기화
    private void initializeFlashlight() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
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
                    Log.e("FlashError", "Error accessing camera for flash: " + e.getMessage());
                    mCameraId = null;
                }
            }
        }
    }

    // 플래시 켜기
    public static void flashLightOn() {
        if (mCameraManager != null && mCameraId != null) {
            try {
                mCameraManager.setTorchMode(mCameraId, true);
                mFlashOn = true;
            } catch (CameraAccessException e) {
                Log.e("FlashError", "Error turning on flashlight: " + e.getMessage());
            }
        } else {
            Log.e("FlashError", "CameraManager or CameraId is null");
        }
    }

    // 플래시 끄기
    public static void flashLightOff() {
        if (mCameraManager != null && mCameraId != null) {
            try {
                mCameraManager.setTorchMode(mCameraId, false);
                mFlashOn = false;
            } catch (CameraAccessException e) {
                Log.e("FlashError", "Error turning off flashlight: " + e.getMessage());
            }
        } else {
            Log.e("FlashError", "CameraManager or CameraId is null");
        }
    }
}

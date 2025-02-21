package smu.team3_orda_diary.ui.alarm;

import static smu.team3_orda_diary.ui.alarm.AlarmFragment.flashLightOff;
import static smu.team3_orda_diary.ui.alarm.AlarmReceiver.mediaPlayer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.databinding.ActivityRingingAlarmBinding;

public class RingingAlarmActivity extends AppCompatActivity implements SensorEventListener {
    private ActivityRingingAlarmBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private PowerManager.WakeLock wakeLock;

    private long lastTime;
    private float lastX, lastY, lastZ, speed;
    private static final int SHAKE_THRESHOLD = 2000;

    private boolean flag = true;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRingingAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "OrdaDiary:ActivityWakeLock"
        );
        wakeLock.acquire();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        calendar = Calendar.getInstance();
        startClockThread();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getApplicationContext(), getString(R.string.alarm_dismiss_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startClockThread() {
        new Thread(() -> {
            while (flag) {
                try {
                    calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    String timeText;
                    if (hour == 0) {
                        timeText = getString(R.string.alarm_time_midnight, minute, second);
                    } else if (hour == 12) {
                        timeText = getString(R.string.alarm_time_pm, hour, minute, second);
                    } else if (hour > 12) {
                        timeText = getString(R.string.alarm_time_pm, hour - 12, minute, second);
                    } else {
                        timeText = getString(R.string.alarm_time_am, hour, minute, second);
                    }

                    runOnUiThread(() -> binding.timeTextView.setText(timeText));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gapOfTime = (currentTime - lastTime);

            if (gapOfTime > 100) {
                lastTime = currentTime;
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gapOfTime * 10000;

                runOnUiThread(() -> binding.textViewNowSpeed.setText("▶ " + speed));

                if (speed > SHAKE_THRESHOLD) {
                    Toast.makeText(getApplicationContext(), getString(R.string.shake_complete_message), Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();
                    flashLightOff();
                    flag = false;
                    finish();
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        flashLightOff();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}

package smu.team3_orda_diary;

/* 필요한 것 */
import static smu.team3_orda_diary.AlarmActivity.flashLightOff;
import static smu.team3_orda_diary.AlarmReceiver.mediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
/* 가속도 센서 위한 것들 */
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
/* 잠금화면 위에 뜨게 하는 것 */
import android.view.WindowManager;
/* 시간 지나는 것 표시용 */
import java.util.Calendar;
// 그외 필요한 것
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


// 알림 울리는 화면 - 가속도 센서를 이용하여 흔들림을 감지해 일정 수치 이상이면 노래도 끄고 화면도 종료
public class RingingAlarmActivity extends AppCompatActivity implements SensorEventListener {
    // 가속도 센서 관련
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 20000;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    //알람 관련
    Calendar calendar;
    TextView timeText, textViewNowSpeed;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing_alarm);
        // 꺼진 화면에서도 킬 수 있게
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //센서 관련
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //알람 관련
        calendar = Calendar.getInstance();
        timeText = (TextView) findViewById(R.id.timeTextView);
        textViewNowSpeed = findViewById(R.id.textViewNowSpeed);

        // 잠금 화면 위로 activity 띄워줌
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        // 앱 화면을 안 보고 있더라도 음악이 나올 수 있게 알람 리시버에서 인식하면 노래 나오도록 함

        // 알람이 울리는 동안 몇초가 지났는지 확인하기 위해 실시간으로 시계 출력
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag == true) {
                    try {
                        calendar = Calendar.getInstance();
                        if (calendar.get(Calendar.HOUR_OF_DAY) > 0 && calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                            timeText.setText("AM " + calendar.get(Calendar.HOUR_OF_DAY) + "시 " + calendar.get(Calendar.MINUTE) + "분 " + calendar.get(Calendar.SECOND) + "초");
                        } else if (calendar.get(Calendar.HOUR_OF_DAY) == 12) {
                            timeText.setText("PM " + calendar.get(Calendar.HOUR_OF_DAY) + "시 " + calendar.get(Calendar.MINUTE) + "분 " + calendar.get(Calendar.SECOND) + "초");
                        } else if (calendar.get(Calendar.HOUR_OF_DAY) > 12 && calendar.get(Calendar.HOUR_OF_DAY) < 24) {
                            timeText.setText("PM " + (calendar.get(Calendar.HOUR_OF_DAY) - 12) + "시 " + calendar.get(Calendar.MINUTE) + "분 " + calendar.get(Calendar.SECOND) + "초");
                        } else if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
                            timeText.setText("AM 0시 " + calendar.get(Calendar.MINUTE) + "분 " + calendar.get(Calendar.SECOND) + "초");
                        }

                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                }
            }
        }).start(); 

    }
    //////////////OnCreate

    // 가속도 센서 관련 필요 기본 함수들 ▼
    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // 여기에서 가속도 센서의 값이 바뀌는 것을 측정함
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
                Log.d("speed 측정중 ", String.valueOf(speed));
                textViewNowSpeed.setText("▶ "+ speed);
                if (speed > SHAKE_THRESHOLD) {

                    // 임계값 넘으면 이벤트 발생 부분!
                    Toast toast = Toast.makeText(getApplicationContext(), "흔들기 완료!", Toast.LENGTH_SHORT);
                    mediaPlayer.stop();
                    flashLightOff();
                    flag=false;
                    finish();
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

    // 알람 안 껐는데 뒤로가기 하면 안되니까 뒤로가기 처리
    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(getApplicationContext(), "알람을 꺼주세요!!", Toast.LENGTH_SHORT);
        toast.show();
    }

}
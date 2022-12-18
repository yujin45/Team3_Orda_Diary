package smu.team3_orda_diary;


import static smu.team3_orda_diary.AlarmActivity.flashLightOn;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.media.MediaPlayer;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
public class AlarmReceiver extends BroadcastReceiver {
    public static MediaPlayer mediaPlayer;
    private static PowerManager.WakeLock sCpuWakeLock;

    public AlarmReceiver(){}
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "알람~!!", Toast.LENGTH_SHORT).show();
        // 리시버 받으면 바로 음악 재생
        mediaPlayer = MediaPlayer.create(context,R.raw.younha);   // 소리를 재생할 MediaPlayer
        mediaPlayer.setLooping(true);   // 무한반복
        mediaPlayer.start();
        flashLightOn();
        // 알림창도 만들기
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        // ▼ 화면 꺼진 상태에서도 확인할 수 있게 하는 부분
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK|
                         PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "화면 꺼져도 작동되게");
        sCpuWakeLock.acquire();
        // 알림창
        notificationHelper.getManager().notify(1,nb.build());
    }



}
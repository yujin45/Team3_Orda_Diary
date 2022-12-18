package smu.team3_orda_diary;

// 리시버에서 인식하면 알람 울릴 때 flash도 켜지게 하기 위함
import static smu.team3_orda_diary.AlarmActivity.flashLightOn;
/* 알림을 주고 알람도 울리게 하기 위한 것들 */
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import androidx.core.app.NotificationCompat;
/* 음악 나오게 하는 것 관련 */
import android.media.MediaPlayer;
import android.os.PowerManager;
/* 기본적인 것들 */
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;

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
        //NotificationCompat.Builder nb = notificationHelper.getChannelNotification(); // 기본
        NotificationCompat.Builder nb = notificationHelper.getChannel2Notification("알람", "일어나세요!!!");

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
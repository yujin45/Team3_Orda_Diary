package smu.team3_orda_diary;

/* 알림 위에 보여주기 위한 것들*/
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import androidx.core.app.NotificationCompat;
/* 기본적인 것들 */
import android.content.Context;
import android.content.Intent;
import android.os.Build;

// 알림창 보여주기 위한 클래스
public class NotificationHelper extends ContextWrapper {
    public static final String channel1ID = "channel1ID";
    public static final String channel1Name = "channel 1 ";
    public static final String channel2ID = "channel2ID";
    public static final String channel2Name = "channel 2 ";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    public void createChannels(){
        NotificationChannel channel1 = new NotificationChannel(channel1ID,channel1Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.purple_500);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2 = new NotificationChannel(channel2ID,channel2Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.purple_700);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel2);

    }

    public NotificationManager getManager() {
        if (mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }
    // 매개변수 뭘 주느냐 따라 화면 보이는게 다름 일단은 기본 사용함
    public NotificationCompat.Builder getChannel1Notification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(), channel1ID)
                .setContentTitle(title)
                .setContentText(message)
               .setSmallIcon(R.drawable.ic_launcher_background);
    }

    public NotificationCompat.Builder getChannel2Notification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(), channel2ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background);
    }

    public NotificationCompat.Builder getChannelNotification(){
        // 소리 나게 함
        //MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.younha);   // 소리를 재생할 MediaPlayer
        //mediaPlayer.setLooping(true);   // 무한반복
        //mediaPlayer.start();
        // 알람 울리는 화면을 보여줌.
        Intent intent = new Intent(getApplicationContext(), RingingAlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        return new NotificationCompat.Builder(getApplicationContext(), channel1ID)
                .setSmallIcon(R.drawable.ic_launcher_background);

    }
}
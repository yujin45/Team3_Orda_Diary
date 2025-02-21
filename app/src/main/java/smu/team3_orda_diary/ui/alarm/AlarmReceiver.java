package smu.team3_orda_diary.ui.alarm;

import static smu.team3_orda_diary.ui.alarm.AlarmFragment.flashLightOn;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import smu.team3_orda_diary.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static MediaPlayer mediaPlayer;
    private static PowerManager.WakeLock sCpuWakeLock;

    private static final String WAKELOCK_TAG = "OrdaDiary:WakeLock";
    private static final int NOTIFICATION_ID = 1;
    private static final int WAKELOCK_TIMEOUT = 10 * 1000;

    public AlarmReceiver() {}

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, context.getString(R.string.alarm_toast_message), Toast.LENGTH_SHORT).show();

        mediaPlayer = MediaPlayer.create(context, R.raw.younha);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        flashLightOn();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(
                context.getString(R.string.alarm_notification_title),
                context.getString(R.string.alarm_notification_message)
        );

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, WAKELOCK_TAG
        );

        sCpuWakeLock.acquire(WAKELOCK_TIMEOUT);

        notificationHelper.getManager().notify(NOTIFICATION_ID, nb.build());
    }
}

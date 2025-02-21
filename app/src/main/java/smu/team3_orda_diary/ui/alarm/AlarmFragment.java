package smu.team3_orda_diary.ui.alarm;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static smu.team3_orda_diary.MainActivity.mDBHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import smu.team3_orda_diary.R;
import smu.team3_orda_diary.databinding.FragmentAlarmBinding;

public class AlarmFragment extends Fragment {
    private FragmentAlarmBinding binding;
    private AlarmManager alarmManager;
    private static CameraManager mCameraManager;
    private static boolean mFlashOn = false;
    public static String mCameraId;
    private Calendar alarmC;
    private String alarmTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

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

        binding.makeButton.setOnClickListener(v -> showTimePicker());
        binding.deleteButton.setOnClickListener(v -> cancelAlarm());

        initializeFlashlight();
    }

    private void showTimePicker() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .setTitleText(getString(R.string.alarm_set))
                .setTheme(R.style.CustomTimePicker)
                .build();

        picker.show(getParentFragmentManager(), "timePicker");

        picker.addOnPositiveButtonClickListener(v -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            updateTimeText(c);
            startAlarm(c);
        });
    }

    private void updateTimeText(Calendar c) {
        String timeText = getString(R.string.alarm_scheduled) + "\n" + DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        binding.textView.setText(timeText);
    }

    private void startAlarm(Calendar c) {
        if (alarmManager == null) return;

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) flags |= FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, flags);

        if (c.before(Calendar.getInstance())) c.add(Calendar.DATE, 1);

        Date date = new Date(c.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        String alarmTime = mDBHelper.getAlarmTime();
        if (alarmTime != null) {
            mDBHelper.updateAlarm(dateStr);
        } else {
            mDBHelper.insertAlarm(dateStr);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        String alarmTime = mDBHelper.getAlarmTime();
        if (alarmTime != null) {
            mDBHelper.deleteAlarm(alarmTime);
        }

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        binding.textView.setText(R.string.alarm_canceled);
    }

    private void initializeFlashlight() {
        if (requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            mCameraManager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
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

    public static void flashLightOn() {
        if (mCameraManager != null && mCameraId != null) {
            try {
                mCameraManager.setTorchMode(mCameraId, true);
                mFlashOn = true;
            } catch (CameraAccessException e) {
                Log.e("FlashError", "Error turning on flashlight: " + e.getMessage());
            }
        }
    }

    public static void flashLightOff() {
        if (mCameraManager != null && mCameraId != null) {
            try {
                mCameraManager.setTorchMode(mCameraId, false);
                mFlashOn = false;
            } catch (CameraAccessException e) {
                Log.e("FlashError", "Error turning off flashlight: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

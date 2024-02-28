package com.ferdifir.alap;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class AlarmWorker extends Worker {

    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification();
        setNextAlarm();
        return Result.success();
    }

    @SuppressLint("MissingPermission")
    private void showNotification() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("alarm", Context.MODE_PRIVATE);

        int notificationId = sharedPref.getInt("notifId", 0);

        String channelId = "alarm_channel";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm")
                .setContentText("Alarm ke " + notificationId + " sudah berbunyi")
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationId, builder.build());

        sharedPref.edit().putInt("notifId", notificationId + 1).apply();
    }


    private void setNextAlarm() {
        int duration = generateRandomTime();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .build();

        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(duration, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(myWorkRequest);
    }

    private int generateRandomTime() {
        return (int) (Math.random() * 20 + 1);
    }
}

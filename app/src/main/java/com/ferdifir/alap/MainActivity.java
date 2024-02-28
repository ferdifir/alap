package com.ferdifir.alap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.ferdifir.alap.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        binding.btnStart.setOnClickListener(v -> startRandomAlarm());
    }

    private void startRandomAlarm() {
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(this).enqueue(myWorkRequest);
    }
}
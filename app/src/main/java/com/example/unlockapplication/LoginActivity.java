package com.example.unlockapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {


    int curBrightnessValue;
    Button LOGIN_BTN;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        runBrightnessThread();

        findViews();
        initViews();

    }


    private void findViews() {
        LOGIN_BTN = findViewById(R.id.LOGIN_BTN);
    }

    private void initViews() {
        LOGIN_BTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (matchBrightnessToMinutes()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void runBrightnessThread() {
        Thread brightnessThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(100);
                        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                        Log.d("pttt", "" + curBrightnessValue);
                    }
                } catch (Settings.SettingNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        brightnessThread.start();
    }


    boolean matchBrightnessToMinutes() {
        return curBrightnessValue == Calendar.getInstance().get(Calendar.MINUTE);
    }
}
package com.example.unlockapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {


    private static final int MAX_NUM_OF_ATTEMPTS = 3;
    private int curBrightnessValue;
    private Thread brightnessThread;
    private Button login_BTN_submit;
    private EditText login_EDT_temperature;
    private TemperatureHandler temperatureHandler;
    private int numOfAttempts;
    private CameraManager cameraManager;
    private boolean flashState;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.temperatureHandler = new TemperatureHandler(this);
        runBrightnessThread();
        findViews();
        initViews();
    }

    CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(String cameraId) {
            super.onTorchModeUnavailable(cameraId);
        }

        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            super.onTorchModeChanged(cameraId, enabled);
            flashState = enabled;
        }
    };

    private boolean tryToLogIn() {
        if (numOfAttempts == MAX_NUM_OF_ATTEMPTS) {
            return false;
        }
        return matchBrightnessToMinutes() && matchInputToTemp() && flashState;
    }

    private boolean matchInputToTemp() {
        try {
            float currentTemperature = this.temperatureHandler.getCurrentCPUTemperature();
            float guessTemperature = Float.parseFloat(login_EDT_temperature.getText().toString());
            return currentTemperature == guessTemperature;
        } catch (NumberFormatException e) {
            Log.e("pttt", e.getMessage());
            return false;
        }
    }


    private void findViews() {
        login_BTN_submit = findViewById(R.id.login_BTN_submit);
        login_EDT_temperature = findViewById(R.id.login_EDT_temperature);
    }

    private void initViews() {
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        cameraManager.registerTorchCallback(torchCallback, null);// (callback, handler)
        login_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pddd", "" + flashState);
                if (tryToLogIn()) {
                    brightnessThread.interrupt();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void runBrightnessThread() {
        brightnessThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(100);
                        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                        //Log.d("pttt", "" + curBrightnessValue);
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
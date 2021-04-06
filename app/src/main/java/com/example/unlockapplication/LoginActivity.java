package com.example.unlockapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private static final int MAX_NUM_OF_ATTEMPTS = 3;
    private Button login_BTN_login;
    private EditText login_EDT_temperature;
    private EditText login_EDT_username;
    private EditText login_EDT_password;
    private TextView login_TXT_login_status;
    private TextView login_TXT_brightness_value_helper;
    private BrightnessHandler brightnessHandler;
    private TemperatureHandler temperatureHandler;
    private CameraFlashHandler cameraFlashHandler;
    private DBMock dbMock;
    private UIUpdater mUIUpdater;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        initViews();

        this.temperatureHandler = new TemperatureHandler(this);
        this.brightnessHandler = new BrightnessHandler(this);
        this.cameraFlashHandler = new CameraFlashHandler(this);

        this.dbMock = new DBMock();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUIUpdater != null) {
            mUIUpdater.startUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mUIUpdater != null) {
            mUIUpdater.stopUpdates();
        }
    }

    private boolean tryToLogIn() {
        String username = this.login_EDT_username.getText().toString();
        String password = this.login_EDT_password.getText().toString();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            this.login_TXT_login_status.setText(R.string.invalid_inpit);
            this.login_TXT_login_status.setTextColor(Color.RED);
            return false;
        }
        User user = this.dbMock.getUser(username);
        if (user == null) {
            this.login_TXT_login_status.setText(R.string.invalid_inpit);
            this.login_TXT_login_status.setTextColor(Color.RED);
            return false;
        }
        if (user.getNumOfAttempts() == MAX_NUM_OF_ATTEMPTS) {
            this.login_TXT_login_status.setText(R.string.max_login_attempts);
            this.login_TXT_login_status.setTextColor(Color.RED);
            return false;
        }
        boolean loginResult = matchBrightnessToMinutes() && matchInputToTemp() && this.cameraFlashHandler.getFlashState() && user.getPassword().equals(password);
        if (loginResult) {
            brightnessHandler.stopBrightnessThread();
            user.resetLoginAttempts();
            this.login_TXT_login_status.setText(R.string.login_successfully);
            this.login_TXT_login_status.setTextColor(Color.GREEN);
        } else {
            user.addLoginAttempt();
            this.login_TXT_login_status.setText(R.string.login_failed);
            this.login_TXT_login_status.setTextColor(Color.RED);
        }
        return loginResult;
    }

    private boolean matchInputToTemp() {
        try {
            float currentTemperature = this.temperatureHandler.getCurrentCPUTemperature();
            float guessTemperature = Float.parseFloat(login_EDT_temperature.getText().toString());
            Log.i("Temperature", "currentTemperature: " + currentTemperature);
            return currentTemperature == guessTemperature;
        } catch (NumberFormatException e) {
            Log.e("Temperature", e.getMessage());
            return false;
        }
    }

    private void findViews() {
        this.login_BTN_login = findViewById(R.id.login_BTN_login);
        this.login_EDT_temperature = findViewById(R.id.login_EDT_temperature);
        this.login_EDT_username = findViewById(R.id.login_EDT_username);
        this.login_EDT_password = findViewById(R.id.login_EDT_password);
        this.login_TXT_login_status = findViewById(R.id.login_TXT_login_status);
        this.login_TXT_brightness_value_helper = findViewById(R.id.login_TXT_brightness_value_helper);
    }

    private void initViews() {
        login_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToLogIn();
            }
        });
        startTimer();
    }

    private void startTimer() {
        this.mUIUpdater = new UIUpdater(new Runnable() {
            @Override
            public void run() {
                login_TXT_brightness_value_helper.setText(new String("Current brightness level:" + String.valueOf(brightnessHandler.getCurrentBrightnessValue())));
            }
        });
    }

    boolean matchBrightnessToMinutes() {
        return (this.brightnessHandler.getCurrentBrightnessValue() % Calendar.getInstance().get(Calendar.MINUTE)) == 0;
    }
}
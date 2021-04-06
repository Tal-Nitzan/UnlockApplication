package com.example.unlockapplication;

import android.content.Context;
import android.provider.Settings;

public class BrightnessHandler {
    private int currentBrightnessValue;
    private Thread brightnessThread;
    private Context context;

    public BrightnessHandler(Context context) {
        this.context = context;
        runBrightnessThread();
    }

    public int getCurrentBrightnessValue() {
        return currentBrightnessValue;
    }

    private void runBrightnessThread() {
        this.brightnessThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(100);
                        currentBrightnessValue = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                        //Log.d("pttt", "" + curBrightnessValue);
                    }
                } catch (Settings.SettingNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        this.brightnessThread.start();
    }

    public void stopBrightnessThread() {
        if (this.brightnessThread.isAlive()) {
            this.brightnessThread.interrupt();
        }
    }
}

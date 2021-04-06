package com.example.unlockapplication;

import android.content.Context;
import android.hardware.camera2.CameraManager;

public class CameraFlashHandler {
    private CameraManager cameraManager;
    private boolean flashState;

    public CameraFlashHandler(Context context) {
        this.cameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        this.cameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeUnavailable(String cameraId) {
                super.onTorchModeUnavailable(cameraId);
            }

            @Override
            public void onTorchModeChanged(String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                flashState = enabled;
            }
        }, null);// (callback, handler)
    }

    public boolean getFlashState() {
        return this.flashState;
    }
}

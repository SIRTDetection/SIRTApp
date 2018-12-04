package sirtapp.sirtdetection.com.sirtapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class CameraBeta {


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }

    }

    /**
     * A safe way to get an instance of the CameraBeta object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a CameraBeta instance
        } catch (Exception e) {
            // CameraBeta is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


}

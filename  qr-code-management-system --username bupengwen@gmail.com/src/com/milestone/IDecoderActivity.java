package com.milestone;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.milestone.camera.CameraManager;

public interface IDecoderActivity {

    public ViewfinderView getViewfinder();

    public Handler getHandler();

    public CameraManager getCameraManager();

    public void handleDecode(Result rawResult, Bitmap barcode);
}

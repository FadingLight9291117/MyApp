package com.example.fadin.myapp.Activity;


import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.fadin.myapp.R;
import com.example.fadin.myapp.Util.face0.CameraController;
import com.example.fadin.myapp.Util.widget.MySurfaceView;

public class GetFaceActivity extends AppCompatActivity {

    private final String TAG = "GetFaceActivity";

    private MySurfaceView mMySurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getface);
        setTitle("录入人脸");

        initSurfaceView();

    }

    private void initSurfaceView(){

        mMySurfaceView = (MySurfaceView) findViewById(R.id.mysurfaceview);
        mMySurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG,"surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG,"surfaceChanged format:"+format+" width:"+width+" height:"+height);
                CameraController.getInstance().openCamera(holder);
                CameraController.getInstance().setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Log.i(TAG,"onPreviewFrame");



                        //将buffer添加到到摄像头，重复利用
                        camera.addCallbackBuffer(data);
                    }
                });

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG,"surfaceDestroyed");
                CameraController.getInstance().closeCamera();
            }
        });
    }
}

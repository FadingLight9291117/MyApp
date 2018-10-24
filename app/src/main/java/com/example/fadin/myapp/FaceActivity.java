package com.example.fadin.myapp;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FaceActivity extends Activity {

    private final String TAG = "FaceActivity";

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face1);
        setTitle("Demo1");

        initSurfaceView();
    }

    private void initSurfaceView(){

        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
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

                        //取出数据进行编码

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

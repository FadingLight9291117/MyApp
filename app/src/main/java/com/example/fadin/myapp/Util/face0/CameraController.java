package com.example.fadin.myapp.Util.face0;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraController implements SurfaceTexture.OnFrameAvailableListener,GLSurfaceView.Renderer{

    private final String TAG = "CameraController";

    private int mDisplayOrientation;//旋转角度
    private int mRotation;
    private CameraPosion mPosion;//前置摄像头和后置摄像头
    private int mPreViewWidth;//预览宽度
    private int mPreViewHeight;//预览高度
    private int mPreViewFps;//帧率
    private  Camera.Size mPreviewSize;

    private Camera mCamera;

    private GLSurfaceView mGlsurfaceView;
    private SurfaceTexture mSurfaceTexture;
    private int mSurfaceTextureId;
    private boolean updateSurface;
    private final float[] mTexMtx = GlUtil.createIdentityMtx();
    private RenderScreen mRenderScreen;

    private static CameraController instance;


    public enum CameraPosion{
        FRONT,
        BACK
    }

    private CameraController() {
        mDisplayOrientation = 90;//旋转角度
        mRotation = 90;
        mPosion = CameraPosion.FRONT;//前置摄像头
        mPreViewWidth = 480;//预览宽度
        mPreViewHeight = 640;//预览高度
        mPreViewFps = 20;//帧率
    }

    public static synchronized CameraController getInstance(){
        if(instance==null){
            instance = new CameraController();
        }
        return instance;
    }

    private int openCommonCamera(){
        int cameraId = mPosion == CameraPosion.FRONT ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;

        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraId) {
                mCamera = Camera.open(i);
            }
        }
        return cameraId;
    }

    //核心步骤
    public boolean openCamera(SurfaceHolder surfaceHolder){
        boolean b = true;
        try {
            //打开摄像头
            int cameraId = openCommonCamera();
            //设置预览方式
            mCamera.setPreviewDisplay(surfaceHolder);
            //根据摄像头id（打开的摄像头），获取旋转角度
            mDisplayOrientation = CameraUtils.getDisplayOrientation(cameraId);
            //设置旋转角度
            mCamera.setDisplayOrientation(mDisplayOrientation);
            //设置摄像头参数
            setPameras();
            //开始预览
            mCamera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    //第二种方式打开摄像头预览方式
    public boolean openCamera(GLSurfaceView glSurfaceView){
        boolean b = true;
        try{
            mGlsurfaceView = glSurfaceView;

            mGlsurfaceView.setEGLContextClientVersion(2);

            mGlsurfaceView.setRenderer(this);
            mGlsurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            mGlsurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    closeCamera();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            b = false;
        }

        return b;
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback callback){
        if(mCamera!=null){
            mCamera.setPreviewCallbackWithBuffer(callback);
            byte[] data = new byte[mPreViewWidth * mPreViewHeight * 3 / 2];
            mCamera.addCallbackBuffer(data);
        }

    }

    public void closeCamera(){
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Camera.Size getmPreviewSize() {
        return mPreviewSize;
    }

    /**
     * 是否横屏
     * @return
     */
    public boolean isLandscape(){
        return false;
    }


    //设置摄像头参数
    private void setPameras(){

        Camera.Size preViewSize = CameraUtils.getOptimalPreviewSize(mCamera,mPreViewWidth,mPreViewHeight);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(mRotation);
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewSize(preViewSize.width,preViewSize.height);

        int[] range = CameraUtils.adaptPreviewFps(mPreViewFps, parameters.getSupportedPreviewFpsRange());
        parameters.setPreviewFpsRange(range[0],range[1]);

        mPreviewSize = preViewSize;
    }


    private void initSurfaceTexture(){

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        //摄像头纹理
        mSurfaceTextureId = textures[0];
        mSurfaceTexture = new SurfaceTexture(mSurfaceTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mSurfaceTextureId);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i(TAG,"onFrameAvailable");
        synchronized(this) {
            updateSurface = true;
        }
        mGlsurfaceView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG,"onSurfaceCreated");

        initSurfaceTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG,"onSurfaceChanged");
        try {
            int cameraId = openCommonCamera();
            mCamera.setPreviewTexture(mSurfaceTexture);
            setPameras();
            mCamera.startPreview();

            mRenderScreen = new RenderScreen(mSurfaceTextureId);
            mRenderScreen.setSreenSize(width,height);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            Log.i(TAG,"onDrawFrame");
            synchronized(this) {
                if (updateSurface) {
                    //把数据给了mSurfaceTextureId
                    mSurfaceTexture.updateTexImage();
                    mSurfaceTexture.getTransformMatrix(mTexMtx);
                    updateSurface = false;
                }
            }

            //渲染到屏幕上
            mRenderScreen.draw(mTexMtx);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.example.fadin.myapp.Util.face0;

import android.annotation.TargetApi;
import android.hardware.Camera;

import java.util.List;

@TargetApi(14)
public class CameraUtils {

    public static int[] adaptPreviewFps(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }

    public static void setOrientation(int cameraId, boolean isLandscape, Camera camera) {
        int orientation = getDisplayOrientation(cameraId);
        if(isLandscape) {
            orientation = orientation - 90;
        }
        camera.setDisplayOrientation(orientation);
    }

    public static boolean supportTouchFocus(Camera camera) {
        if(camera != null) {
            return (camera.getParameters().getMaxNumFocusAreas() != 0);
        }
        return false;
    }

    public static void setAutoFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTouchFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Camera.Size getOptimalPreviewSize(Camera camera, int width, int height) {
        Camera.Size optimalSize = null;
        double minHeightDiff = Double.MAX_VALUE;
        double minWidthDiff = Double.MAX_VALUE;
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        if (sizes == null) return null;
        //找到宽度差距最小的
        for(Camera.Size size:sizes){
            if (Math.abs(size.width - width) < minWidthDiff) {
                minWidthDiff = Math.abs(size.width - width);
            }
        }
        //在宽度差距最小的里面，找到高度差距最小的
        for(Camera.Size size:sizes){
            if(Math.abs(size.width - width) == minWidthDiff) {
                if(Math.abs(size.height - height) < minHeightDiff) {
                    optimalSize = size;
                    minHeightDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
    }


    //根据传入的摄像头id，获取摄像头旋转角度
    public static int getDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation + 360) % 360;
        }
        return result;
    }

    public static boolean supportFlash(Camera camera){
        Camera.Parameters params = camera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if(flashModes == null) {
            return false;
        }
        for(String flashMode : flashModes) {
            if(Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
        }
        return false;
    }
}

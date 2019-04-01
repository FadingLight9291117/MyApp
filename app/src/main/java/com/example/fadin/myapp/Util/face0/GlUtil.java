package com.example.fadin.myapp.Util.face0;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Some OpenGL static utility functions.
 */
public class GlUtil {
    private GlUtil() {}

    private static final String TAG = "GlUtil";

    public static FloatBuffer createSquareVtx() {
        final float vtx[] = {
                // XYZ, UV
                -1f,  1f, 0f, 0f, 1f,
                -1f, -1f, 0f, 0f, 0f,
                1f,   1f, 0f, 1f, 1f,
                1f,  -1f, 0f, 1f, 0f,
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(vtx);
        fb.position(0);
        return fb;
    }


    public static FloatBuffer createVertexBufferImage() {  //顶点镜像
        final float vtx[] = {
                // XYZ
                1f,   1f, 0f,
                1f,  -1f, 0f,
                -1f,  1f, 0f,
                -1f, -1f, 0f,
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(vtx);
        fb.position(0);
        return fb;
    }

    public static FloatBuffer createVertexBuffer() {
        final float vtx[] = {
                // XYZ
                -1f,  1f, 0f,
                -1f, -1f, 0f,
                1f,   1f, 0f,
                1f,  -1f, 0f,
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(vtx);
        fb.position(0);
        return fb;
    }

    public static FloatBuffer createTexCoordBuffer() {
        final float vtx[] = {
                // UV
                0f, 1f,
                0f, 0f,
                1f, 1f,
                1f, 0f,
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * vtx.length);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(vtx);
        fb.position(0);
        return fb;
    }

    public static float[] createIdentityMtx() {
        float[] m = new float[16];
        Matrix.setIdentityM(m, 0);
        return m;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vs = loadShader(GLES20.GL_VERTEX_SHADER,   vertexSource);
        int fs = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        //
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        //
        return shader;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException("glGetError encountered (see log)");
        }
    }

    public static void checkEglError(String op) {
        int error;
        while ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException("eglGetError encountered (see log)");
        }
    }


    // Assert that no OpenGL ES 2.0 error has been raised.
    public static void checkNoGLES2Error(String msg) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(msg + ": GLES20 error: " + error);
        }
    }

    public static FloatBuffer createFloatBuffer(float[] coords) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    /**
     * Generate texture with standard parameters.
     */
    public static int generateTexture(int target) {
        final int textureArray[] = new int[1];
        GLES20.glGenTextures(1, textureArray, 0);
        final int textureId = textureArray[0];
        GLES20.glBindTexture(target, textureId);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        checkNoGLES2Error("generateTexture");
        return textureId;
    }
}

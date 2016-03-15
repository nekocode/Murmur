package cn.nekocode.murmur.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShaderRenderer implements GLSurfaceView.Renderer {
    final String vshader =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}" ;
    String shader;
    int program;
    int gwidth, gheight;
    boolean success;
    FloatBuffer vertexBuffer;
    long start;
    Context context;
    static float squareCoords[] = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f}; // top right


    public ShaderRenderer(Context _context, String _shader) {
        shader = _shader;
        context = _context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        success = compileShader();

        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        start = System.currentTimeMillis();

        if (success)
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        else
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    boolean compileShader() {
        int fso = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fso, shader);
        GLES20.glCompileShader(fso);
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(fso, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.e("ShadeRunner", "Error compiling shader: " + GLES20.glGetShaderInfoLog(fso));
            return false;
        }
        int vso = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vso, vshader);
        GLES20.glCompileShader(vso);
        GLES20.glGetShaderiv(vso, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.e("ShadeRunner", "Error compiling shader: " + GLES20.glGetShaderInfoLog(vso));
            return false;
        }
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vso);
        GLES20.glAttachShader(program, fso);
        GLES20.glLinkProgram(program);
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            Log.e("ShadeRunner", "Error linking program: " + GLES20.glGetProgramInfoLog(program));
            return false;
        }
        return true;
    }

    float backColor[] = new float[]{0.0f, 0.4f, 0.58f};
    public void setBackColor(int color) {
        int r = 0xFF & color;
        int g = (0xFF00 & color) >> 8;
        int b = (0xFF0000 & color) >> 16;
        backColor[2] = r * 1.0f / 255.f;
        backColor[1] = g * 1.0f / 255.f;
        backColor[0] = b * 1.0f / 255.f;
    }

    float speed = 0.9f;
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (!success)
            return;

        GLES20.glUseProgram(program);

        int handle;
        handle = GLES20.glGetUniformLocation(program, "iResolution");
        final float res[] = {(float) gwidth, (float) gheight, 1.0f};
        GLES20.glUniform3fv(handle, 1, res, 0);


        float time = ((float) (System.currentTimeMillis() - start)) / 1000.0f * speed;
        time = time % 1.5f;
        handle = GLES20.glGetUniformLocation(program, "iGlobalTime");
        GLES20.glUniform1f(handle, time);


        handle = GLES20.glGetUniformLocation(program, "BackColor");
        GLES20.glUniform3fv(handle, 1, backColor, 0);


        int pos = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(pos);
        GLES20.glVertexAttribPointer(pos, 2,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glDisableVertexAttribArray(pos);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        gwidth = width;
        gheight = height;
        GLES20.glViewport(0, 0, width, height);
    }
}
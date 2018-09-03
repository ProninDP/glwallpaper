package android.firstopneglproject.com.glwallpaper;

import android.content.Context;
import android.firstopneglproject.com.glwallpaper.objects.ParticleShooter;
import android.firstopneglproject.com.glwallpaper.objects.ParticleSystem;
import android.firstopneglproject.com.glwallpaper.programs.ParticleShaderProgram;
import android.firstopneglproject.com.glwallpaper.util.LoggerConfig;
import android.firstopneglproject.com.glwallpaper.util.MatrixHelper;
import android.firstopneglproject.com.glwallpaper.util.TextureHelper;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class ParticlesRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];    
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    
    private ParticleShaderProgram particleProgram;      
    private ParticleSystem particleSystem;
    private ParticleShooter particleShooter;

    private long globalStartTime;
    private int texture;

    private long frameStartTimeMs;
    private long startTimeMs;
    private int frameCount;



    public ParticlesRenderer(Context context) {
        this.context = context;
    }
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        // Enable additive blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        
        particleProgram = new ParticleShaderProgram(context);        
        particleSystem = new ParticleSystem(10);
        globalStartTime = System.nanoTime();

        particleShooter = new ParticleShooter();

        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {                
        glViewport(0, 0, width, height);        

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
            / (float) height, 1f, 10f);
        
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -2f, -5f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
            viewMatrix, 0);
    }
    private void limitFrameRate(int framesPerSecond) {
        long elapsedFrameTimeMs = SystemClock.elapsedRealtime() - frameStartTimeMs;
        long expectedFrameTimeMs = 1000 / framesPerSecond;
        long timeToSleepMs = expectedFrameTimeMs - elapsedFrameTimeMs;

        if (timeToSleepMs > 0) {
            SystemClock.sleep(timeToSleepMs);
        }
        frameStartTimeMs = SystemClock.elapsedRealtime();
    }
    private void logFrameRate() {
        if (LoggerConfig.ON) {
            long elapsedRealtimeMs = SystemClock.elapsedRealtime();
            double elapsedSeconds = (elapsedRealtimeMs - startTimeMs) / 1000.0;

            if (elapsedSeconds >= 1.0) {
                Log.v(TAG, frameCount / elapsedSeconds + "fps");
                startTimeMs = SystemClock.elapsedRealtime();
                frameCount = 0;
            }
            frameCount++;
        }
    }
    @Override
    public void onDrawFrame(GL10 glUnused) {
        limitFrameRate(24);
        logFrameRate();
        glClear(GL_COLOR_BUFFER_BIT);


        drawParticles();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 8000000000f;

        particleShooter.addParticles(particleSystem, currentTime, 1);

        particleProgram.useProgram();
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

    }
}
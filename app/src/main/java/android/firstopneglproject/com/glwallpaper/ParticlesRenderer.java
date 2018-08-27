package android.firstopneglproject.com.glwallpaper;

import android.content.Context;
import android.firstopneglproject.com.glwallpaper.objects.ParticleShooter;
import android.firstopneglproject.com.glwallpaper.objects.ParticleSystem;
import android.firstopneglproject.com.glwallpaper.programs.ParticleShaderProgram;
import android.firstopneglproject.com.glwallpaper.util.Geometry.Point;
import android.firstopneglproject.com.glwallpaper.util.Geometry.Vector;
import android.firstopneglproject.com.glwallpaper.util.MatrixHelper;
import android.firstopneglproject.com.glwallpaper.util.TextureHelper;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
    /*
    // Maximum saturation and value.
    private final float[] hsv = {0f, 1f, 1f};*/
    
    private ParticleShaderProgram particleProgram;      
    private ParticleSystem particleSystem;
    private ParticleShooter particleShooter;
    //private ParticleShooter greenParticleShooter;
    //private ParticleShooter blueParticleShooter;
    //private ParticleShooter randomParticleShooter;
    /*private ParticleFireworksExplosion particleFireworksExplosion;*/
    //private Random random;
    private long globalStartTime;
    private int texture;

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
        particleSystem = new ParticleSystem(10000);        
        globalStartTime = System.nanoTime();
        
        final Vector particleDirection = new Vector(0f, 0.5f, 0f);
        
        final float angleVarianceInDegrees = 5f; 
        final float speedVariance = 1f;

        Random random = new Random();
        /*
        redParticleShooter = new ParticleShooter(
            new Point(-1f, 0f, 0f), 
            particleDirection,                
            Color.rgb(255, 50, 5));
        
        greenParticleShooter = new ParticleShooter(
            new Point(0f, 0f, 0f), 
            particleDirection,
            Color.rgb(25, 255, 25));
        
        blueParticleShooter = new ParticleShooter(
            new Point(1f, 0f, 0f), 
            particleDirection,
            Color.rgb(5, 50, 255));     
        */
        particleShooter = new ParticleShooter(
            new Point(-random.nextFloat()+random.nextFloat(), random.nextFloat(), random.nextFloat()),
            particleDirection,                
            Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)),
            angleVarianceInDegrees, 
            speedVariance);
        /*
        greenParticleShooter = new ParticleShooter(
            new Point(0f, 0f, 0f), 
            particleDirection,
            Color.rgb(25, 255, 25),            
            angleVarianceInDegrees, 
            speedVariance);
        
        blueParticleShooter = new ParticleShooter(
            new Point(0.5f, 0f, 0f),
            particleDirection,
            Color.rgb(5, 50, 255),            
            angleVarianceInDegrees, 
            speedVariance);
        */
        //random = new Random();
        /*randomParticleShooter = new ParticleShooter(
                new Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(random.nextInt(255),
                        random.nextInt(255),
                        random.nextInt(255)),
                angleVarianceInDegrees,
                speedVariance);
        /*
        particleFireworksExplosion = new ParticleFireworksExplosion();
        
        random = new Random();  */
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

    @Override
    public void onDrawFrame(GL10 glUnused) {        
        glClear(GL_COLOR_BUFFER_BIT);
        
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        
        particleShooter.addParticles(particleSystem, currentTime, 1);
        //greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        //blueParticleShooter.addParticles(particleSystem, currentTime, 5);
        //randomParticleShooter.addParticles(particleSystem, currentTime, 5);
        /*
        if (random.nextFloat() < 0.02f) {
            hsv[0] = random.nextInt(360);
            
            particleFireworksExplosion.addExplosion(
                particleSystem,
                new Vector(
                    -1f + random.nextFloat() * 2f, 
                     3f + random.nextFloat() / 2f,
                    -1f + random.nextFloat() * 2f), 
                Color.HSVToColor(hsv), 
                globalStartTime);                              
        }    */
        
        particleProgram.useProgram();
        /*
        particleProgram.setUniforms(viewProjectionMatrix, currentTime);
         */
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw(); 
    }
}
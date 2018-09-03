package android.firstopneglproject.com.glwallpaper.objects;

import android.firstopneglproject.com.glwallpaper.util.Geometry.Point;
import android.firstopneglproject.com.glwallpaper.util.Geometry.Vector;
import android.graphics.Color;

import java.util.Random;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

/** This class shoots particles in a particular direction. */

public class ParticleShooter {

    
    private final Random random = new Random();
    private float[] directionVector = { 0f, 0f, 1f, 1f };
    private float[] rotationMatrix = new float[16];
    private float[] resultVector = new float[4];
    /*

    public ParticleShooter(Point position, Vector direction, int color) {
     */

    
    public void addParticles(ParticleSystem particleSystem, float currentTime,
                             int count) {
        for (int i = 0; i < count; i++) {
            final float angleVariance = 5f;
            final float speedVariance = 1f;
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() * 360f) * angleVariance,
                    (random.nextFloat() * 360f) * angleVariance,
                    (random.nextFloat() * 360f) * angleVariance);
            
            multiplyMV(
                resultVector, 0, 
                rotationMatrix, 0, 
                directionVector, 0);
            
            float speedAdjustment = 1f + random.nextFloat() * speedVariance;
            
            /*
            particleSystem.addParticle(position, color, direction, currentTime);
             */

            particleSystem.addParticle(
                    new Point(
                    -1f + random.nextFloat() * 2f,
                    0f + random.nextFloat() * 4f,
                    -1f + random.nextFloat() * 2f),
                    Color.rgb(
                            random.nextInt(255),
                            random.nextInt(255),
                            random.nextInt(255)),
                    new Vector(
                            resultVector[0] * speedAdjustment,
                            resultVector[1] * speedAdjustment,
                            resultVector[2] * speedAdjustment),
                    currentTime);
        }       
    }
}

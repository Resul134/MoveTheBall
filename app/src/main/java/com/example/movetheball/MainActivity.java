package com.example.movetheball;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//Subclass af "Activity"
public class MainActivity extends AppCompatActivity implements SensorEventListener2 {

    private float xPos, xAccel, xVel = 0.0f; // boldens positioner og accelerationer bliver defineret i x aksen.
    private float yPos, yAccel, yVel = 0.0f; //boldens positioner og accelerationer bliver defineret i y aksen.
    private float xMaks, yMaks;
    private Bitmap ball;
    private SensorManager sensorManager;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT); // Låser skærmen til portræt.
        BallView ballView = new BallView(this);
        setContentView(ballView);

        Point screenDimensions = new Point(); // Et objekt som holder 2 integer koordinater
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screenDimensions);
        xMaks = (float) screenDimensions.x - 100;
        yMaks = (float) screenDimensions.y - 100;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener( this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop(); //Kalder den før her, så systemet kan afkoble sensoren før systemet renser resourcerne.
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0];
            yAccel = sensorEvent.values[1];
            updateBall(); //Opdaterer boldens position
        }
        //Du har ikke en accelerometer!
    }


    //Metoden her behandler boldens velocity, og regner ud hvor meget bolden har rykket sig i den frame.

    private void updateBall() {
        float frameTime = 0.777f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        //Fart kan justeres her
        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;


        xPos -= xS;
        yPos -= yS;

        
        if (xPos > xMaks) {
            xPos = xMaks;
        } else if (xPos < 0) {
            xPos = 0;
        }

        if (yPos > yMaks) {
            yPos = yMaks;
        } else if (yPos < 0) {
            yPos = 0;
        }
    }

    //region, en del af interfacet, men unødvendige metoder.
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // vi bruger den ikke, den skal være her da vi har implementeret Sensoreventlistener2
    }
    @Override
    public void onFlushCompleted(Sensor sensor) {
        // vi bruger den ikke, den skal være her da vi har implementeret Sensoreventlistener2
    }
    //end region

    // Custom view
    private class BallView extends View {

        public BallView(Context context) {
            super(context);
            //henter bolden
            Bitmap ballSource = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            final int Width = 100;
            final int Height = 100;
            //sætter bolden og laver et objekt ud af den, hvor vi fylder metodens parametre ud. Meget simpelt.
            ball = Bitmap.createScaledBitmap(ballSource, Width, Height, true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(ball, xPos, yPos, null);
            invalidate();

        }
    }
}

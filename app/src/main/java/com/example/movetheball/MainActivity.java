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

public class MainActivity extends AppCompatActivity implements SensorEventListener2 {

    private float xPos, xAccel, xVel = 0.0f; // boldens positioner og accelerationer bliver defineret i x aksen.
    private float yPos, yAccel, yVel = 0.0f; //boldens positioner og accelerationer bliver defineret i y aksen.
    private float xMaks, yMaks; // Det er grænser for hvor bolden højst på løbe, så den ikke overskrider skærmen
    private Bitmap ball; // Et objekt der kan opføre sig som bolden, som vi kan lege med værdierne.
    private SensorManager sensorManager; // Sensormanager, da det er vigtig at registrere og afkoble sensoren når den ikke er i brug.

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
        super.onStart(); //Kalder den efter den har kaldet til superklassen.
        sensorManager.registerListener( this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }   //Vi fortæller hvilken sensor vi skal bruge, og dens refresh rate, hvilket er den sidste parameter.

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop(); //Kalder den før her, så systemet kan afkoble sensoren før systemet renser resourcerne.
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0]; //Henter x og y værdierne og opdaterer billederne
            yAccel = -sensorEvent.values[1]; //Det er meget vigtigt at vi opdaterer frames.
            updateBall(); //Opdaterer boldens position
        }
        //Du har ikke en accelerometer!
    }


    //Metoden her behandler boldens velocity, og regner ud hvor meget bolden har rykket sig i den frame.
    //Ved at kombinere boldens nuværende position med forskydningen, hvor man får den nye position.
    private void updateBall() {
        float frameTime = 0.666f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        //Fart kan justeres her
        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        //Sætter boldens nye position, ved at trække den xSpeed, og ySpeed fra x og y positionen.
        xPos -= xS;
        yPos -= yS;

        //Conditional statements så bolden position ikke overskrider maks når metoden opdaterer positionen.
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

    //Extends view, denne klasse kan bruges som en i view i vores onCreate.
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
        //Tegner view, vi har en canvas object, som tiladder os at udføre operationer så som bitmap/text/cirkler os
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(ball, xPos, yPos, null);//canvas objektet bruger en metode som tager imod bitmap, positioner og farve.
            invalidate();

        }
    }
}

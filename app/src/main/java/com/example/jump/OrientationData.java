package com.example.jump;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationData implements SensorEventListener {
    private SensorManager manager;
    private Sensor accelerometer;
    private Sensor magnometer;

    private float[] accelOutput;
    private float[] magOutput;

    private float[] orientation = new float[3];
    public float[] getOrientation() {
        return orientation;
    }

    //starting orientation so that we can measure correctly how the phone
    //is angled.
    private float[] startingOrientation = null;
    public float[] getStartingOrientation() {
        return startingOrientation;
    }

    //when start new game we want a new startingOrientation.
    //so we reset startOrientation
    public void newGame() {
        startingOrientation = null;
    }

    public OrientationData(Context context) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void register() {
        //sets a delay for onSensorChanged so that it doesn't go off always.
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME);
    }

    //if we don't need to currently listen we can turn off the sensor to
    //save resources.
    public void pause() {

        manager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelOutput = event.values;
        } else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magOutput = event.values;
        }
        if(accelOutput != null && magOutput != null) {
            float[] R = new float[9]; //rotation matrix
            float[] I = new float[9]; //Inclination matrix
            boolean success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
            if(success) {
                SensorManager.getOrientation(R, orientation);
                if(startingOrientation == null) {
                    startingOrientation = new float[orientation.length];
                    System.arraycopy(orientation, 0, startingOrientation,
                            0, orientation.length);
                }
            }
        }
    }
}

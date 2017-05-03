package com.example.ll4713.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private TextView xAxisView;
    private TextView yAxisView;
    private TextView zAxisView;
    private TextView magnitudeView;
    private TextView filteredMagnitudeView;

    private static final double EARTH_ACCELERATION = 9.807;

    private static final int MOVING_AVERAGE = 5;
    //private double[] accelerometerData = new double[MOVING_AVERAGE];
    //private List<Double> accelerometerData = Arrays.asList(new Double[MOVING_AVERAGE]);
    private List<Double> accelerometerData = new ArrayList<Double>();
    private int index = 0;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xAxisView = (TextView) findViewById(R.id.x_axis);
        yAxisView = (TextView) findViewById(R.id.y_axis);
        zAxisView = (TextView) findViewById(R.id.z_axis);

        magnitudeView = (TextView) findViewById(R.id.acceleration_magnitude);
        filteredMagnitudeView = (TextView) findViewById(R.id.filtered_acceleration_magnitude);

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float xAxisValue = event.values[0];
        float yAxisValue = event.values[1];
        float zAxisValue = event.values[2];
        // Do something with this sensor data.
        double magnitude = calculateMagnitude(xAxisValue, yAxisValue, zAxisValue)
                                                    - EARTH_ACCELERATION;

        double filteredMagnitude = movingAverageFilter(accelerometerData, magnitude);


        xAxisView.setText("X Axis: " + Float.toString(xAxisValue));
        yAxisView.setText("Y Axis: " + Float.toString(yAxisValue));
        zAxisView.setText("Z Axis: " + Float.toString(zAxisValue));

        //Using equation from "Improved Heading Estimation for Smartphone-Based Indoor Positioning
        // Systems" by W. Kang et al
        magnitudeView.setText("Magnitude: " +
                                                Double.toString(magnitude));
        filteredMagnitudeView.setText("Filtered Magnitude: " + Double.toString(filteredMagnitude));
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private double calculateMagnitude(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    //assuming the input size is the same as averagingNumber
    private double movingAverage(List<Double> input) {
        double sum = 0;
        for (double i : input) {
            sum += i;
        }

        return sum / input.size();
    }

    private double movingAverageFilter(List<Double> input, double newestData) {
        //calculating moving average of the current last 5 data
        double movingAverage = movingAverage(input);

        //we put the newest acceleration data into the array instead of the oldest data which is pointed
        //with index
        input.add(index, newestData);
        index++;
        index %= MOVING_AVERAGE;

        return movingAverage;
    }

}

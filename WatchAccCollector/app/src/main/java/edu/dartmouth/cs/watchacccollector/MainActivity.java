package edu.dartmouth.cs.watchacccollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import edu.dartmouth.cs.watchacccollector.accelerometer.Filter;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private BoxInsetLayout mContainerView;

    /**
     * Filter class required to filter noise from accelerometer
     */
    private Filter filter = null;


    /**
     * SensorManager
     */
    private SensorManager mSensorManager;

    /**
     * Accelerometer Sensor
     */
    private Sensor mAccelerometer;

    /**
     * Step count to be displayed in UI
     */
    private int stepCount = 0;

    /**
     * Is accelerometer running?
     */
    private static boolean isAccelRunning = false;

    //Sensor data files
    private File mRawAccFile;
    private FileOutputStream mRawAccOutputStream;

    /*
	 * Various UI components
	 */
    private TextView stepsView;
    private CompoundButton accelButton;

    private static ArrayBlockingQueue<Double> mAccBuffer;
    int ACCELEROMETER_BLOCK_CAPACITY = 160; // Size of processing window
    int ACCELEROMETER_BUFFER_CAPACITY = 3200; // Size of the blocking buffer
    // Heuristic: these is lower and up limit for step speed
    int MINIMUM_STEP_TIME_DIFF = 30;
    // If the difference bwtween the min and max is very small,
    // then the user might not walking but doing other kind of activities
    double STILLNESS_THRESHOLD = 5;

    // Create a AsyncTask to enable real time processing
    private OnSensorChangedTask mAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mAccBuffer = new ArrayBlockingQueue<Double>(ACCELEROMETER_BUFFER_CAPACITY);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();

        stepsView = (TextView)findViewById(R.id.stepCount);
        //Set the buttons and the text accordingly
        accelButton = (ToggleButton) findViewById(R.id.StartButton);
        accelButton.setChecked(isAccelRunning);
        accelButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton btn,boolean isChecked) {
                        if(!isAccelRunning) {
                            startAccelerometer();
                            accelButton.setChecked(true);
                        }
                        else {
                            stopAccelerometer();
                            accelButton.setChecked(false);
                        }
                    }
                }
        );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            mRawAccOutputStream.close();
        }catch (Exception ex)
        {
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            mContainerView.setBackground(null);
        }
    }

    /**
     * start accelerometer
     */
    private void startAccelerometer() {
        isAccelRunning = true;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        //Set up filter
        double SMOOTH_FACTOR = 0.1;
        filter = new Filter(SMOOTH_FACTOR);
        stepCount = 0;
    }

    /**
     * stop accelerometer
     */
    private void stopAccelerometer() {
        isAccelRunning = false;
        mSensorManager.unregisterListener(this);

        //Free filter and step detector
        filter = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            float accel[] = event.values;

            //First, Get filtered values
            double mag = Math.sqrt(accel[0]*accel[0] + accel[1]*accel[1] + accel[2]*accel[2]);
            double smoothed = filter.getSmoothedValue(mag);

            // Use blocking queue to reduce letency
            try {
                mAccBuffer.add(new Double(smoothed));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(smoothed));
            }

        }

    }

    private class OnSensorChangedTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... aurl) {

            int blockSize = 0;
            double[] accBlock = new double[ACCELEROMETER_BLOCK_CAPACITY];

            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled () == true)
                    {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        // Find the minimum/maximum/middle value of current window
                        max = Double.MIN_VALUE;
                        min = Double.MAX_VALUE;

                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                            if (min > val) {
                                min = val;
                            }
                        }

                        // Determine whether the user is walking through this threshold
                        if(max - min<STILLNESS_THRESHOLD)
                            continue;

                        double avg = (max+min)/2.0;
                        int last_zero_idx = -ACCELEROMETER_BLOCK_CAPACITY;

                        for(int i=1;i<ACCELEROMETER_BLOCK_CAPACITY;i++){

                            // The user's walking speed is withing a range
                            if((accBlock[i]-avg)*(accBlock[i-1]-avg)<0 && i-last_zero_idx>MINIMUM_STEP_TIME_DIFF) {
                                stepCount++;
                                last_zero_idx = i;
                            }
                        }

                        publishProgress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            sendUpdatedStepCountToUI();
        }

        @Override
        protected void onCancelled() {
            return;
        }

    }

    /* (non-Javadoc)
        * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
    */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    private void sendUpdatedStepCountToUI() {
        stepsView.setText("Steps=" + stepCount);
    }
}

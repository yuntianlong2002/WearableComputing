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
import android.widget.SeekBar;
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
    private double calCount = 0;
    private double distCount = 0;
    private int height = 175;
    private int weight = 60;


    // Tuple class
    public class Tuple<X, Y> {
        public final X timestamp;
        public final Y value;
        public Tuple(X t, Y v) {
            this.timestamp = t;
            this.value = v;
        }
    }

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
    private TextView calView;
    private TextView distView;
    private SeekBar seekBar_height;
    private SeekBar seekBar_weight;
    private TextView heightPrint;
    private TextView weightPrint;

    private CompoundButton accelButton;

    private static ArrayBlockingQueue<Tuple<Long, Double> > mAccBuffer;
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

        mAccBuffer = new ArrayBlockingQueue<>(ACCELEROMETER_BUFFER_CAPACITY);

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();

        stepsView = (TextView)findViewById(R.id.stepCount);
        calView = (TextView)findViewById(R.id.calCount);
        distView = (TextView)findViewById(R.id.distCount);
        seekBar_height = (SeekBar) findViewById(R.id.height);
        heightPrint = (TextView)findViewById(R.id.heightInput);
        height = seekBar_height.getProgress() + 130;
        heightPrint.setText(height + "cm");

        seekBar_weight = (SeekBar) findViewById(R.id.weight);
        weightPrint = (TextView)findViewById(R.id.weightInput);
        weight = seekBar_weight.getProgress() + 50;
        weightPrint.setText(weight + "kg");


        seekBar_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                height = seekBar.getProgress() + 130;
                heightPrint.setText(height + "cm");
            }
        });

        seekBar_weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                weight = seekBar.getProgress() + 50;
                weightPrint.setText(weight + "kg");
            }
        });


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
        calCount = 0;
        distCount = 0;
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
                mAccBuffer.add(new Tuple(System.currentTimeMillis(), smoothed));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Tuple<Long,Double>> newBuf = new ArrayBlockingQueue<>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Tuple(System.currentTimeMillis(), smoothed));
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

            long start_timestamp = 0;
            long end_timestamp = 0;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled () == true)
                    {
                        return null;
                    }

                    // Dumping buffer
                    Tuple<Long,Double> curr = mAccBuffer.take();
                    if(blockSize==0){
                        start_timestamp = curr.timestamp.longValue();
                    }
                    accBlock[blockSize++] = curr.value.doubleValue();



                    if (blockSize == ACCELEROMETER_BLOCK_CAPACITY) {
                        end_timestamp = curr.timestamp.longValue();
                        long time_diff_milli = end_timestamp - start_timestamp;


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


                        double avg = (max+min)/2.0;
                        int last_zero_idx = -ACCELEROMETER_BLOCK_CAPACITY;

                        int steps_this_window = 0;

                        // Determine whether the user is walking through this threshold
                        if(max - min>STILLNESS_THRESHOLD){
                            for (int i = 1; i < ACCELEROMETER_BLOCK_CAPACITY; i++) {

                                // The user's walking speed is withing a range
                                if ((accBlock[i] - avg) * (accBlock[i - 1] - avg) < 0 && i - last_zero_idx > MINIMUM_STEP_TIME_DIFF) {
                                    steps_this_window++;
                                    last_zero_idx = i;
                                }
                            }
                        }

                        // Compute distance
                        if(steps_this_window>0){
                            double steps_per_two_seconds = 2*steps_this_window/(time_diff_milli/1000.0);
                            double stride_length;
                            switch((int)steps_per_two_seconds){
                                case 1:
                                    stride_length = (double)height/500.0;
                                    break;
                                case 2:
                                    stride_length = (double)height/400.0;
                                    break;
                                case 3:
                                    stride_length = (double)height/300.0;
                                    break;
                                case 4:
                                    stride_length = (double)height/200.0;
                                    break;
                                case 5:
                                    stride_length = (double)height/100.0/1.2;
                                    break;
                                case 6:
                                    stride_length = (double)height/100.0;
                                    break;
                                default:
                                    stride_length = (double)height/100.0*1.2;
                            }
                            double speed = steps_per_two_seconds*stride_length/2.0;
                            distCount = distCount + (speed * (time_diff_milli/1000.0));

                            double cal_per_one_sec = speed * weight/800.0;
                            calCount = calCount + (cal_per_one_sec*(time_diff_milli/1000.0));


                        } else {
                            calCount = calCount + (1.0 * weight/1800.0 *(time_diff_milli/1000.0) / 2);
                        }

                        stepCount+=steps_this_window;

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
        calView.setText("Cal=" + (int)calCount);
        distView.setText("Dist=" + (int)distCount + " m");
    }
}

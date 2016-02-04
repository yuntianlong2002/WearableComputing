package com.example.android.wearable.jumpingjack;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;

public class EMAAlert {

    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;
    private Context mContext;

    private static EMAAlert mObject;
    private static Object obj = new Object();

    private static final long[] mVibratePattern = new long[]{0, 500, 500};

    private EMAAlert() {
        mMediaPlayer = new MediaPlayer();
    }

    public static EMAAlert getAlertObject() {
        synchronized (obj) {
            if (mObject == null) {
                mObject = new EMAAlert();
            }

            return mObject;
        }
    }

    public void startAlert(Context context) {
        cancel();

        synchronized (EMAAlert.this) {
            mVibrator = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
            mContext = context;

            startRing(context);
            startVibrate(context);
        }
    }

    private void startRing(Context context) {
        try {
            Uri notification = getAlertUri(context);

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(context, notification);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVibrate(Context context) {
        mVibrator.vibrate(mVibratePattern, 1);
    }

    private Uri getAlertUri(Context context) {
        String soundUriStr = "android.resource://" + context.getPackageName()
                + "/" + R.raw.ema_sound;
        Uri notification = Uri.parse(soundUriStr);

        return notification;
    }

    public void cancel() {
        synchronized (this) {
            try {
                mVibrator.cancel();
                mMediaPlayer.stop();
            } catch (Exception ex) {
            }
        }
    }
}

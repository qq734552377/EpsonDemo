package com.ucast.jnidiaoyongdemo.erweima.view.mysaomiao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ucast.jnidiaoyongdemo.Model.KeyboardSwitch;
import com.ucast.jnidiaoyongdemo.R;
import com.ucast.jnidiaoyongdemo.erweima.view.camera.CameraManager;
import com.ucast.jnidiaoyongdemo.erweima.view.view.ViewfinderView;
import com.ucast.jnidiaoyongdemo.erweima.view.decoding.CaptureActivityHandler;
import com.ucast.jnidiaoyongdemo.erweima.view.decoding.InactivityTimer;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import java.io.IOException;
import java.util.Vector;


public class CaptureActivity extends Activity implements Callback {

    private final String TAG = "CaptureActivity";

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 1.00f;
    private boolean vibrate;
    private SurfaceView surfaceView;
    public static int CAMERAID = 1;
    public static boolean ISPORTRAIT = true;
    public final static String CAMERAKEY = "camera_type";
    public final static String RESULT = "result";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        Intent intent = getIntent();
        CAMERAID = intent.getIntExtra(CAMERAKEY, 0);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        ISPORTRAIT = false;


        // 初始化 CameraManager
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        Log.e(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        initAllThing();
    }

    private void initAllThing(){
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
        try {
            CameraManager.get().startPreview();
            handler.restartPreviewAndDecode();
        }catch (Exception e){
            CaptureActivity.this.finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(final Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String result = obj.getText();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("扫描结果");
        dialog.setMessage(obj.getText());
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                KeyboardSwitch.sendToKeyboard(result);
                handler.restartPreviewAndDecode();
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                KeyboardSwitch.sendToKeyboard(result);
                handler.restartPreviewAndDecode();
            }
        });
//        dialog.create().show();
        KeyboardSwitch.sendToKeyboard(result);
        closeAc(CAMERAID,result);

    }

    public void closeAc(int type, String msg) {
        Intent intent = new Intent();
        intent.putExtra(RESULT, msg);
        setResult(type, intent);
        CaptureActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeAc(CAMERAID,"");
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}

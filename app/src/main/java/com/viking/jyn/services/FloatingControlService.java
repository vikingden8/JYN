package com.viking.jyn.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.viking.jyn.Const;
import com.viking.jyn.R;

public class FloatingControlService extends Service implements View.OnClickListener {

    private WindowManager windowManager;

    private LinearLayout floatingControls;

    private View controls;

    private IBinder binder = new ServiceBinder();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //Inflate the layout using LayoutInflater
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        floatingControls = (LinearLayout) li.inflate(R.layout.layout_floating_controls, null);
        controls = floatingControls.findViewById(R.id.controls);

        //Initialize imageButtons
        ImageButton stopIB = controls.findViewById(R.id.stop);
        stopIB.setOnClickListener(this);

        //Set layout params to display the controls over any screen.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                dpToPx(32),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // From API26, TYPE_PHONE deprecated. Use TYPE_APPLICATION_OVERLAY for O
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            params.type = WindowManager.LayoutParams.TYPE_PHONE;

        //Initial position of the floating controls
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        //Add the controls view to window manager
        windowManager.addView(floatingControls, params);

        //Add touch lister to floating controls view to move/close/expand the controls
        floatingControls.setOnTouchListener(new View.OnTouchListener() {
            boolean isMoving = false;
            private WindowManager.LayoutParams paramsF = params;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = false;
                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int xDiff = (int) (event.getRawX() - initialTouchX);
                        int yDiff = (int) (event.getRawY() - initialTouchY);
                        paramsF.x = initialX + xDiff;
                        paramsF.y = initialY + yDiff;
                        /* Set an offset of 10 pixels to determine controls moving. Else, normal touches
                         * could react as moving the control window around */
                        if (Math.abs(xDiff) > 10 || Math.abs(yDiff) > 10)
                            isMoving = true;
                        windowManager.updateViewLayout(floatingControls, paramsF);
                        break;
                }
                return false;
            }
        });
        return START_STICKY;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop:
                stopScreenSharing();
                break;
        }
    }

    /**
     * Set stop intent and start the recording service
     */
    private void stopScreenSharing() {
        Intent stopIntent = new Intent(this, RecorderService.class);
        stopIntent.setAction(Const.SCREEN_RECORDING_STOP);
        startService(stopIntent);
    }

    @Override
    public void onDestroy() {
        if (floatingControls != null) windowManager.removeView(floatingControls);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Const.TAG, "Binding successful!");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(Const.TAG, "Unbinding and stopping service");
        stopSelf();
        return super.onUnbind(intent);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public class ServiceBinder extends Binder {
        FloatingControlService getService() {
            return FloatingControlService.this;
        }
    }
}

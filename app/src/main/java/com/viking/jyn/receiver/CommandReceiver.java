package com.viking.jyn.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.viking.jyn.Const;
import com.viking.jyn.R;
import com.viking.jyn.services.RecorderService;
import com.viking.jyn.ui.MainActivity;

public class CommandReceiver extends BroadcastReceiver {

    private static final String ADB_CMD = "com.viking.jyn.action.ADB_CMD" ;

    // cmd_name should be one of them: start, stop or config
    private static final String CMD_NAME = "cmd_name" ;
    private static final String CMD_START = "start" ;
    private static final String CMD_STOP = "stop" ;
    private static final String CMD_CONFIG = "config" ;
    private static final String FILE_NAME = "file_name" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ADB_CMD)){
            Log.i(Const.TAG, "new adb command has arrived...") ;
            if (intent.hasExtra(CMD_NAME)){
                String cmd_name = intent.getStringExtra(CMD_NAME) ;
                if (CMD_START.equals(cmd_name)){
                    handleStart(context, intent) ;
                }else if (CMD_STOP.equals(cmd_name)){
                    handleStop(context, intent) ;
                }else if (CMD_CONFIG.equals(cmd_name)){
                    handleConfig(context, intent) ;
                }else{
                    Log.i(Const.TAG, "un-support command...") ;
                }
            }
        }
    }

    // start command only take a file name extra argument
    private void handleStart(Context context, Intent intent){
        Log.i(Const.TAG, "handling start command...") ;
        if (!intent.hasExtra(FILE_NAME)){
            Log.i(Const.TAG, "handle start command must has a file_name extra argument.") ;
            return ;
        }
        String file_name = intent.getStringExtra(FILE_NAME) ;
        Log.i(Const.TAG, String.format("file name: %s", file_name));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit() ;
        editor.putString(context.getString(R.string.file_name_key), file_name) ;
        editor.apply();
        boolean is_running = isServiceRunning(context, RecorderService.class) ;
        if (!is_running) {
            Intent sendStart = new Intent(context, MainActivity.class) ;
            sendStart.setAction(Const.SCREEN_RECORDING_START) ;
            context.startActivity(sendStart);
        }
    }

    private void handleStop(Context context, Intent intent){
        Log.i(Const.TAG, "handling stop command...") ;
        Intent sendStop = new Intent(context, MainActivity.class) ;
        sendStop.setAction(Const.SCREEN_RECORDING_STOP) ;
        context.startActivity(sendStop);
    }

    private static final String RESOLUTION_NAME = "resolution" ;
    private static final String FPS_NAME = "fps" ;
    private static final String BIT_RATE_NAME = "bit-rate" ;
    private static final String ORIENTATION_NAME = "orientation" ;
    private static final String AUDIO_NAME = "audio" ;
    private static final String TARGET_APP_NAME = "target-app" ;

    // there are six configure types
    // * resolution
    //     -- 0 for 720p
    //     -- 1 for 1080P
    //     -- 2 for 1440P

    // * fps-frames per second
    //     -- 0 for 25
    //     -- 1 for 30
    //     -- 2 for 35
    //     -- 3 for 40
    //     -- 4 for 50
    //     -- 5 for 60

    // * bit-rate
    //     -- 0 for 1.0 Mbit (Very low)-->1048576
    //     -- 1 for 2.5 Mbit-->2621440
    //     -- 2 for 3.5 Mbit (low)-->3670016
    //     -- 3 for 4.5 Mbit-->4718592
    //     -- 4 for 6.8 Mbit (Good)-->7130317
    //     -- 5 for 9.8 Mbit-->10276045
    //     -- 6 for 12 Mbit (Excellent)-->12582912
    //     -- 7 for 24 Mbit (Warning)-->25165824
    //     -- 8 for 48 Mbit (Warning)-->50331648

    // * orientation
    //     -- 0 for auto
    //     -- 1 for portrait
    //     -- 2 for landscape

    // * audio
    //     -- 0 for no record mic
    //     -- 1 for record mic

    // * target-app
    //     -- open target app before recording
    private void handleConfig(Context context, Intent intent){
        Log.i(Const.TAG, "handling configure command...") ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit() ;
        if (intent.hasExtra(RESOLUTION_NAME)){
            int resolution = intent.getIntExtra(RESOLUTION_NAME, 0);
            Log.i(Const.TAG, String.format("resolution: %d", resolution));
            if (resolution >=0 && resolution < 3){
                // do resolution saving...
                switch (resolution){
                    case 0:
                        editor.putString(context.getString(R.string.res_key), "720");
                        break;
                    case 1:
                        editor.putString(context.getString(R.string.res_key), "1080");
                        break;
                    case 2:
                        editor.putString(context.getString(R.string.res_key), "1440");
                        break;
                }
            }
        }
        if (intent.hasExtra(FPS_NAME)){
            int fps = intent.getIntExtra(FPS_NAME, 0) ;
            Log.i(Const.TAG, String.format("frames per second: %d", fps)) ;
            if (fps >=0 && fps < 6){
                // do fps saving...
                switch (fps){
                    case 0:
                        editor.putString(context.getString(R.string.fps_key), "25");
                        break;
                    case 1:
                        editor.putString(context.getString(R.string.fps_key), "30");
                        break;
                    case 2:
                        editor.putString(context.getString(R.string.fps_key), "35");
                        break;
                    case 3:
                        editor.putString(context.getString(R.string.fps_key), "40");
                        break;
                    case 4:
                        editor.putString(context.getString(R.string.fps_key), "50");
                        break;
                    case 5:
                        editor.putString(context.getString(R.string.fps_key), "60");
                        break;
                }
            }
        }
        if (intent.hasExtra(BIT_RATE_NAME)){
            int bit_rate = intent.getIntExtra(BIT_RATE_NAME, 0) ;
            Log.i(Const.TAG, String.format("bit rate: %d", bit_rate)) ;
            if (bit_rate >=0 && bit_rate < 9){
                // do bit rate saving...
                switch (bit_rate){
                    case 0:
                        editor.putString(context.getString(R.string.fps_key), "1048576");
                        break;
                    case 1:
                        editor.putString(context.getString(R.string.fps_key), "2621440");
                        break;
                    case 2:
                        editor.putString(context.getString(R.string.fps_key), "3670016");
                        break;
                    case 3:
                        editor.putString(context.getString(R.string.fps_key), "4718592");
                        break;
                    case 4:
                        editor.putString(context.getString(R.string.fps_key), "7130317");
                        break;
                    case 5:
                        editor.putString(context.getString(R.string.fps_key), "10276045");
                        break;
                    case 6:
                        editor.putString(context.getString(R.string.fps_key), "12582912");
                        break;
                    case 7:
                        editor.putString(context.getString(R.string.fps_key), "25165824");
                        break;
                    case 8:
                        editor.putString(context.getString(R.string.fps_key), "50331648");
                        break;
                }
            }
        }
        if (intent.hasExtra(ORIENTATION_NAME)){
            int orientation = intent.getIntExtra(ORIENTATION_NAME, 0);
            Log.i(Const.TAG, String.format("orientation: %d", orientation)) ;
            if (orientation >=0 && orientation < 3){
                // do orientation saving...
                switch (orientation){
                    case 0:
                        editor.putString(context.getString(R.string.orientation_key), "auto");
                        break;
                    case 1:
                        editor.putString(context.getString(R.string.orientation_key), "portrait");
                        break;
                    case 2:
                        editor.putString(context.getString(R.string.orientation_key), "landscape");
                        break;
                }
            }
        }
        if (intent.hasExtra(AUDIO_NAME)){
            int audio = intent.getIntExtra(AUDIO_NAME, 0) ;
            Log.i(Const.TAG, String.format("recording audio or not: %d", audio)) ;
            if (audio >=0 && audio < 2){
                // do audio saving...
                boolean recoding_audio = false ;
                if (audio == 1){
                    recoding_audio = true ;
                }
                editor.putBoolean(context.getString(R.string.audio_rec_key), recoding_audio) ;
            }
        }
        if (intent.hasExtra(TARGET_APP_NAME)){
            String target_pkg = intent.getStringExtra(TARGET_APP_NAME) ;
            Log.i(Const.TAG, String.format("target pkg name: %s", target_pkg)) ;
            if (target_pkg != null && !"".equals(target_pkg)){
                // check package exist or not
                if (isPkgExist(context, target_pkg)){
                    // do target app saving...
                    Log.i(Const.TAG, "package exist, and save it to db");
                    editor.putBoolean(context.getString(R.string.preference_enable_target_app_key), true) ;
                    editor.putString(context.getString(R.string.preference_app_chooser_key), target_pkg) ;
                }
            }
        }
        editor.apply();
    }

    private boolean isPkgExist(Context context, String pkg){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkg, 0);
            if (packageInfo != null)
                return true ;
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false ;
    }

    private boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

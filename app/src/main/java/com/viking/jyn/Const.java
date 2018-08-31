package com.viking.jyn;

import java.util.HashMap;
import java.util.Map;

public class Const {

    public enum ASPECT_RATIO {
        AR16_9(1.7777778f), AR18_9(2f);

        private static Map<Float, ASPECT_RATIO> map = new HashMap<Float, ASPECT_RATIO>();

        static {
            for (ASPECT_RATIO aspectRatio : ASPECT_RATIO.values()) {
                map.put(aspectRatio.numVal, aspectRatio);
            }
        }

        private float numVal;

        ASPECT_RATIO(float numVal) {
            this.numVal = numVal;
        }

        public static ASPECT_RATIO valueOf(float val) {
            return map.get(val) == null ? AR16_9 : map.get(val);
        }
    }

    public static final String TAG = "JYN";
    public static final String APP_DIR = "jyn";
    public static final int EXTERNAL_DIR_REQUEST_CODE = 1000;
    public static final int AUDIO_REQUEST_CODE = 1001;
    public static final int FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE = 1002;
    public static final int SCREEN_RECORD_REQUEST_CODE = 1003;
    public static final int CAMERA_SYSTEM_WINDOWS_CODE = 1007;
    public static final String SCREEN_RECORDING_START = "com.viking.jyn.services.action.start_recording";
    public static final String SCREEN_RECORDING_STOP = "com.viking.jyn.services.action.stop_recording";
    public static final int SCREEN_RECORDER_NOTIFICATION_ID = 5001;
    public static final String RECORDER_INTENT_DATA = "recorder_intent_data";
    public static final String RECORDER_INTENT_RESULT = "recorder_intent_result";
    public static final String RECORDING_NOTIFICATION_CHANNEL_ID = "recording_notification_channel_id1";
    public static final String RECORDING_NOTIFICATION_CHANNEL_NAME = "Persistent notification shown when recording screen ";
}

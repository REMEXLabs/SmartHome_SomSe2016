/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hdm.csm.smarthome.watch.services;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.watch.activities.VoiceRecognitionActivity;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom HelmutWatchface with an analog clock, indicators and custom touch actions.
 * In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 */
public class HelmutWatchfaceService extends CanvasWatchFaceService {
    private static final String LOG_TAG = "AnalogWatchFaceService";

    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final String PREFS_EMERGENCY_CALL = "emergency_call";
    private static final String PREFS_KEY_EMERGENCY_NUMBER = "emergency_contact_number";
    static final String PREFS_WATCHFACE = "watchface";
    static final String PREFS_KEY_INDICATORS = "indicator_colors";
    static final String INTENT_UPDATE_INDICATORS = "helmut_update_indicators";

    private static final long EMERGENCY_TOUCH_DELAY = 500;


    @Override
    public void onCreate(){
        super.onCreate();
    }

    public void onDestroy(){

    }

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private void makeEmergencyCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_CONTACTS, MODE_PRIVATE);
        int size = sharedPref.getInt("Size", 0);
        if (size==0) return;
        String numbers[] = new String[size];
        for(int i=0;i<size;i++) {
            numbers[i]=(sharedPref.getString(Constants.WEAR_CONTACTLIST + i, null));
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+numbers[0]));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(this, VoiceRecognitionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        AtomicInteger touchCounter = new AtomicInteger(0);
        long startTime = 0;

        static final int MSG_UPDATE_TIME = 0;

        static final float TWO_PI = (float) Math.PI * 2f;

        Paint mHourPaint;
        Paint mMinutePaint;
        Paint mSecondPaint;
        Paint mTickPaint;
        Paint mIndicatorPaint;
        Paint mLightPaint;

        boolean mMute;
        Calendar mCalendar;

        private Integer[] mIndicatorColors = new Integer[6];

        /** Handler to update the time once a second in interactive mode. */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                            Log.v(LOG_TAG, "updating time");
                        }
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        final BroadcastReceiver mIndicatorsUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "onReceive: " + intent );
                new LoadIndicatorColorsTask().execute();
            }
        };

        boolean mRegisteredReceivers = false;


        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        Bitmap mBackgroundBitmap;
        Bitmap mBackgroundScaledBitmap;

        void setIndicatorColors(Integer[] indicatorColors) {
            Log.d(LOG_TAG, "setIndicatorColors( " + Arrays.toString(indicatorColors) + " )");
            if (indicatorColors == null) {
                return;
            }
            this.mIndicatorColors = indicatorColors;
            invalidate();
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "onCreate");
            }
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(HelmutWatchfaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            Resources resources = HelmutWatchfaceService.this.getResources();
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.watchface_background_icons, null /* theme */);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            mHourPaint = new Paint();
            mHourPaint.setARGB(255, 200, 200, 200);
            mHourPaint.setStrokeWidth(5.f);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);

            mMinutePaint = new Paint();
            mMinutePaint.setARGB(255, 200, 200, 200);
            mMinutePaint.setStrokeWidth(3.f);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

            mSecondPaint = new Paint();
            mSecondPaint.setARGB(255,178, 0, 64);
            mSecondPaint.setStrokeWidth(2.f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

            mTickPaint = new Paint();
            mTickPaint.setARGB(100, 255, 255, 255);
            mTickPaint.setStrokeWidth(2.f);
            mTickPaint.setAntiAlias(true);

            mIndicatorPaint = new Paint();
            mIndicatorPaint.setARGB(255, 0, 255, 0);
            mIndicatorPaint.setStrokeWidth(15.f);
            mIndicatorPaint.setStyle(Paint.Style.STROKE);
            mIndicatorPaint.setAntiAlias(true);

            mLightPaint = new Paint();
            mLightPaint.setARGB(255, 0, 255, 0);
            mLightPaint.setStrokeWidth(15.f);
            mLightPaint.setStyle(Paint.Style.STROKE);
            mLightPaint.setAntiAlias(true);
            mLightPaint.setColor(getResources().getColor(R.color.colorPrimary));

            mCalendar = Calendar.getInstance();

            mIndicatorColors[0] = Color.BLACK;
            mIndicatorColors[1] = Color.BLACK;
            mIndicatorColors[2] = Color.BLACK;
            mIndicatorColors[3] = Color.BLACK;
            mIndicatorColors[4] = Color.BLACK;
            mIndicatorColors[5] = Color.BLACK;

            new LoadIndicatorColorsTask().execute();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "onPropertiesChanged: low-bit ambient = " + mLowBitAmbient);
            }
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "onTimeTick: ambient = " + isInAmbientMode());
            }
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "onAmbientModeChanged: " + inAmbientMode);
            }
            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                mHourPaint.setAntiAlias(antiAlias);
                mMinutePaint.setAntiAlias(antiAlias);
                mSecondPaint.setAntiAlias(antiAlias);
                mTickPaint.setAntiAlias(antiAlias);
            }
            invalidate();

            // Whether the timer should be running depends on whether we're in ambient mode (as well
            // as whether we're visible), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);
            if (mMute != inMuteMode) {
                mMute = inMuteMode;
                mHourPaint.setAlpha(inMuteMode ? 100 : 255);
                mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
                mSecondPaint.setAlpha(inMuteMode ? 80 : 255);
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                        width, height, true /* filter */);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            int width = bounds.width();
            int height = bounds.height();

            // Draw the background, scaled to fit.
            canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);

            // Find the center. Ignore the window insets so that, on round watches with a
            // "chin", the watch face is centered on the entire screen, not just the usable
            // portion.
            float centerX = width / 2f;
            float centerY = height / 2f;

            // Draw the ticks.
            float innerTickRadius = centerX - 10;
            float outerTickRadius = centerX;
            for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot = tickIndex * TWO_PI / 12;
                float innerX = (float) Math.sin(tickRot) * innerTickRadius;
                float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
                float outerX = (float) Math.sin(tickRot) * outerTickRadius;
                float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
                canvas.drawLine(centerX + innerX, centerY + innerY,
                        centerX + outerX, centerY + outerY, mTickPaint);
            }

            float seconds =
                    mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f;
            float secRot = seconds / 60f * TWO_PI;
            float minutes = mCalendar.get(Calendar.MINUTE) + seconds / 60f;
            float minRot = minutes / 60f * TWO_PI;
            float hours = mCalendar.get(Calendar.HOUR) + minutes / 60f;
            float hrRot = hours / 12f * TWO_PI;

            float secLength = centerX - 40;
            float minLength = centerX - 60;
            float hrLength = centerX - 100;

            // Circle for Light
            mLightPaint.setColor(mIndicatorColors[0]);
            canvas.drawCircle(centerX, centerY, width/2-7, mLightPaint);

            int indicatorInset = 35;

            float indicatorSize = 360.f / 4f;

            for(int i = 1; i <= 4; i++) {
                // Log.d(LOG_TAG, "drawing indicator " + i + ": " + indicatorInset + ", " + indicatorInset + ", " + (width-indicatorInset) + ", " + (height-indicatorInset) + ", " + (i*indicatorSize+3.f) + ", " + ((i+1)*indicatorSize-3.f));
                int color = mIndicatorColors[i];
                mIndicatorPaint.setARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
                canvas.drawArc(indicatorInset, indicatorInset, width-indicatorInset, height-indicatorInset, i*indicatorSize+3.f, indicatorSize-3.f, false, mIndicatorPaint);
            }


            if (!isInAmbientMode()) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondPaint);
            }

            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinutePaint);

            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourPaint);


        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "onVisibilityChanged: " + visible);
            }

            if (visible) {
                registerReceivers();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                new LoadIndicatorColorsTask().execute();
            } else {
                unregisterReceivers();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceivers() {
            if (mRegisteredReceivers) {
                return;
            }
            mRegisteredReceivers = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            HelmutWatchfaceService.this.registerReceiver(mTimeZoneReceiver, filter);

            filter = new IntentFilter(INTENT_UPDATE_INDICATORS);
            HelmutWatchfaceService.this.registerReceiver(mIndicatorsUpdateReceiver, filter);
        }

        private void unregisterReceivers() {
            if (!mRegisteredReceivers) {
                return;
            }
            mRegisteredReceivers = false;
            HelmutWatchfaceService.this.unregisterReceiver(mTimeZoneReceiver);
            HelmutWatchfaceService.this.unregisterReceiver(mIndicatorsUpdateReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "updateTimer");
            }
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * React to touch events
         */
        @Override
        public void onTapCommand(
                @TapType int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case WatchFaceService.TAP_TYPE_TAP:
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH:
                    Log.d(LOG_TAG, "onTapCommand: TAP_TYPE_TOUCH");

                    long difference = System.currentTimeMillis()-startTime;
                    startTime = System.currentTimeMillis();

                    if(difference >= EMERGENCY_TOUCH_DELAY) {
                        Timer watchfaceTapTimer = new Timer();
                        watchfaceTapTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(touchCounter.get() == 1) {
                                    Log.d(LOG_TAG, "single touch registered");
                                    startVoiceRecognition();
                                }
                            }
                        }, EMERGENCY_TOUCH_DELAY );
                        touchCounter.set(1);
                        break;
                    }

                    touchCounter.incrementAndGet();

                    if(touchCounter.get() == 3) {
                        Log.d(LOG_TAG, "3 taps in a row");

                        makeEmergencyCall();
                        touchCounter.set(0);
                    }
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL:
                    break;

                default:
                    super.onTapCommand(tapType, x, y, eventTime);
                    break;
            }
        }

        private class LoadIndicatorColorsTask extends AsyncTask<Void, Void, Integer[]> {
            @Override
            protected Integer[] doInBackground(Void... voids) {
                SharedPreferences preferences = getSharedPreferences(PREFS_WATCHFACE, MODE_PRIVATE);
                String indicatorData = preferences.getString(PREFS_KEY_INDICATORS, null);

                if (indicatorData == null) {
                    Log.e(LOG_TAG, "could not get indicator colors from preferences");
                    return null;
                }

                JsonObject indicatorColorsJson = (new JsonParser().parse(indicatorData)).getAsJsonObject();
                Integer[] indicatorColors = new Integer[6];

                Arrays.fill(indicatorColors, getResources().getColor(R.color.colorPrimaryLight));
                indicatorColors[0] = Color.BLACK;

                for (Map.Entry<String, JsonElement> e:indicatorColorsJson.entrySet()) {
                    int id = Integer.parseInt(e.getKey().substring(e.getKey().length()-1));
                    indicatorColors[id-1] = e.getValue().getAsInt();
                }

                return indicatorColors;
            }

            @Override
            protected void onPostExecute(Integer[] result) {
                setIndicatorColors(result);
            }
        }
    }
}

package hdm.csm.smarthome.phone.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import hdm.csm.smarthome.phone.OpenHabBridge;
import hdm.csm.smarthome.phone.SmartHomeApplication;
import hdm.csm.smarthome.phone.bus.GcmIdReceivedEvent;
import hdm.csm.smarthome.phone.bus.OpenHabServiceResolvedEvent;
import hdm.csm.smarthome.phone.bus.OpenHabSetupCompleteEvent;
import hdm.csm.smarthome.phone.bus.SetupCompleteEvent;
import hdm.csm.smarthome.phone.gcm.RegistrationIntentService;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundSetupService extends IntentService {

    private static final String LOG_TAG = "SetupService";
    private static final long DURATION_TIMEOUT = 10000;
    private SmartHomeApplication mApplication;
    private Bus mBus;
    private Timer mTimer;

    public BackgroundSetupService() {
        super(LOG_TAG);
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate()");
        mApplication = (SmartHomeApplication)getApplicationContext();
        mBus = mApplication.getBus();

        mBus.register(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        mBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "starting setup...");

        Log.d(LOG_TAG, "step 1 (discover OpenHAB-Service)...");
        startTimeoutTimer("discover OpenHAB-Service");
        mApplication.getNDL().startDiscoveringServices();
    }

    @Subscribe
    public void onOpenHabServiceResolved(OpenHabServiceResolvedEvent event) {
        mTimer.cancel();
        mApplication.getNDL().stopDiscoveringServices();

        Log.d(LOG_TAG, "step 2 (get GCM-ID)...");
        startTimeoutTimer("get GCM-ID");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Subscribe
    public void onGcmIdReceived(GcmIdReceivedEvent event) {
        mTimer.cancel();

        Log.d(LOG_TAG, "step 3 (setup OpenHAB)...");
        startTimeoutTimer("setup OpenHAB");
        OpenHabBridge.getInstance(this).setup(event.getGcmId());
    }

    @Subscribe
    public void onOpenHabSetupComplete(OpenHabSetupCompleteEvent event) {
        Log.d(LOG_TAG, "setup complete!");
        mApplication.postToBusOnMainThread( new SetupCompleteEvent(true) );
        mTimer.cancel();
    }

    private void startTimeoutTimer(String action) {
        mTimer = new Timer();
        mTimer.schedule(new TimeoutTask(action), DURATION_TIMEOUT);
    }

    private class TimeoutTask extends TimerTask {
        private String action;

        private TimeoutTask(String action) {
            this.action = action;
        }


        @Override
        public void run() {
            Log.e(LOG_TAG, "timeout (action = " + action +")");
            mApplication.postToBusOnMainThread(new SetupCompleteEvent(false, "Timeout (" + action + ")"));
        }
    }
}

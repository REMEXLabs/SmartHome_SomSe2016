package hdm.csm.smarthome.phone;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.facebook.stetho.Stetho;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import hdm.csm.smarthome.phone.bus.*;
import hdm.csm.smarthome.phone.gcm.RegistrationIntentService;
import hdm.csm.smarthome.phone.nsd.NetworkDiscoveryListener;

import java.util.Timer;
import java.util.TimerTask;

public class SmartHomeApplication extends Application {
    private static final String LOG_TAG = "SHA";

    static final String PREFS_OPENHAB = "openhab";
    static final String PREFS_KEY_OPENHAB_ADDRESS = "openhab_address";
    private static final long DURATION_TIMEOUT = 5000;

    private Bus mBus = new Bus();
    private NetworkDiscoveryListener mNDL = null;
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        Stetho.initializeWithDefaults(this);
        mBus.register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mBus.unregister(this);
    }

    /**
     * @return the global (application-wide) event bus
     */
    public Bus getBus() {
        return mBus;
    }

    public NetworkDiscoveryListener getNDL() {
        if (mNDL==null) {
            mNDL = new NetworkDiscoveryListener(this, getBus());
        }

        return mNDL;
    }

    public void startSetup() {
        Log.d(LOG_TAG, "starting setup...");

        Log.d(LOG_TAG, "setup step 1 (discover OpenHAB-Service)...");
        startTimeoutTimer(1, "discover OpenHAB-Service");
        getNDL().startDiscoveringServices();
    }

    /**
     * Posts the provided event on the global event bus using the main thread
     *
     * @param event to post to the bus
     */
    public void postToBusOnMainThread(final Object event) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mBus.post(event);
            }
        };
        new Handler(Looper.getMainLooper()).post(task);
    }

    /**
     * Part of the setup-process.
     *
     * Subscribes to {@link OpenHabServiceResolvedEvent}s on the global event bus,
     * updates the OpenHAB-data in the local preferences and moves on to setup step 2
     *
     * @param event containing information about the OpenHAB-service
     */
    @Subscribe
    public void onOpenHabServiceResolved(OpenHabServiceResolvedEvent event) {
        mTimer.cancel();

        OpenHabBridge.getInstance(this).setOpenHabAddress("http://" + event.getHost().getHostAddress() + ":" + event.getPort());
        getNDL().stopDiscoveringServices();

        Log.d(LOG_TAG, "setup step 2 (get GCM-ID)...");
        startTimeoutTimer(2, "get GCM-ID");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Subscribe
    public void onGcmIdReceived(GcmIdReceivedEvent event) {
        mTimer.cancel();

        Log.d(LOG_TAG, "setup step 3 (setup OpenHAB)...");
        startTimeoutTimer(3, "setup OpenHAB");
        OpenHabBridge.getInstance(this).setup(event.getGcmId());
    }

    @Subscribe
    public void onOpenHabSetupComplete(OpenHabSetupCompleteEvent event) {
        mTimer.cancel();
        Log.d(LOG_TAG, "setup complete!");
        mBus.post( new SetupCompleteEvent(true) );
    }

    @Subscribe
    public void onOpenHabSetupFailed(OpenHabSetupFailedEvent event) {
        mTimer.cancel();
        Log.d(LOG_TAG, "setup failed!");
        mBus.post( new SetupCompleteEvent(false, event.getCode() + ": " + event.getMessage()) );
    }

    private void startTimeoutTimer(int setupStep, String action) {
        mTimer = new Timer();
        mTimer.schedule(new TimeoutTask(setupStep, action), DURATION_TIMEOUT);
    }

    private class TimeoutTask extends TimerTask {
        private final int setupStep;
        private final String action;

        private TimeoutTask(int setupStep, String action) {
            this.action = action;
            this.setupStep = setupStep;
        }

        @Override
        public void run() {
            Log.e(LOG_TAG, "timeout (action = " + action +")");

            if (setupStep == 1) {
                Log.d(LOG_TAG, "using db or default address for OpenHAB");

                getNDL().stopDiscoveringServices();

                Log.d(LOG_TAG, "setup step 2 (get GCM-ID)...");
                startTimeoutTimer(2, "get GCM-ID");
                Intent intent = new Intent(SmartHomeApplication.this, RegistrationIntentService.class);
                startService(intent);

                return;
            }

            postToBusOnMainThread(new SetupCompleteEvent(false, "Timeout (" + action + ")"));
        }
    }
}

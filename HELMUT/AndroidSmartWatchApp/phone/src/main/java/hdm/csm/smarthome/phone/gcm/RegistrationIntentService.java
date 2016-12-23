package hdm.csm.smarthome.phone.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.phone.SmartHomeApplication;
import hdm.csm.smarthome.phone.bus.GcmIdReceivedEvent;

import static hdm.csm.smarthome.phone.gcm.GcmHelper.PREFS_GCM_REGISTRATION_COMPLETE;

public class RegistrationIntentService extends IntentService {
    private static final String LOG_TAG = "RegIntentService";
    public static final String INTENT_GCM_REGISTRATION_COMPLETE = "gcm_registration_complete";

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(LOG_TAG, "Starting GCM registration");

        Intent registrationComplete = new Intent(INTENT_GCM_REGISTRATION_COMPLETE);

        try {
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(LOG_TAG, "GCM Registration Token: " + token);
            registrationComplete.putExtra("token", token);

            ((SmartHomeApplication)getApplication()).postToBusOnMainThread(new GcmIdReceivedEvent(token));
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(PREFS_GCM_REGISTRATION_COMPLETE, false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}

package hdm.csm.smarthome.phone.gcm;

import android.content.Context;
import android.content.SharedPreferences;

public class GcmHelper {
    private static final String PREFS_GCM = "gcm";
    private static final String PREFS_GCM_TOKEN = "token";
    public static final String PREFS_GCM_REGISTRATION_COMPLETE = "gcm_registration_complete";

    private GcmHelper(){}

    public static boolean isRegistrationComplete(Context context) {
        return getPreferences(context).getBoolean(PREFS_GCM_REGISTRATION_COMPLETE, false);
    }

    public static void onRegistrationComplete(Context context, String gcmId) {
        getPreferences(context)
                .edit()
                .putBoolean(PREFS_GCM_REGISTRATION_COMPLETE, true)
                .putString(PREFS_GCM_TOKEN, gcmId)
                .apply();
    }

    public static void onNewToken(Context context) {
        getPreferences(context)
                .edit()
                .putBoolean(PREFS_GCM_REGISTRATION_COMPLETE, false)
                .apply();
    }

    public static String getGcmId(Context context) {
        return getPreferences(context).getString(PREFS_GCM_TOKEN, null);
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_GCM, Context.MODE_PRIVATE);
    }
}

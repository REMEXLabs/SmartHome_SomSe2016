package hdm.csm.smarthome.watch.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.common.WearCommunicationBridge;
import hdm.csm.smarthome.watch.activities.NotificationActivity;

import static hdm.csm.smarthome.watch.activities.NotificationActivity.NOTIFICATION_BELL;
import static hdm.csm.smarthome.watch.activities.NotificationActivity.NOTIFICATION_VOICE_FAIL;
import static hdm.csm.smarthome.watch.activities.NotificationActivity.NOTIFICATION_VOICE_SUCCESS;
import static hdm.csm.smarthome.watch.services.HelmutWatchfaceService.*;

/**
 * Listens to messages from the phone via bluetooth and processes them
 */
public class PhoneListenerService extends WearableListenerService {
    private static final String LOG_TAG = "PLS";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(LOG_TAG, "onMessageReceived( " + messageEvent.getPath() + " )");

        WearCommunicationBridge wearCommunicationBridge = WearCommunicationBridge.getInstance(this);

        JsonObject data = wearCommunicationBridge.decodeJsonData(messageEvent.getData());
        if (data == null) { return; }

        Log.d(LOG_TAG, "message data: "+data);
             if (data.getAsJsonArray(Constants.WEAR_CONTACTLIST) != null && data.getAsJsonArray(Constants.WEAR_CONTACTLIST).size()>0){
                 SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.PREFS_CONTACTS, MODE_PRIVATE);
                 SharedPreferences.Editor editor = sharedPref.edit();

                 JsonArray js = data.getAsJsonArray(Constants.WEAR_CONTACTLIST);
                 editor.putInt("Size", js.size());
                 for (int i = 0; i < js.size(); i++) {
                     editor.remove(Constants.WEAR_CONTACTLIST+i);
                     editor.putString(Constants.WEAR_CONTACTLIST+i, js.get(i).toString());
                     Log.d(LOG_TAG, "Emergency contacts: added "+ js.get(i).toString() + " with key: "+Constants.WEAR_CONTACTLIST+i);

                 }
                editor.apply();
             }else {
                 Log.e(LOG_TAG,"Could not save emergency numbers");
             }

            if (data.getAsJsonPrimitive(Constants.WEAR_VIBRATE) != null && data.getAsJsonPrimitive(Constants.WEAR_VIBRATE).getAsBoolean()) {
                Vibrator vibratorService = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibratorService.vibrate(1000);
                Intent intent = new Intent(this , NotificationActivity.class);
                intent.putExtra(NotificationActivity.INTENT_NOTIFICATION_ID, NOTIFICATION_BELL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        if (data.getAsJsonPrimitive(Constants.WEAR_VOICECOMMAND_SUCCESS) != null) {
            Vibrator vibratorService = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibratorService.vibrate(1000);

            Log.d(LOG_TAG, "voice command result: " + data.getAsJsonPrimitive(Constants.WEAR_VOICECOMMAND_SUCCESS).getAsString());

            if((data.getAsJsonPrimitive(Constants.WEAR_VOICECOMMAND_SUCCESS).getAsString()).equals("ON")){
                Log.d(LOG_TAG,"voice command succeeded");
                Intent intent = new Intent(this , NotificationActivity.class);
                intent.putExtra(NotificationActivity.INTENT_NOTIFICATION_ID, NOTIFICATION_VOICE_SUCCESS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);}
            else{
                Log.d(LOG_TAG,"voice command failed");
                Intent intent = new Intent(this , NotificationActivity.class);
                intent.putExtra(NotificationActivity.INTENT_NOTIFICATION_ID, NOTIFICATION_VOICE_FAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        if (data.getAsJsonObject(Constants.WEAR_WATCHFACE_INDICATOR) != null) {
            JsonObject indicatorData = data.getAsJsonObject(Constants.WEAR_WATCHFACE_INDICATOR);

            String indicator = indicatorData.getAsJsonPrimitive("indicator").getAsString();
            int color = indicatorData.getAsJsonPrimitive("color").getAsInt();

            Log.d(LOG_TAG, "indicators: received update for " + indicator + ": " + color);
            SharedPreferences preferences = getSharedPreferences(PREFS_WATCHFACE, MODE_PRIVATE);

            String indicatorsAsString = preferences.getString(PREFS_KEY_INDICATORS, "{}");
            JsonObject indicators = new JsonParser().parse(indicatorsAsString).getAsJsonObject();

            indicators.addProperty(indicator, color);

            preferences.edit()
                    .putString(PREFS_KEY_INDICATORS, indicators.toString())
                    .apply();

            Intent intent = new Intent(INTENT_UPDATE_INDICATORS);
            sendBroadcast(intent);
        }
    }
}

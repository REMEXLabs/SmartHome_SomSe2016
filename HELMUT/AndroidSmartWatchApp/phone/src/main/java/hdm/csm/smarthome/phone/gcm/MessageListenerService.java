package hdm.csm.smarthome.phone.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.common.WearCommunicationBridge;
import hdm.csm.smarthome.phone.activities.TestActivity;

public class MessageListenerService extends GcmListenerService {
    private static final String LOG_TAG = "MLS";
    private WearCommunicationBridge mWearCommunicationBridge;

    @Override
    public void onCreate() {
        mWearCommunicationBridge = WearCommunicationBridge.getInstance(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(LOG_TAG, "onMessageReceived( " + from + ", " + data +" )");

        String wearData = data.getString("wear_data");
        if(wearData != null) {
            Log.d(LOG_TAG, "forwarding to watch: " + wearData);
            mWearCommunicationBridge.sendMessage("gcm", wearData);
        }

        // sendNotification(message);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, TestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

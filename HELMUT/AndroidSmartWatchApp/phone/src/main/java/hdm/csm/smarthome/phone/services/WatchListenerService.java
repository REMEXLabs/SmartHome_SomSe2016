package hdm.csm.smarthome.phone.services;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.common.WearCommunicationBridge;
import hdm.csm.smarthome.phone.OpenHabBridge;

public class WatchListenerService extends WearableListenerService {
    private static final String LOG_TAG = "WLS";
    private OpenHabBridge mOpenHabBridge;
    private WearCommunicationBridge mWearCommunicationBridge;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate()");
        mOpenHabBridge = OpenHabBridge.getInstance(this);
        mWearCommunicationBridge = WearCommunicationBridge.getInstance(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(LOG_TAG, "onMessageReceived( path=" + messageEvent.getPath() + ", data=" + mWearCommunicationBridge.decodeStringData(messageEvent.getData()) + " )");

        if (messageEvent.getPath().equals(Constants.WEAR_VOICECOMMAND)) {
            mOpenHabBridge.sendSpeechCommand(mWearCommunicationBridge.decodeStringData(messageEvent.getData()));
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Watch Message")
                        .setContentText(messageEvent.getPath());

        // Creates an explicit intent for an Activity in your app
        // Intent resultIntent = new Intent(this, ResultActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        // stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        // stackBuilder.addNextIntent(resultIntent);
        /*
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        */


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }
}

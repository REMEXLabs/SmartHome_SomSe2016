package hdm.csm.smarthome.watch.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.ViewGroup;
import android.view.WindowManager;
import hdm.csm.smarthome.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity to show full-screen notifications on the watch
 */
public class NotificationActivity extends Activity {

    public static final String INTENT_NOTIFICATION_ID = "notificationId";
    public static final int NOTIFICATION_BELL = 1;
    public static final int NOTIFICATION_VOICE_SUCCESS = 2;
    public static final int NOTIFICATION_VOICE_FAIL = 3;
    public static final int NOTIFICATION_OVEN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int notificationId = getIntent().getIntExtra(INTENT_NOTIFICATION_ID, 0);

        setContentView(R.layout.activity_notification);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ViewGroup bgLayout = (ViewGroup) findViewById(R.id.bgLayout);
                switch (notificationId){
                    case NOTIFICATION_BELL:
                        bgLayout.setBackground(getDrawable(R.drawable.bell_ringing));
                        break;
                    case NOTIFICATION_VOICE_SUCCESS:
                        bgLayout.setBackground(getDrawable(R.drawable.voice_command_success));
                        break;
                    case NOTIFICATION_VOICE_FAIL:
                        bgLayout.setBackground(getDrawable(R.drawable.voice_command_failed));
                        break;
                    case NOTIFICATION_OVEN:
                        bgLayout.setBackground(getDrawable(R.drawable.oven_on));
                        break;
                }
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                NotificationActivity.this.finish();
            }
        }, 3000);
    }
}

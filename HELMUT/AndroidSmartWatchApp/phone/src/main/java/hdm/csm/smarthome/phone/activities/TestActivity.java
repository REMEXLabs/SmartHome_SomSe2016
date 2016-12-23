package hdm.csm.smarthome.phone.activities;

import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.JsonObject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.common.WearCommunicationBridge;
import hdm.csm.smarthome.phone.OpenHabBridge;
import hdm.csm.smarthome.phone.SmartHomeApplication;
import hdm.csm.smarthome.phone.bus.OpenHabServiceResolvedEvent;
import hdm.csm.smarthome.phone.bus.WatchfaceIndicatorsUpdatedEvent;
import hdm.csm.smarthome.phone.database.DbManager;
import hdm.csm.smarthome.phone.database.DbSchema;
import hdm.csm.smarthome.phone.nsd.NetworkDiscoveryListener;
import hdm.csm.smarthome.phone.openhab.OpenHabApi;
import hdm.csm.smarthome.phone.openhab.Thing;
import hdm.csm.smarthome.phone.services.ContactBridge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static hdm.csm.smarthome.phone.gcm.GcmHelper.PREFS_GCM_REGISTRATION_COMPLETE;
import static hdm.csm.smarthome.phone.gcm.RegistrationIntentService.INTENT_GCM_REGISTRATION_COMPLETE;

public class TestActivity extends AppCompatActivity {
    private static final String LOG_TAG = "TestActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @BindView(R.id.tvToken) TextView mTvToken;
    @BindView(R.id.tvNsdStatus) TextView mTvNsdStatus;
    @BindView(R.id.bnSendMessage) Button mBnSendMessage;
    @BindView(R.id.tvIndicator1) TextView mTvIndicator1;
    @BindView(R.id.tvIndicator2) TextView mTvIndicator2;
    @BindView(R.id.tvIndicator3) TextView mTvIndicator3;
    @BindView(R.id.tvIndicator4) TextView mTvIndicator4;
    @BindView(R.id.tvIndicator5) TextView mTvIndicator5;
    @BindView(R.id.tvIndicator6) TextView mTvIndicator6;

    private boolean isReceiverRegistered;
    private Bus mBus;
    private NetworkDiscoveryListener mNDL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBus = ((SmartHomeApplication)getApplication()).getBus();
        mBus.register(this);

        setContentView(R.layout.activity_test);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle testBundle = new Bundle();
        Bundle subBundle = new Bundle();

        subBundle.putString("substring", "Sub-Bundle-String");

        testBundle.putInt("integer", 1);
        testBundle.putFloat("float", 1.1f);
        testBundle.putString("string", "Mein String");
        testBundle.putBundle("bundle", subBundle);
        ContactBridge myContactBridge = new ContactBridge();
        myContactBridge.setContext(this);
        myContactBridge.sendContactsToWatch();



        mBnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject data = new JsonObject();
                data.addProperty(Constants.WEAR_VIBRATE, true);

                WearCommunicationBridge.getInstance(TestActivity.this).sendMessage("test/message", data.toString());
            }
        });




        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(PREFS_GCM_REGISTRATION_COMPLETE, false);
                if (sentToken) {
                    mTvToken.setText(intent.getStringExtra("token"));
                } else {
                    mTvToken.setText(R.string.gcm_registration_error);
                }
            }
        };

        registerReceiver();

        // Start IntentService to register this application with GCM.
        /*
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        */

        OpenHabBridge.getInstance(this).updateIndicatorColors();

        /*
        mNDL = ((SmartHomeApplication)getApplication()).getNDL();
        mNDL.startDiscoveringServices();
        */

        OpenHabApi openHabApi = OpenHabBridge.getInstance(this).getApi();
        if (openHabApi == null) {
            return;
        }

        openHabApi.getThings().enqueue(new Callback<List<Thing>>() {
            @Override
            public void onResponse(Call<List<Thing>> call, Response<List<Thing>> response) {
                if(response.isSuccessful()) {
                    Log.d(LOG_TAG, "retrieved things:");
                    for (Thing thing:response.body()) {
                        Log.d(LOG_TAG, "    " + thing);
                    }
                } else {
                    Log.e(LOG_TAG, "error retrieving things: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Thing>> call, Throwable t) {
                Log.e(LOG_TAG, "error retrieving things: " + t.getMessage());
            }
        });

        // OpenHabBridge.getInstance(this).updateGcmId("testid");
    }






    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mBus.unregister(this);
        super.onDestroy();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(INTENT_GCM_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    public void switchScreen(View view) {
        Intent intent = new Intent(this, ContactsView.class);
        startActivity(intent);

    }


    @OnClick(R.id.bnUpdateIndicators)
    public void updateWatchfaceIndicators() {
        OpenHabBridge.getInstance(this).updateIndicatorColors();
    }

    /**
     * Subscribes to {@link WatchfaceIndicatorsUpdatedEvent}s on the global event bus
     * and updates the view with the new indicator colors.
     *
     * @param event containing the status of the watchface-indicators
     */
    @Subscribe
    public void onWatchfaceIndicatorsUpdated(WatchfaceIndicatorsUpdatedEvent event) {
        Log.d(LOG_TAG, "onWatchfaceIndicatorsUpdated( " + event + " )");

        Integer[] colors = event.getColors();

        mTvIndicator1.setBackgroundColor(colors[0]);
        mTvIndicator2.setBackgroundColor(colors[1]);
        mTvIndicator3.setBackgroundColor(colors[2]);
        mTvIndicator4.setBackgroundColor(colors[3]);
        mTvIndicator5.setBackgroundColor(colors[4]);
        mTvIndicator6.setBackgroundColor(colors[5]);
    }

    /**
     * Subscribes to {@link OpenHabServiceResolvedEvent}s on the global event bus
     * and updates the view with information about the OpenHAB-service
     *
     * @param event containing information about the OpenHAB-service
     */
    @Subscribe
    public void onOpenHabServiceResolvedEvent(OpenHabServiceResolvedEvent event){
        Log.d(LOG_TAG, "received event: "+event);
        mTvNsdStatus.setText("NSD: http://" + event.getHost().getHostAddress() + ":" + event.getPort());
    }


}

package hdm.csm.smarthome.phone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.phone.OpenHabBridge;
import hdm.csm.smarthome.phone.SmartHomeApplication;
import hdm.csm.smarthome.phone.bus.GcmIdReceivedEvent;
import hdm.csm.smarthome.phone.bus.OpenHabServiceResolvedEvent;
import hdm.csm.smarthome.phone.bus.SetupCompleteEvent;
import hdm.csm.smarthome.phone.gcm.GcmHelper;

public class SetupActivity extends AppCompatActivity {
    private Bus mBus;
    private OpenHabBridge mOpenHabBridge;

    @BindView(R.id.tvOpenHabService) TextView tvOpenHabService;
    @BindView(R.id.tvGcmId) TextView tvGcmId;
    @BindView(R.id.tvOpenHabSetup) TextView tvOpenHabSetup;
    @BindView(R.id.tvCurrentAction) TextView tvCurrentAction;
    @BindView(R.id.etLog) EditText etLog;
    @BindView(R.id.bnStartSetup) Button bnStartSetup;

    private boolean setupRunning = false;
    private SmartHomeApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ButterKnife.bind(this);

        mApplication = (SmartHomeApplication)getApplication();

        mBus = mApplication.getBus();
        mBus.register(this);

        mOpenHabBridge = OpenHabBridge.getInstance(this);

        String openHabAddress = mOpenHabBridge.getOpenHabAddress();
        if (openHabAddress != null) {
            tvOpenHabService.setText(openHabAddress + " (db)");
        }

        String gcmId = GcmHelper.getGcmId(this);
        if (gcmId != null) {
            tvGcmId.setText(gcmId + " (db)");
        }

        tvOpenHabSetup.setText(mOpenHabBridge.isSetupComplete()?"true":"false");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @OnClick(R.id.bnStartSetup)
    public void onClickSetupStart() {
        startSetup();
    }

    private void startSetup() {
        setupRunning = true;
        bnStartSetup.setEnabled(false);
        tvCurrentAction.setText("discovering OpenHAB-service...");
        log("discovering OpenHAB-service...");
        mApplication.startSetup();
    }

    @Subscribe
    public void onOpenHabServiceResolved(OpenHabServiceResolvedEvent event) {
        log("OpenHAB-service discovered: " + event.getHost().getHostAddress() + ":" + event.getPort());
        log("getting GCM-ID...");
        tvCurrentAction.setText("getting GCM-ID...");
        tvOpenHabService.setText(event.getHost().getHostAddress() + ":" + event.getPort() + " (nsd)");
    }

    @Subscribe
    public void onGcmIdReceived(GcmIdReceivedEvent event) {
        log("GCM-ID received: " + event.getGcmId());
        log("setting up OpenHAB...");
        tvCurrentAction.setText("setting up OpenHAB...");
        tvGcmId.setText(event.getGcmId());
    }

    @Subscribe
    public void onSetupComplete(SetupCompleteEvent event) {
        setupRunning = true;
        bnStartSetup.setEnabled(true);

        if (event.isSuccess()) {
            log("setup complete!");
            tvCurrentAction.setText("setup complete!");
            tvOpenHabSetup.setText("true");
        } else {
            log("error: " + event.getMessage());
            tvCurrentAction.setText("error: " + event.getMessage());
        }
    }

    private void log(String msg) {
        etLog.append(msg + "\n");
    }
}

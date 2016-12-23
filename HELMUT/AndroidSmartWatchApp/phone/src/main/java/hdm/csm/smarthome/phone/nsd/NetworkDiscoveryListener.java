package hdm.csm.smarthome.phone.nsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import com.squareup.otto.Bus;
import hdm.csm.smarthome.phone.SmartHomeApplication;
import hdm.csm.smarthome.phone.bus.OpenHabServiceResolvedEvent;

public class NetworkDiscoveryListener implements NsdManager.DiscoveryListener {
    private static final String LOG_TAG = "NDL";
    private final Bus mBus;
    private final NsdManager mNsdManager;
    private final Context mContext;
    private boolean discoveryActive = false;

    public NetworkDiscoveryListener (Context context, Bus bus) {
        mBus = bus;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mContext = context.getApplicationContext();
    }

    public void startDiscoveringServices() {
        Log.d(LOG_TAG, "startDiscoveringServices");

        if (discoveryActive) {
            return;
        }

        mNsdManager.discoverServices(
              "_openhab-server._tcp.", NsdManager.PROTOCOL_DNS_SD, this);
    }

    public void stopDiscoveringServices() {
        if (!discoveryActive) {
            return;
        }
        mNsdManager.stopServiceDiscovery(this);
    }

    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(LOG_TAG, "Discovery failed: Error code: " + errorCode);
        discoveryActive = false;
        mNsdManager.stopServiceDiscovery(this);
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(LOG_TAG, "Discovery failed: Error code: " + errorCode);
        discoveryActive = false;
        mNsdManager.stopServiceDiscovery(this);
    }

    @Override
    public void onDiscoveryStarted(String serviceType) {
        Log.d(LOG_TAG, "Service discovery started");
        discoveryActive = true;
    }

    @Override
    public void onDiscoveryStopped(String serviceType) {
        Log.d(LOG_TAG, "Service discovery stopped");
        discoveryActive = false;
    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {
        Log.d(LOG_TAG, "Service found: " + serviceInfo);
        mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(LOG_TAG, "Failed resolving service: " + serviceInfo);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(LOG_TAG, "Service resolved: " + serviceInfo);

                ((SmartHomeApplication)mContext).postToBusOnMainThread(new OpenHabServiceResolvedEvent(
                        serviceInfo.getServiceName(),
                        serviceInfo.getServiceType(),
                        serviceInfo.getHost(),
                        serviceInfo.getPort()
                ));
            }
        });
    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {
        Log.e(LOG_TAG, "Service lost: " + serviceInfo);
    }
}

package hdm.csm.smarthome.phone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import hdm.csm.smarthome.phone.bus.OpenHabSetupCompleteEvent;
import hdm.csm.smarthome.phone.bus.OpenHabSetupFailedEvent;
import hdm.csm.smarthome.phone.bus.WatchfaceIndicatorsUpdatedEvent;
import hdm.csm.smarthome.phone.gcm.GcmHelper;
import hdm.csm.smarthome.phone.openhab.Channel;
import hdm.csm.smarthome.phone.openhab.OpenHabApi;
import hdm.csm.smarthome.phone.openhab.Thing;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static android.content.Context.MODE_PRIVATE;
import static hdm.csm.smarthome.phone.SmartHomeApplication.PREFS_KEY_OPENHAB_ADDRESS;
import static hdm.csm.smarthome.phone.SmartHomeApplication.PREFS_OPENHAB;

public class OpenHabBridge {
    private static final String LOG_TAG = "OHB";

    private static final String DEFAULT_OPENHAB_ADDRESS = "http://10.60.41.102:8080";

    private static final String OH_ITEM_CONFIG_GCMID = "gcmid";
    private static final String PREFS_KEY_OPENHAB_SETUPCOMPLETE = "setup_complete";
    private static final String THING_TYPE_UID = "androidwear:watch";
    private static final String THING_UID = THING_TYPE_UID + ":smarthome";

    private final SmartHomeApplication mApplication;
    private final Bus mBus;
    private OpenHabApi mOpenHabApi;
    private Retrofit mRetrofit;
    private SharedPreferences mPreferences;

    private String mOpenHabAddress = null;

    // singleton pattern
    private static OpenHabBridge instance = null;

    public static OpenHabBridge getInstance(Context context) {
        if (instance == null) {
            SmartHomeApplication application = (SmartHomeApplication)context.getApplicationContext();
            instance = new OpenHabBridge(application, application.getBus());
        }
        return instance;
    }

    private OpenHabBridge(SmartHomeApplication application, Bus bus) {
        mApplication = application;
        mBus = bus;
        mPreferences = application.getSharedPreferences(PREFS_OPENHAB, MODE_PRIVATE);

        initializeApi();
    }

    public void sendSpeechCommand(String command) {
        Log.d(LOG_TAG, "sendSpeechCommand( " + command + " )");

        RequestQueue queue = Volley.newRequestQueue(mApplication);
        String urlString= getOpenHabAddress()+"/classicui/CMD?VoiceCommand="+command;
        Log.d(LOG_TAG, "sendURL" + urlString);
        urlString=urlString.replaceAll(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "error: "+error);
            }
        });

        // Add the request to the RequestQueue.
        int socketTimeout = 2000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    public void updateIndicatorColors() {
        Log.d(LOG_TAG, "updateIndicatorColors");
        RequestQueue queue = Volley.newRequestQueue(mApplication);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                getOpenHabAddress() + "/rest/items/WatchfaceIndicators",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "onResponse( " + response + " )");

                        JsonObject data = new JsonParser().parse(response).getAsJsonObject();
                        JsonArray members = data.getAsJsonArray("members");
                        LinkedList<Integer> colors = new LinkedList<>();

                        for(JsonElement item:members) {
                            String state = item.getAsJsonObject().get("state").getAsString();
                            int color = Color.rgb(0, 0, 0);
                            if (!state.equals("NULL")) {
                                String[] hsb = state.split(",");
                                Log.d(LOG_TAG, Float.parseFloat(hsb[0]) + ", " + (Float.parseFloat(hsb[1])/100.0f) + ", " + (Float.parseFloat(hsb[2])/100.0f));
                                color = Color.HSVToColor(new float[]{Float.parseFloat(hsb[0]), Float.parseFloat(hsb[1])/100.0f, Float.parseFloat(hsb[2])/100.0f});
                            }

                            colors.add(color);
                        }

                        mBus.post(new WatchfaceIndicatorsUpdatedEvent(colors.toArray(new Integer[colors.size()])));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "onErrorResponse( " + error + " )");
                    }
                }
        );

        queue.add(request);
    }


    /**
     * @return the {@link OpenHabApi}
     */
    public OpenHabApi getApi() {
        return mOpenHabApi;
    }

    public boolean isSetupComplete() {
        return mPreferences.getBoolean(PREFS_KEY_OPENHAB_SETUPCOMPLETE, false);
    }

    public void setSetupComplete(boolean state) {
        mPreferences
                .edit()
                .putBoolean(PREFS_KEY_OPENHAB_SETUPCOMPLETE, state)
                .apply();
    }

    public String getOpenHabAddress() {
        if (mOpenHabAddress == null) {
            mOpenHabAddress = mPreferences.getString(PREFS_KEY_OPENHAB_ADDRESS, null);
        }

        if (mOpenHabAddress == null) {
            mOpenHabAddress = DEFAULT_OPENHAB_ADDRESS;
        }
        return mOpenHabAddress;
    }

    public void setOpenHabAddress(String address) {
        if(getOpenHabAddress() != null && address.equals(getOpenHabAddress())){
            return;
        }

        mPreferences
                .edit()
                .putString(PREFS_KEY_OPENHAB_ADDRESS, address)
                .apply();

        mOpenHabAddress = address;
        initializeApi();
    }

    public void setup(final String gcmId) {
        Log.d(LOG_TAG, "setup( " + gcmId + ")");

        HashMap<String, String> config = new HashMap<>();
        config.put("gcmid", gcmId);

        ArrayList<Channel> channels = new ArrayList<Channel>();

        Channel channelVibrate = new Channel(THING_UID + ":vibration", "vibration", "androidwear:vibration", "Switch", "", "");
        Channel channelVoiceCommandSuccess = new Channel(THING_UID + ":voiceCommandSuccess", "voiceCommandSuccess", "androidwear:voiceCommandSuccess", "Switch", "", "");
        channels.add(channelVibrate);
        channels.add(channelVoiceCommandSuccess);

        Thing thing = new Thing( "androidwear:watch:smarthome", THING_TYPE_UID, "Ingoberts Smartwatch", config, channels);

        mOpenHabApi.createThing(thing).enqueue(new CustomCallback<Thing>() {
            @Override
            public void onSuccess(Call<Thing> call, Thing body) {
                Log.d(LOG_TAG, "setup: created thing " + THING_UID);

                GcmHelper.onRegistrationComplete(mApplication, gcmId);
                setSetupComplete(true);

                mApplication.postToBusOnMainThread(new OpenHabSetupCompleteEvent());
            }

            @Override
            public void onError(Call<Thing> call, String message, int code) {
                mApplication.postToBusOnMainThread(new OpenHabSetupFailedEvent(message, code));

                Log.e(LOG_TAG, "setup: failed creating thing " + THING_UID);
            }
        });
    }

    public void updateGcmId(final String gcmId) {
        Log.d(LOG_TAG, "updateGcmID( " + gcmId + ")");

        if (!isSetupComplete()) {
            setup(gcmId);
            return;
        }

        HashMap<String, String> config = new HashMap<>();
        config.put(OH_ITEM_CONFIG_GCMID, gcmId);

        mOpenHabApi.updateThingConfig(THING_UID, config).enqueue(new CustomCallback<Thing>() {
            @Override
            public void onSuccess(Call<Thing> call, Thing body) {
                GcmHelper.onRegistrationComplete(mApplication, gcmId);
                setSetupComplete(true);

                Log.d(LOG_TAG, "Successfully updated gcmid");
            }
        });
    }

    private void initializeApi() {
        if (getOpenHabAddress() == null) {
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        mRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(getOpenHabAddress() + "/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mOpenHabApi = mRetrofit.create(OpenHabApi.class);
    }

    abstract class CustomCallback<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, retrofit2.Response<T> response) {
            if (response.isSuccessful()) {
                onSuccess(call, response.body());
            } else {
                Log.e(LOG_TAG, "OpenHAB-REST call failed: " + response.code() + ", " + response.message());
                onError(call, response.message(), response.code());
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e(LOG_TAG, "OpenHAB-REST call failed: " + t.getMessage());
            onError(call, t.getMessage(), -1);
        }

        public abstract void onSuccess(Call<T> call, T body);

        public void onError(Call<T> call, String message, int code){

        }
    }
}

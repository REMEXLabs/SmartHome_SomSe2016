package hdm.csm.smarthome.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.Charset;
import java.util.Arrays;

public class WearCommunicationBridge {
    private static WearCommunicationBridge instance;
    private String LOG_TAG = "WCB";

    private final Gson mGson;
    private GoogleApiClient mGoogleApiClient;

    // singleton pattern
    public static WearCommunicationBridge getInstance(Context context) {
        if(instance == null){
            instance = new WearCommunicationBridge(context.getApplicationContext());
        }
        return instance;
    }

    private WearCommunicationBridge(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
        mGson = GsonSingleton.getGson();
    }

    public void sendMessage(String path) {
        sendMessage(path, "");
    }

    public void sendMessage(String path, Bundle data) {
        final String dataString = data!=null?mGson.toJsonTree(data).toString():"";
        Log.d(LOG_TAG, "sendMessage( "+path+", "+dataString);

        sendMessage(path, dataString);
    }

    public void sendMessage(String path, JsonObject data) {
        sendMessage(path, data.toString());
    }

    public void sendMessage(String path, String data) {
        sendMessage(path, data.getBytes(Charset.forName("utf-16")));
    }

    private void sendMessage(final String path, final byte[] data) {
        Log.d(LOG_TAG, "sendMessage( "  + path + ", " + Arrays.toString(data) + " )");
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                Log.d(LOG_TAG, "got nodes: "+getConnectedNodesResult.getNodes().size());

                for(Node node:getConnectedNodesResult.getNodes()){
                    Log.d(LOG_TAG, "found node: "+node.getDisplayName());

                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, data).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                            Log.d(LOG_TAG, "message result: "+sendMessageResult.getStatus());
                        }
                    });
                }
            }
        });
    }

    public JsonObject decodeJsonData(byte [] data) {
        if (data.length == 0) { return null; }
        return new JsonParser().parse(new String(data, Charset.forName("utf-16"))).getAsJsonObject();
    }

    public String decodeStringData(byte [] data) {
        if (data.length == 0) { return null; }
        return new String(data, Charset.forName("utf-16"));
    }
}

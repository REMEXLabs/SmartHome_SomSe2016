package hdm.csm.smarthome.watch.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import hdm.csm.smarthome.R;
import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.common.WearCommunicationBridge;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * used to test some stuff - may not be up to date...
 */
public class TestActivity extends Activity {

    private static final int PERMISSION_CALL = 1;
    private Button mBVoiceAction;
    private TextView mTvRecognizedText;
    private Button mBSendMessage;
    private Button mBCall;
    private static final int SPEECH_REQUEST_CODE = 0;

    private WearCommunicationBridge wearCommunicationBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wearCommunicationBridge = WearCommunicationBridge.getInstance(this);

        setContentView(R.layout.activity_test);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mBVoiceAction = (Button) stub.findViewById(R.id.bnVoiceAction);
                mBVoiceAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displaySpeechRecognizer();
                    }
                });

                mTvRecognizedText = (TextView) stub.findViewById(R.id.tvRecognizedText);

                mBSendMessage = (Button) stub.findViewById(R.id.bnSendMessage);
                mBSendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("message button clicked");
                        wearCommunicationBridge.sendMessage("test/message");

                    }
                });

                mBCall = (Button) stub.findViewById(R.id.bnCall);
                mBCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.CALL_PHONE) != PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
                            }
                        } else {
                            makeCall();
                        }
                    }
                });
            }
        });
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void makeCall() {

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("filneam",getApplicationContext().MODE_PRIVATE);
        int size = sharedPref.getInt("Size", 0);
        if (size==0) return;
        String numbers[] = new String[size];
        for(int i=0;i<size;i++)
        {
            numbers[i]=(sharedPref.getString(Constants.WEAR_CONTACTLIST + i, null));
            Log.d("ADD",sharedPref.getString((Constants.WEAR_CONTACTLIST + i), null));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+numbers[0]));
        startActivity(intent);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String voiceCommand = results.get(0);

            if(voiceCommand != null && voiceCommand.length() > 0) {
                mTvRecognizedText.setText(results.get(0));
                wearCommunicationBridge.sendMessage(Constants.WEAR_VOICECOMMAND, results.get(0));
            } else {
                mTvRecognizedText.setText(R.string.voice_recognition_failed);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CALL) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                makeCall();
            }
        }
    }
}
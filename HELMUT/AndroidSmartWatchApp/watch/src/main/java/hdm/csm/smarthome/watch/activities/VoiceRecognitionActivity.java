package hdm.csm.smarthome.watch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import hdm.csm.smarthome.common.Constants;
import hdm.csm.smarthome.common.WearCommunicationBridge;

import java.util.List;

/**
 * shows the google voice recognition screen and sends the result to the phone
 */
public class VoiceRecognitionActivity extends Activity {
    private static final int SPEECH_RECOGNIZER = 1;
    private static final String LOG_TAG = "VRA";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_RECOGNIZER);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_RECOGNIZER && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String voiceCommand = results.get(0);

            if(voiceCommand != null && voiceCommand.length() > 0) {
                Log.d(LOG_TAG, "recognized: " + results.get(0));
                WearCommunicationBridge.getInstance(this).sendMessage(Constants.WEAR_VOICECOMMAND, results.get(0));
            } else {
                Log.e(LOG_TAG, "voice recognition failed");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        this.finish();
    }
}

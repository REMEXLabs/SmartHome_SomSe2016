//Copyright (c) Microsoft Corporation All rights reserved.
//
//MIT License:
//
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software.
//
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package smartcare.android.de.smartcare;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.net.Socket;

import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;

import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.client.IO;


public class BandHeartRateAppActivity extends Activity {

    private BandClient client = null;
    private TextView txtStatus;
    private RadioGroup radioGroup;
    private Button btnStart, btnEnd, btnEmergency;
    private List<Integer> heartRateList = null;
    private List<Integer> hearRateListSleep = null;
    private float heartRateDayAvg = 0.0f;
    private float heartRateSleepAvg = 0.0f;
    private boolean isSleeping = false;
    private boolean gyrActive = false;
    private boolean gyrRunning = false;
    private int numOfMovements = 0;
    private int currentGyrTrackTimeCycle = 0;
    private int currentSleepCycleId = -1;

    public static class MovementThreshold {
        public static final float xAngularThreshold = 20.f;
        public static final float yAngularThreshold = 20.f;
        public static final float zAngularThreshold = 20.f;

        public static final int trackCycles = 10;
        public static final int minNumOfMovements = 50;

    }

    public static class LastGyrData {
        public static float lastX = 0.0f;
        public static float lastY = 0.0f;
        public static float lastZ = 0.0f;

    }

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                if (isSleeping == false) {


                    heartRateList.add(event.getHeartRate());

                    if (heartRateList.size() > 1440) {
                        heartRateList.remove(0);
                    }

                    for (int i = 0; i < heartRateList.size(); i++) {
                        heartRateDayAvg += heartRateList.get(i);
                    }

                    heartRateDayAvg = heartRateDayAvg / heartRateList.size();
                    float sleepThreshold = heartRateDayAvg - (heartRateDayAvg * 0.1f);

                    if (heartRateDayAvg < sleepThreshold) {
                        gyrActive = true;
                    }
                } else {

                    hearRateListSleep.add(event.getHeartRate());

                    if (hearRateListSleep.size() > 1440) {
                        hearRateListSleep.remove(0);
                    }

                    for (int i = 0; i < hearRateListSleep.size(); i++) {
                        heartRateSleepAvg += hearRateListSleep.get(i);
                    }

                    heartRateSleepAvg = heartRateSleepAvg / hearRateListSleep.size();
                    float sleepThreshold = heartRateSleepAvg + (heartRateSleepAvg * 0.1f);

                    if (event.getHeartRate() > sleepThreshold) {
                        gyrActive = true;
                    }


                }


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();

                try {

                    String json = "value=" + event.getHeartRate() + "&" + "patientId=" + (radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())) + 1) + "&" + "date=" + sdf.format(now);

                    byte[] postData = json.getBytes("UTF-8");
                    int postDataLength = postData.length;

                    System.setProperty("http.keepAlive", "false");

                    URL url = new URL("http://smartcare.mi.hdm-stuttgart.de/api/public/heartrate.json");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("charset", "utf-8");
                    con.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.write(postData);

                    InputStream is = con.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    String line;

                    try {
                        BufferedReader r1 = new BufferedReader(new InputStreamReader(
                                is, "UTF-8"));
                        while ((line = r1.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                    } finally {
                        is.close();
                        appendToUI(sb.toString());
                        con.disconnect();
                    }

                    client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BandException e) {
                    String exceptionMessage = "";
                    switch (e.getErrorType()) {
                        case UNSUPPORTED_SDK_VERSION_ERROR:
                            exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                            break;
                        case SERVICE_ERROR:
                            exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                            break;
                        default:
                            exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                            break;
                    }
                }
            }
        }
    };
    private BandGyroscopeEventListener mGyroscopeListener = new BandGyroscopeEventListener() {
        @Override
        public void onBandGyroscopeChanged(final BandGyroscopeEvent event) {
            if (event != null) {
                gyrRunning = true;
                float x;
                float y;
                float z;

                x = Math.abs(event.getAngularVelocityX());
                y = Math.abs(event.getAngularVelocityY());
                z = Math.abs(event.getAngularVelocityZ());


                if (x > MovementThreshold.xAngularThreshold
                        || y > MovementThreshold.yAngularThreshold
                        || z > MovementThreshold.zAngularThreshold) {

                    ++numOfMovements;
                    appendToUI("Number of Movements = " + numOfMovements);
                }


                if (currentGyrTrackTimeCycle >= MovementThreshold.trackCycles) {
                    try {
                        client.getSensorManager().unregisterGyroscopeEventListener(mGyroscopeListener);
                        gyrActive = false;
                        currentGyrTrackTimeCycle = 0;
                        gyrRunning = false;

                        if (numOfMovements >= MovementThreshold.minNumOfMovements) {

                            isSleeping = !isSleeping;
                            appendToUI("Is asleep!");
                            setPatientAsleepServer(radioGroup.getFocusedChild().getId());

                            if (isSleeping) {
                                new FakeSleepCycleStart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                new FakeSleepCycleEnd().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                        numOfMovements = 0;

                    } catch (BandIOException ex) {
                        appendToUI(ex.toString());
                    }

                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heartRateList = new Vector<>();
        hearRateListSleep = new Vector<>();

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        new SensorSubscriptionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())) + 1;

                new FakeSleepCycleStart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,index);
            }
        });

        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())) + 1;
                new FakeSleepCycleEnd().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,index);
            }
        });

        btnEmergency = (Button) findViewById(R.id.btnEmergency);
        btnEmergency.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())) + 1;
                new TriggerEmergency().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,index);
            }
        });

        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        new HeartRateConsentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,reference);

        new SensorSubscriptionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    protected void onResume() {
        super.onResume();
        txtStatus.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
		if (client != null) {
			try {
				client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
			} catch (BandIOException e) {
				appendToUI(e.getMessage());
			}
		}
		*/
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    private class SensorSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);

                        while (true) {

                            if (gyrActive) {
                                ++currentGyrTrackTimeCycle;
                            }

                            Thread.sleep(60000);


                            client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);

                            if (gyrActive && !gyrRunning) {
                                client.getSensorManager().registerGyroscopeEventListener(mGyroscopeListener, SampleRate.MS128);
                            }
                        }
                    } else {
                        appendToUI("You have not given this application consent to access heart rate data yet."
                                + " Please press the Heart Rate Consent button.\n");
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage = "";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {

                if (getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage = "";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private class FakeSleepCycleStart extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();

                String json = "patientId=" + params[0] + "&" + "dateFrom=" + sdf.format(now);

                byte[] postData = json.getBytes("UTF-8");
                int postDataLength = postData.length;

                System.setProperty("http.keepAlive", "false");

                URL url = new URL("http://smartcare.mi.hdm-stuttgart.de/api/public/sleepcycle.json/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);

                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                String line;

                try {
                    BufferedReader r1 = new BufferedReader(new InputStreamReader(
                            is, "UTF-8"));
                    while ((line = r1.readLine()) != null) {
                        sb.append(line).append("\n");

                    }

                } finally {
                    is.close();
                    appendToUI(sb.toString());
                    conn.disconnect();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setPatientAsleepServer(params[0]);
            return null;
        }
    }

    private class FakeSleepCycleEnd extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {

            getSleepCycleId(params[0]);

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();


                String json = "sleepcycleId=" + currentSleepCycleId + "&" + "dateTo=" + sdf.format(now);

                byte[] postData = json.getBytes("UTF-8");
                int postDataLength = postData.length;

                System.setProperty("http.keepAlive", "false");

                URL url = new URL("http://smartcare.mi.hdm-stuttgart.de/api/public/sleepcycle.json/" + currentSleepCycleId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);

                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                String line;

                try {
                    BufferedReader r1 = new BufferedReader(new InputStreamReader(
                            is, "UTF-8"));
                    while ((line = r1.readLine()) != null) {
                        sb.append(line).append("\n");

                    }

                } finally {
                    is.close();
                    appendToUI(sb.toString());
                    conn.disconnect();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setPatientAwakeServer(params[0]);
            return null;
        }
    }

    private class TriggerEmergency extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            try {
                io.socket.client.Socket socket = IO.socket("http://smartcare.mi.hdm-stuttgart.de:8080");
               socket.connect();
                String message = "Notruf!";
                socket.emit("notification", message);
            }
            catch(URISyntaxException ex)
            {ex.printStackTrace();}
            return null;
        }
    }
    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
    }

    private void getSleepCycleId(Integer patientID) {
        try {


            System.setProperty("http.keepAlive", "false");

            URL url = new URL("http://smartcare.mi.hdm-stuttgart.de/api/public/sleepcycle.json?" + "patientId=" + patientID + "&" + "isAsleep=" + true);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            InputStream is = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"));

                while ((line = r1.readLine()) != null) {
                    sb.append(line);
                    JSONArray answer = new JSONArray(line);
                    currentSleepCycleId = Integer.parseInt(answer.getJSONObject(0).get("id").toString());
                }
            } finally {
                is.close();
                appendToUI(sb.toString());
                conn.disconnect();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setPatientAsleepServer(Integer patientID) {
        try {

            String json = "patientId=" + patientID + "&" + "isAsleep=" + true;

            byte[] postData = json.getBytes("UTF-8");
            int postDataLength = postData.length;

            System.setProperty("http.keepAlive", "false");

            URL url = new URL("http://smartcare.mi.hdm-stuttgart.de/api/public/patient.json/"+patientID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);

            InputStream is = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");

                }
            } finally {
                is.close();
                appendToUI(sb.toString());
                conn.disconnect();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setPatientAwakeServer(Integer patientID) {
        try {

            String json = "patientId=" + patientID + "&" + "isAsleep=" + false;

            byte[] postData = json.getBytes("UTF-8");
            int postDataLength = postData.length;

            System.setProperty("http.keepAlive", "false");

            URL url = new URL("http://smartcare.mi.hdm-stuttgart.de/api/public/patient.json/"+patientID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);

            InputStream is = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");

                }
            } finally {
                is.close();
                appendToUI(sb.toString());
                conn.disconnect();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }
}

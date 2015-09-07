package com.example.susinda.mapproject;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ScheduledExecutorService;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Marker srilankaMarker;
    ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        new sendPOSTTask().execute("http://146.148.86.166:8280/mapapi/1.0/location");


       // scheduler = Executors.newSingleThreadScheduledExecutor();

      // scheduler.scheduleAtFixedRate
      //          (new Runnable() {
      //              public void run() {
      //                  new ReadWeatherJSONFeedTask().execute("http://146.148.86.166:8280/mapapi/1.0/getlocation");
      //              }
      //          }, 15, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        srilankaMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(7.2, 79)).title("Susinda"));

    }



    public boolean sendPost(String url) {
        boolean result = false;
        HttpClient hc = new DefaultHttpClient();
        String message;

        HttpPost p = new HttpPost(url);
        //{"location":{"lon":"80", "lat":"7"}}
        JSONObject object = new JSONObject();
        JSONObject rootJSON = new JSONObject();
        try {

            object.put("lon", "80");
            object.put("lat", "7");
            rootJSON.put("location",object);

        } catch (Exception ex) {

        }

        try {
            message = rootJSON.toString();
            p.setEntity(new StringEntity(message, "UTF8"));
            p.setHeader("Content-type", "application/json");
            HttpResponse resp = hc.execute(p);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == 200) {
                    result = true;
                    HttpEntity entity = resp.getEntity();
                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                }
            }

            Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }

        return stringBuilder.toString();
    }


    private class ReadWeatherJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                Log.d("result ...", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONObject envelopeObject = new JSONObject(jsonObject.getString("Envelope"));
                JSONObject bodyObject = new JSONObject(envelopeObject.getString("Body"));
                JSONObject viewObject = new JSONObject(bodyObject.getString("viewResponse"));
                String lonlat = (String)viewObject.get("return");
                lonlat = lonlat.substring(1, lonlat.length() -1);
                String[] arr = lonlat.split(",");
                Double lat = Double.parseDouble(arr[0]);
                Double lon = Double.parseDouble(arr[1]);


                Log.d("returnObject", lonlat.toString());
                Toast.makeText(getBaseContext(), lonlat.toString(), Toast.LENGTH_SHORT).show();
                srilankaMarker.setPosition(new LatLng(lat, lon));


            } catch (Exception e) {
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }


    private class sendPOSTTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... urls) {
            return sendPost(urls[0]);
        }

        protected void onPostExecute(Boolean result1) {
            //Log.d("result " , result1);
        }

    }

}


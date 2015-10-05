package com.example.susinda.mapproject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Marker ceylonMarker;
    ScheduledExecutorService scheduler;
    LocationManager locationManager;
    double lon;
    double lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        new SendPOSTTask().execute("http://146.148.86.166:8280/mapapi/1.0/location");
                        new GetLocationInfoTask().execute("http://146.148.86.166:8280/mapapi/1.0/getlocation");
                    }
                }, 15, 30, TimeUnit.SECONDS);
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

    private void setUpMap() {
        ceylonMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(7.2, 79)).title("Susinda"));
    }


    private class GetLocationInfoTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            String result = doHttpGet(urls[0]);
            return result;
        }

        protected void onPostExecute(String result) {
            try {
                Log.d("result ...", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONObject envelopeObject = new JSONObject(jsonObject.getString("Envelope"));
                JSONObject bodyObject = new JSONObject(envelopeObject.getString("Body"));
                JSONObject viewObject = new JSONObject(bodyObject.getString("viewResponse"));
                String lonLat = (String)viewObject.get("return");
                lonLat = lonLat.substring(1, lonLat.length() -1);
                String[] arr = lonLat.split(",");
                Double lat = Double.parseDouble(arr[0]);
                Double lon = Double.parseDouble(arr[1]);

                Log.d("returnObject", lonLat.toString());
                Toast.makeText(getBaseContext(), lonLat.toString(), Toast.LENGTH_SHORT).show();
                ceylonMarker.setPosition(new LatLng(lat, lon));
            } catch (Exception e) {
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }

    private class SendPOSTTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            String message = null;
            //{"location":{"lon":"80", "lat":"7"}}
            JSONObject object = new JSONObject();
            JSONObject rootJSON = new JSONObject();
            try {
                object.put("lon", lon);
                object.put("lat", lat);
                rootJSON.put("location",object);
                message = rootJSON.toString();
            } catch (Exception ex) {
                Log.d("Exception on posting", ex.getMessage());
            }
            String result = doHttpPost(urls[0], message);
            return result;
        }

        protected void onPostExecute(String result) {
            Log.d("result " , result);
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //editLocation.setText("");
            //pb.setVisibility(View.INVISIBLE);
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            lon = loc.getLongitude();
            lat = loc.getLatitude();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v("lon", longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v("lat", latitude);

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: " + cityName;
            //editLocation.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }


    //Utility methods
    public String doHttpPost(String url, String message) {

        HttpClient hc = new DefaultHttpClient();
        StringBuilder stringBuilder = new StringBuilder();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(message, "UTF8"));
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse resp = hc.execute(httpPost);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = resp.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                }
            }
            Log.d("doHttpPost", "" + resp.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public String doHttpGet(String URL) {
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("doHttpGet", e.getLocalizedMessage());
        }

        return stringBuilder.toString();
    }

}

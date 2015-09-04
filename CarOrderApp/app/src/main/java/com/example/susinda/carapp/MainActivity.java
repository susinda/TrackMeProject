package com.example.susinda.carapp;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnViewOrder(View view) {

        TokenManager manager = new TokenManager();
        String username = "admin";
        String password = "admin";
//        Token token = manager.getToken(username, password);
//        TextView lblModel = (TextView) findViewById(R.id.lblModel);
//        lblModel.setText("Model : " + token.toString());

        EditText txtUrl = (EditText) findViewById(R.id.txtUrl);
        EditText txtOrderId = (EditText) findViewById(R.id.txtOrderId);
        String url = txtUrl.getEditableText().toString();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url = url + "vieworder?id=" + txtOrderId.getText().toString();
        Log.d("url .................", url);
        new ReadWeatherJSONFeedTask().execute(url);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

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
                Log.d("result .................", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONObject configObject = new JSONObject(jsonObject.getString("Configuration"));
                Log.d("configObject", configObject.toString());

                Toast.makeText(getBaseContext(),
                        configObject.getString("Model") +
                                " - " + configObject.getString("Engine"),
                        Toast.LENGTH_SHORT).show();

                TextView lblModel = (TextView) findViewById(R.id.lblModel);
                TextView lblEngine = (TextView) findViewById(R.id.lblEngine);
                TextView lblColor = (TextView) findViewById(R.id.lblColor);

                lblModel.setText("Model : " +configObject.getString("Model"));
                lblEngine.setText("Engine : " +configObject.getString("Engine"));
                lblColor.setText("Color : " +configObject.getString("Color"));

            } catch (Exception e) {
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }


 }

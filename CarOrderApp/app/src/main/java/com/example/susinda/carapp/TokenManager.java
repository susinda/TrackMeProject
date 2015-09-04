package com.example.susinda.carapp;

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by susinda on 6/15/15.
 */
public class TokenManager {

    private HttpClient httpClient;

    public TokenManager() {
        httpClient = new DefaultHttpClient();
    }

    public Token getToken(String username, String password){

        String submitUrl = "http://23.251.129.82:8285/token";
        String consumerKey = "VS3MkzSZI7pdhjjNrFwTblnkaeMa";
        String consumerSecret = "oqUykfE9qZEYL0CS77PwenyxCG4a";

        try {
            String applicationToken = consumerKey + ":" + consumerSecret;
            applicationToken = "Basic " + Base64.encode(applicationToken.getBytes(), Base64.DEFAULT);

            HttpPost httpPost = new HttpPost(submitUrl);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("grant_type", "password"));
            nameValuePair.add(new BasicNameValuePair("username", "admin"));
            nameValuePair.add(new BasicNameValuePair("password", "admin"));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                httpPost.addHeader("Authorization", applicationToken);

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
                // write response to log
                Log.d("Http Post Response:", response.toString());
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            String responseX = getResponsePayload(response);
            return JSONClient.getAccessToken(responseX);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getResponsePayload(HttpResponse response) throws IOException {
        StringBuffer buffer = new StringBuffer();
        InputStream in = null;
        try {
            if (response.getEntity() != null) {
                in = response.getEntity().getContent();
                int length;
                byte[] tmp = new byte[2048];
                while ((length = in.read(tmp)) != -1) {
                    buffer.append(new String(tmp, 0, length));
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return buffer.toString();
    }
}


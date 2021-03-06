package com.training.superior.superiortraining.Models;

import android.os.AsyncTask;
import android.webkit.CookieManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by joakim on 4/12/15.
 */
public class ExerciseTask extends AsyncTask<Void, Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();

        //Load saved cookies from cookie store.
        String[] keyValueSets = CookieManager.getInstance().getCookie(Misc.ServerAddress).split(";");

        String sessionCookie = "";
        for(String cookie : keyValueSets)
        {
            if(cookie.contains("session"))
                sessionCookie = cookie;
            System.out.println(cookie.toString());
        }

        JSONArray jsonArray = new JSONArray();
        try {
            HttpGet request = new HttpGet(Misc.ServerAddress + "/exercises");

            request.addHeader("Cookie", sessionCookie);
            request.addHeader("content-type", "application/x-www-form-urlencoded");

            HttpResponse response = httpClient.execute(request);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String readString, jSonString = "";
            while ( (readString = in.readLine()) != null ) {
                jSonString += readString;
            }
            jsonArray = new JSONArray(jSonString);
            System.out.println(jsonArray.toString());
            System.out.println(request.getURI());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return jsonArray;
    }
}

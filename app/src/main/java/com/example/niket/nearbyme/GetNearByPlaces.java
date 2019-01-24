package com.example.niket.nearbyme;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class GetNearByPlaces extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    InputStream inputStream;
    BufferedReader bufferedReader;
    StringBuilder builder;
    String data;
    String placeType;


    public GetNearByPlaces(String placeType) {

        this.placeType = placeType;
    }

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        try {
            URL myUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {

                builder.append(line);

            }

            data = builder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject parentJsonObject = new JSONObject(s);
            JSONArray jsonArray = parentJsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONObject gemetryObject = jsonObject.getJSONObject("geometry").getJSONObject("location");
                String lat = gemetryObject.getString("lat");
                String lang = gemetryObject.getString("lng");

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lang));

                String name = jsonObject.getString("name");
                String vicinity = jsonObject.getString("vicinity");

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name + "(" + vicinity + ")");
                markerOptions.position(latLng);

                if (placeType.equals("atm"))
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_atm));
                else if (placeType.equals("hospital"))
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_hosp));
                else if (placeType.equals("supermarket"))
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_shop));
                else if (placeType.equals("restaurant"))
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_res));
                else
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

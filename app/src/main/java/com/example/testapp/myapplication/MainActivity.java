package com.example.testapp.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button weatherButton;
    TextView weatherText;

    double myLatitude = 0.00, myLongitude = 0.00;
    String LOCATION_TYPE = LocationManager.NETWORK_PROVIDER;
    long MIN_TIME = 5000;
    float MIN_DISTANCE = 1000;
    final int REQ_CODE = 999;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    final String APP_ID = "YOUR APP ID HERE";


    // required objects for getting location
    LocationManager myLocationManager;
    LocationListener myLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = (TextView) findViewById(R.id.weatherText);
        weatherButton = (Button) findViewById(R.id.weatherButton);

        weatherButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // get the location
                weatherText.setText("Retrieving the weather...");
                getWeather();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get the device location
        getDeviceLocation();
    }

    private void getDeviceLocation() {

        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        myLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                myLatitude = location.getLatitude();
                myLongitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                // Location provider is disabled, update the textarea
                Log.d("LocIssue", "Location provider disabled.");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_CODE);

            return;
        }

        myLocationManager.requestLocationUpdates(LOCATION_TYPE, MIN_TIME, MIN_DISTANCE, myLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            }
            else {
                Log.d("CameraPermissions", "Permission denied.");
            }
        }
    }

    private void getWeather() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_CODE);

            return;
        }

        Location location = myLocationManager.getLastKnownLocation(LOCATION_TYPE);

        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

        // request parameters for openweathermap
        RequestParams myParams = new RequestParams();
        myParams.put("appid", APP_ID);
        myParams.put("lat", myLatitude);
        myParams.put("lon", myLongitude);



        AsyncHttpClient myClient = new AsyncHttpClient();

        myClient.get(WEATHER_URL, myParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    weatherText.setText("Your current weather is " + response.getJSONArray("weather").getJSONObject(0).getString("main"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // a failure status, log to the console
                Log.d("STATUS", "fail!");
                Log.e("MyApp", "Failure: " + e.toString());
            }
        });
    }
}

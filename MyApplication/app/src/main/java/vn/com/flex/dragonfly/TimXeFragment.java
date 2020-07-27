package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.obj.InfoMain;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndung on 12/23/15.
 */
public class TimXeFragment extends FragmentActivity {
    private InfoMain mInfoMain;
    private GoogleMap mGoogleMap;
    private ArrayList<LatLng> mMarkerPoints;
    private DownloadTask downloadTask;
    private ParserTask parserTask;
    private String distance, duration;
    private TextView txt_space;
    private ProgressDialog pDialog;
    private boolean flagFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tim_xe_layout);
        savedInstanceState = getIntent().getExtras();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_timxe);
        mGoogleMap = fm.getMap();
        // Enable MyLocation Button in the Map
        mGoogleMap.setMyLocationEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        pDialog.show();
        flagFirst = true;
        if (checkGPS()) {
            if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
                mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
                txt_space = (TextView) findViewById(R.id.txt_space);
                mMarkerPoints = new ArrayList<>();
                mGoogleMap.setOnMyLocationChangeListener (new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if (flagFirst && location != null) {
                            flagFirst = false;
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                            mMarkerPoints.add(current);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(current, 16);
                            mGoogleMap.animateCamera(cameraUpdate);
                            LatLng timXe = null;
                            if (mInfoMain != null) {
                                TextView txt_btn = (TextView) findViewById(R.id.txt_btn);
                                txt_btn.setText(mInfoMain.devicename);
                                timXe = new LatLng(Double.valueOf(mInfoMain.latitude), Double.valueOf(mInfoMain.longitude));
                                mMarkerPoints.add(timXe);
                            }
                            if (mMarkerPoints.size() >= 2) {
                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(mMarkerPoints.get(1))
                                        .icon(BitmapDescriptorFactory.fromResource(mInfoMain.getIconMakerResourceId())));

                                // Getting URL to the Google Directions API
                                String url = getDirectionsUrl(mMarkerPoints.get(0), mMarkerPoints.get(1));
                                downloadTask = new DownloadTask();
                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
                            }
                        }
                        pDialog.dismiss();
                    }
                });
            }
        } else {
            Toast.makeText(TimXeFragment.this, "Không có GPS", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }

        AnimationLinearLayout btnBack = (AnimationLinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
        if (parserTask != null) {
            parserTask.cancel(true);
        }
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        if (j == 0) {
                            // Get distance from the list
                            distance = (String) point.get("distance");
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                        } else {
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLUE);
                }
                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    mGoogleMap.addPolyline(lineOptions);
                    txt_space.setText("Khoảng cách: " + distance + ", thời gian: " + duration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private boolean checkGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

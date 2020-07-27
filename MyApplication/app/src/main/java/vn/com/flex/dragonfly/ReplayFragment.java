package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import vn.com.flex.dragonfly.adapter.CustomInfoWindowReplayAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.lib.MultiDirectionSlidingDrawer;
import vn.com.flex.dragonfly.obj.InfoMain;
import vn.com.flex.dragonfly.obj.ReplayItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Matrix;

/**
 * Created by Nguyen on 12/19/2015.
 */
public class ReplayFragment extends FragmentActivity {

    private GoogleMap mGoogleMap;
    private RequestQueue mVolleyQueue;
    private ProgressDialog pDialog;
    private InfoMain mInfoMain;
    private ArrayList<ReplayItem> mListReplayItem;
    private Marker marker;
    private TextView mSearchView;
    private MultiDirectionSlidingDrawer mDrawer;
    private CalendarView mCalend;
    private AnimationLinearLayout btn_cancel, btn_ok;
    private boolean flagPlay = false;
    private int mSpeedPlay = 0, index = 0, mPlayTimeFast = 1000, lineWidth = 12;
    private boolean flagClear = false;
    private Handler mHandlerProcess = new Handler();
    private final double degreesPerRadian = 180.0 / Math.PI;
    private SeekBar mSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replay_screen);
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        mSearchView = (TextView) findViewById(R.id.txt_search_view);
        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        findViewById(R.id.picker).setVisibility(View.GONE);
        findViewById(R.id.layout_btn_calendar).setVisibility(View.VISIBLE);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        btn_cancel = (AnimationLinearLayout) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.close();
            }
        });
        btn_ok = (AnimationLinearLayout) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagPlay = false;
                findViewById(R.id.img_play).setBackgroundResource(R.drawable.play_1);
                stopCallTimePlayMap();
                mSeekBar.setProgress(0);
                mGoogleMap.clear();
                pDialog.show();
                getDataApi(mSearchView.getText().toString());
                mDrawer.close();
            }
        });
        flagClear = true;
        mSearchView.setText(getDateCurrent());
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawer.isOpened()) {
                    mDrawer.close();
                } else {
                    mDrawer.open();
                }
            }
        });
        mCalend = (CalendarView) findViewById(R.id.calendView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mCalend.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    flagClear = true;
                    mSearchView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                }
            });
        }

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
            mListReplayItem = new ArrayList<>();
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            FragmentManager fragment = getSupportFragmentManager();
            SupportMapFragment fm = (SupportMapFragment) fragment.findFragmentById(R.id.map_replay);
            // Getting Map for the SupportMapFragment
            mGoogleMap = fm.getMap();
            // Enable MyLocation Button in the Map
            mGoogleMap.setMyLocationEnabled(true);
        }

        getDataApi(mSearchView.getText().toString());

        AnimationLinearLayout btnBack = (AnimationLinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AnimationLinearLayout btnPlay = (AnimationLinearLayout) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListReplayItem != null && mListReplayItem.size() > 0) {
                    if (flagPlay) {
                        flagPlay = false;
                        findViewById(R.id.img_play).setBackgroundResource(R.drawable.play_1);
                        stopCallTimePlayMap();
                    } else {
                        flagPlay = true;
                        findViewById(R.id.img_play).setBackgroundResource(R.drawable.pause);
                        mDrawer.close();
                        if (flagClear && mGoogleMap != null) {
                            mGoogleMap.clear();
                            flagClear = false;
                        }
                        setTimePlayMap();
                    }
                }
            }
        });

        AnimationLinearLayout btnPlayFast = (AnimationLinearLayout) findViewById(R.id.btn_play_fast);
        btnPlayFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListReplayItem != null && mListReplayItem.size() > 0) {
                    mSpeedPlay++;
                    if (mSpeedPlay == 1) {
                        ((TextView) findViewById(R.id.txt_play_fast)).setText("2x");
                        mPlayTimeFast = 500;
                    } else if (mSpeedPlay == 2) {
                        ((TextView) findViewById(R.id.txt_play_fast)).setText("4x");
                        mPlayTimeFast = 250;
                    } else if (mSpeedPlay == 3) {
                        ((TextView) findViewById(R.id.txt_play_fast)).setText("8x");
                        mPlayTimeFast = 125;
                    } else {
                        ((TextView) findViewById(R.id.txt_play_fast)).setText("1x");
                        mSpeedPlay = 0;
                        mPlayTimeFast = 1000;
                    }
                    if (flagPlay) {
                        setTimePlayMap();
                    }
                }
            }
        });

        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mListReplayItem != null && mListReplayItem.size() > 0) {
                    index = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                }
                if (mListReplayItem != null && mListReplayItem.size() > 0) {
                    drawLineProgress(index);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        System.gc();
    }

    private void drawLineWhenPlay() {
        index++;
        if (index <= mListReplayItem.size() - 1) {
            LatLng latLng0;
            LatLng latLng = new LatLng(Double.valueOf(mListReplayItem.get(index).latitude), Double.valueOf(mListReplayItem.get(index).longitude));
            if (mListReplayItem.size() > 1) {
                latLng0 = new LatLng(Double.valueOf(mListReplayItem.get(index - 1).latitude), Double.valueOf(mListReplayItem.get(index - 1).longitude));
                if (marker != null) {
                    marker.remove();
                    if (mListReplayItem.get(index - 1).status.contains("Dừng") || mListReplayItem.get(index - 1).status.contains("Chế độ nghỉ")) {
                        marker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng0)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop)));
                    } else {
                        drawArrowHead(mGoogleMap, latLng0, latLng);
                    }
                }
                mGoogleMap.addPolyline((new PolylineOptions())
                        .add(latLng0, latLng)
                        .width(lineWidth)
                        .color(Color.BLUE)
                        .geodesic(true));
                marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_online)));
                mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowReplayAdapter(getLayoutInflater(), mListReplayItem.get(index)));
                marker.showInfoWindow();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Util.getPolygonOptions(latLng, 100, ReplayFragment.this, checkGPS())));
                mSeekBar.setProgress(index);
            }

        } else {
            stopCallTimePlayMap();
        }
    }

    private void drawLineProgress(int progress) {
        try {
            if (progress > 1 && progress < mListReplayItem.size()) {
                if (mListReplayItem.size() > 1) {
                    for (int i = 1; i < progress; i++) {
                        LatLng latLng0;
                        LatLng latLng = new LatLng(Double.valueOf(mListReplayItem.get(i).latitude), Double.valueOf(mListReplayItem.get(i).longitude));
                        latLng0 = new LatLng(Double.valueOf(mListReplayItem.get(i - 1).latitude), Double.valueOf(mListReplayItem.get(i - 1).longitude));

                        mGoogleMap.addPolyline((new PolylineOptions())
                                .add(latLng0, latLng)
                                .width(lineWidth)
                                .color(Color.BLUE)
                                .geodesic(true));
                        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowReplayAdapter(getLayoutInflater(), mListReplayItem.get(i)));

                        if (mListReplayItem.get(i).status.contains("Dừng") || mListReplayItem.get(i).status.contains("Chế độ nghỉ")) {
                            marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop)));
                        } else {
                            drawArrowHead(mGoogleMap, latLng0, latLng);
                        }
                    }
                }
            }
        }catch (Exception e) {
            android.util.Log.d("TAG", e.getMessage());
        }
    }

    private void drawLine() {
        if (mListReplayItem.size() > 1) {
            for (int i = 1; i < mListReplayItem.size(); i++) {
                LatLng latLng0;
                LatLng latLng = new LatLng(Double.valueOf(mListReplayItem.get(i).latitude), Double.valueOf(mListReplayItem.get(i).longitude));
                latLng0 = new LatLng(Double.valueOf(mListReplayItem.get(i - 1).latitude), Double.valueOf(mListReplayItem.get(i - 1).longitude));

                mGoogleMap.addPolyline((new PolylineOptions())
                        .add(latLng0, latLng)
                        .width(lineWidth)
                        .color(Color.BLUE)
                        .geodesic(true));

                if (mListReplayItem.get(i).status.contains("Dừng") || mListReplayItem.get(i).status.contains("Chế độ nghỉ")) {
                    marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop)));
                } else {
                    drawArrowHead(mGoogleMap, latLng0, latLng);
                }
            }
        }
    }

    private void drawArrowHead(GoogleMap mMap, LatLng from, LatLng to) {
        // obtain the bearing between the last two points
        double bearing = getBearing(from, to);

        // round it to a multiple of 3 and cast out 120s
        double adjBearing = Math.round(bearing / 3) * 3;
        while (adjBearing >= 120) {
            adjBearing -= 120;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
        float anchorX = 0.5f;
        float anchorY = 0.5f;

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_dir);

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(0.35f, 0.35f);
        // rotate the Bitmap
        matrix.postRotate((float) bearing);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);

        //Bitmap scaled = Bitmap.createScaledBitmap(image, sizeX, sizeY, false);
        mMap.addMarker(new MarkerOptions()
                .position(from)
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                .anchor(anchorX, anchorY));
    }

    private double getBearing(LatLng from, LatLng to) {
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = -Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;

        return angle;
    }

    private Runnable mRunnableProcess = new Runnable() {
        @Override
        public void run() {
            drawLineWhenPlay();
            mHandlerProcess.postDelayed(mRunnableProcess, mPlayTimeFast);
        }
    };

    private void setTimePlayMap() {
        mHandlerProcess.postDelayed(mRunnableProcess, mPlayTimeFast);
    }

    private void stopCallTimePlayMap() {
        if (mHandlerProcess != null) {
            mHandlerProcess.removeCallbacks(mRunnableProcess);
        }
    }

    private void getDataApi(String date) {
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                getUrlReplay(mInfoMain.id, date),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            mListReplayItem.clear();
                            index = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                                    ReplayItem item = new ReplayItem();
                                    item.devid = jsonItem.has("devid") ? jsonItem.getString("devid") : "";
                                    item.devimei = jsonItem.has("devimei") ? jsonItem.getString("devimei") : "";
                                    item.devstatus = jsonItem.has("devstatus") ? jsonItem.getString("devstatus") : "";
                                    item.devname = jsonItem.has("devname") ? jsonItem.getString("devname") : "";
                                    item.icon = jsonItem.has("icon") ? jsonItem.getString("icon") : "";
                                    item.latitude = jsonItem.has("latitude") ? jsonItem.getString("latitude") : "";
                                    item.longitude = jsonItem.has("longitude") ? jsonItem.getString("longitude") : "";
                                    item.address = jsonItem.has("address") ? jsonItem.getString("address") : "";
                                    item.direction = jsonItem.has("direction") ? jsonItem.getString("direction") : "";
                                    item.distance = jsonItem.has("distance") ? jsonItem.getDouble("distance") : 0;
                                    item.sumdistance = jsonItem.has("sumdistance") ? jsonItem.getDouble("sumdistance") : 0;
                                    item.driver = jsonItem.has("driver") ? jsonItem.getString("driver") : "";
                                    item.trktime = jsonItem.has("trktime") ? jsonItem.getString("trktime") : "";
                                    item.status = jsonItem.has("status") ? jsonItem.getString("status") : "";
                                    mListReplayItem.add(item);
                                }
                            }
                            LatLng point = new LatLng(Double.valueOf(mListReplayItem.get(0).latitude), Double.valueOf(mListReplayItem.get(0).longitude));
                            mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowReplayAdapter(getLayoutInflater(), mListReplayItem.get(mListReplayItem.size() - 1)));
                            marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(mListReplayItem.get(0).latitude), Double.valueOf(mListReplayItem.get(0).longitude)))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_online)));
                            marker.showInfoWindow();
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(Util.getPolygonOptions(point, 100, ReplayFragment.this, checkGPS()));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 10);
                            mGoogleMap.moveCamera(cameraUpdate);
                            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                            mSeekBar.setEnabled(true);
                            mSeekBar.setMax(mListReplayItem.size());
                            if (flagClear) {
                                drawLine();
                            }
                            pDialog.dismiss();
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                            new AlertDialog.Builder(ReplayFragment.this)
                                    .setMessage("Không có dữ liệu")
                                    .setCancelable(false)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.toString());
                        pDialog.dismiss();
                    }
                });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        mVolleyQueue.add(jsonObjReq);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String getUserData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("username", "");
    }

    private String getPassData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("password", "");
    }

    private String getDateCurrent() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    private String getUrlReplay(String devid, String date) {
        return Util.urlReplay + getUserData() + "&password=" + getPassData() + "&devid=" + devid + "&date=" + date;
    }

    private boolean checkGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

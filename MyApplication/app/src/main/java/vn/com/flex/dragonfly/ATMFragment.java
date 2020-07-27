package vn.com.flex.dragonfly;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.obj.InfoMain;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Nguyen on 12/19/2015.
 */
public class ATMFragment extends FragmentActivity {
    private InfoMain mInfoMain;
    private GoogleMap mGoogleMap;
    private RequestQueue mVolleyQueue;
    private ProgressDialog pDialog;
    private RelativeLayout mLayoutHeader;
    private LinearLayout mLayoutSearch;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atm_screen);
        mVolleyQueue = Volley.newRequestQueue(this);
        mLayoutHeader = (RelativeLayout) findViewById(R.id.layout_header);
        mLayoutSearch = (LinearLayout) findViewById(R.id.layout_search);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        pDialog.show();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_atm);
        mGoogleMap = fm.getMap();
        // Enable MyLocation Button in the Map
        mGoogleMap.setMyLocationEnabled(true);
        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            getPointXe();
            getDataJson("");
        } else {
            pDialog.dismiss();
        }

        searchView = (SearchView) findViewById(R.id.search_view);

        AnimationLinearLayout btnBack = (AnimationLinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AnimationLinearLayout btnSearch = (AnimationLinearLayout) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                mLayoutHeader.setVisibility(View.GONE);
                mLayoutSearch.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
            }
        });

        AnimationLinearLayout btnThoat = (AnimationLinearLayout) findViewById(R.id.btn_thoat);
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutHeader.setVisibility(View.VISIBLE);
                mLayoutSearch.setVisibility(View.GONE);
            }
        });

        AnimationLinearLayout btnTim = (AnimationLinearLayout) findViewById(R.id.btn_tim);
        btnTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInfoMain != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    searchView.clearFocus();
                    pDialog.show();
                    mGoogleMap.clear();
                    getPointXe();
                    getDataJson(searchView.getQuery().toString());
                }
            }
        });
    }

    private void getPointXe() {
        LatLng pointXe = new LatLng(Double.valueOf(mInfoMain.latitude), Double.valueOf(mInfoMain.longitude));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(pointXe)
                .icon(BitmapDescriptorFactory.fromResource(mInfoMain.getIconMakerResourceId())));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(pointXe));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    private void getDataJson(final String type) {
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                createUrl(mInfoMain.latitude, mInfoMain.longitude, type),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                                    JSONObject geometry = jsonItem.getJSONObject("geometry");
                                    JSONObject location = geometry.getJSONObject("location");
                                    double lat = location.getDouble("lat");
                                    double lng = location.getDouble("lng");
                                    mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .title(jsonItem.getString("name"))
                                            .snippet(jsonItem.getString("vicinity"))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_atm)));
                                }
                            }

                            pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(ATMFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ATMFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
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

    private String createUrl(String lat, String lng, String searchKey) {
        if (searchKey == null || searchKey.equals("")) {
            return Util.urlNearByGoogle + lat + "," + lng + "&radius=1000&name=atm";
        }
        return Util.urlNearByGoogle + lat + "," + lng + "&radius=1000&type=atm&name=" + searchKey;
    }
}

package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
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
 * Created by ndung on 12/24/15.
 */
public class PetrolFragment extends FragmentActivity {
    private InfoMain mInfoMain;
    private GoogleMap mGoogleMap;
    private RequestQueue mVolleyQueue;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.petrol_screen);
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        pDialog.show();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_petrol);
        mGoogleMap = fm.getMap();
        // Enable MyLocation Button in the Map
        mGoogleMap.setMyLocationEnabled(true);
        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            LatLng pointXe = new LatLng(Double.valueOf(mInfoMain.latitude), Double.valueOf(mInfoMain.longitude));
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(pointXe)
                    .icon(BitmapDescriptorFactory.fromResource(mInfoMain.getIconMakerResourceId())));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(pointXe));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                    Request.Method.GET,
                    createUrl(mInfoMain.latitude, mInfoMain.longitude),
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
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_petrol)));

                                    }
                                }

                                pDialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(PetrolFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(PetrolFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    });
            // Adding request to request queue
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mVolleyQueue.add(jsonObjReq);
        } else {
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
    public void onBackPressed() {
        finish();
    }

    private String createUrl(String lat, String lng) {
        return Util.urlNearByGoogle + lat + "," + lng + "&radius=1000&name=x%C4%83ng";
    }
}

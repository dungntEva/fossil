package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import vn.com.flex.dragonfly.adapter.LocationAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.obj.LocationItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nguyen on 12/19/2015.
 */
public class LocationFragment extends FragmentActivity implements AdapterView.OnItemClickListener{
    private ProgressDialog pDialog;
    private RequestQueue mVolleyQueue;
    private ListView mListLocation;
    private LocationAdapter mLocationAdapter;
    private ArrayList<LocationItem> mListDataLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_screen);
        // Initialise Volley Request Queue.
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.show();
        mListDataLocation = new ArrayList<>();
        mListLocation = (ListView) findViewById(R.id.list_location);
        mListLocation.setOnItemClickListener(this);
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("devname") != null) {
            String devName = getIntent().getExtras().getString("devname");
            TextView txtBtn = (TextView) findViewById(R.id.txt_btn);
            txtBtn.setText(devName);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                createUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                                    LocationItem item = new LocationItem();
                                    item.id = jsonItem.has("id") ? jsonItem.getString("id") : "";
                                    item.name = jsonItem.has("name") ? jsonItem.getString("name") : "";
                                    item.username = jsonItem.has("username") ? jsonItem.getString("username") : "";
                                    item.location_type_id = jsonItem.has("location_type_id") ? jsonItem.getString("location_type_id") : "";
                                    item.latitude1 = jsonItem.has("latitude1") ? jsonItem.getString("latitude1") : "";
                                    item.longitude1 = jsonItem.has("longitude1") ? jsonItem.getString("longitude1") : "";
                                    item.latitude2 = jsonItem.has("latitude2") ? jsonItem.getString("latitude2") : "";
                                    item.longitude2 = jsonItem.has("longitude2") ? jsonItem.getString("longitude2") : "";
                                    item.description = jsonItem.has("description") ? jsonItem.getString("description") : "";
                                    item.icon = jsonItem.has("icon") ? jsonItem.getString("icon") : "";
                                    item.amount = jsonItem.has("amount") ? jsonItem.getString("amount") : "";
                                    item.address = jsonItem.has("address") ? jsonItem.getString("address") : "";
                                    item.contact = jsonItem.has("contact") ? jsonItem.getString("contact") : "";
                                    item.phone = jsonItem.has("phone") ? jsonItem.getString("phone") : "";
                                    item.createddate = jsonItem.has("createddate") ? jsonItem.getString("createddate") : "";
                                    item.visible = jsonItem.has("visible") ? jsonItem.getString("visible") : "";
                                    item.rad = jsonItem.has("rad") ? jsonItem.getString("rad") : "";
                                    mListDataLocation.add(item);
                                }
                            }
                            mLocationAdapter = new LocationAdapter(mListDataLocation, LocationFragment.this);
                            mListLocation.setAdapter(mLocationAdapter);
                            pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(LocationFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LocationFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        mVolleyQueue.add(jsonObjReq);
        AnimationLinearLayout btnBack = (AnimationLinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent myIntent = new Intent(LocationFragment.this, LocationMapFragment.class);
        Bundle b = new Bundle();
        b.putSerializable("objInfoLocation", mListDataLocation.get(position));
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String createUrl(){
        return Util.urlLocation + getUserData() + "&password=" + getPassData();
    }

    private String getUserData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("username", "");
    }

    private String getPassData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("password", "");
    }
}
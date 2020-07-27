package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import vn.com.flex.dragonfly.adapter.AlarmAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.lib.MultiDirectionSlidingDrawer;
import vn.com.flex.dragonfly.obj.AlarmItem;
import vn.com.flex.dragonfly.obj.InfoMain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nguyen on 12/19/2015.
 */
public class AlarmFragment extends FragmentActivity implements AdapterView.OnItemClickListener {

    private InfoMain mInfoMain;
    private ProgressDialog pDialog;
    private RequestQueue mVolleyQueue;
    private ListView mListAlarm;
    private AlarmAdapter mAlarmAdapter;
    private MultiDirectionSlidingDrawer mDrawer;
    private ArrayList<AlarmItem> mListDataAlarm;
    private CalendarView mCalend;
    private int mAlarmType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_screen);
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.show();
        mListDataAlarm = new ArrayList<>();
        mListAlarm = (ListView) findViewById(R.id.list_alarm);
        mListAlarm.setOnItemClickListener(this);
        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
            pDialog.show();
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            getData(createUrl(mInfoMain.id, "", ""));
        }

        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);

        findViewById(R.id.txt_canh_bao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.picker).setVisibility(View.VISIBLE);
                findViewById(R.id.calendView).setVisibility(View.GONE);
                if (!mDrawer.isOpened()) {
                    mDrawer.open();
                }
            }
        });

        findViewById(R.id.txt_ngay_canh_bao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.picker).setVisibility(View.GONE);
                findViewById(R.id.calendView).setVisibility(View.VISIBLE);
                if (!mDrawer.isOpened()) {
                    mDrawer.open();
                }
            }
        });

        findViewById(R.id.btn_tim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtNgayCanhBao = ((TextView)findViewById(R.id.txt_ngay_canh_bao)).getText().toString();
                mListDataAlarm.clear();
                findViewById(R.id.layout_header).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_search).setVisibility(View.GONE);
                mDrawer.close();
                getData(createUrl(mInfoMain.id, String.valueOf(mAlarmType), txtNgayCanhBao));
            }
        });

        findViewById(R.id.btn_thoat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.layout_header).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_search).setVisibility(View.GONE);
                mDrawer.close();
            }
        });

        NumberPicker numbePicker = (NumberPicker) findViewById(R.id.picker);
        final String[] arrayString= new String[]{"Mất trộm","Mất nguồn","Vào vùng","Ra vùng"};
        numbePicker.setMinValue(0);
        numbePicker.setMaxValue(arrayString.length - 1);
        numbePicker.setDisplayedValues(arrayString);
        numbePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                ((TextView) findViewById(R.id.txt_canh_bao)).setText(arrayString[i1]);
                mAlarmType = i1 + 1;
            }
        });

        mCalend = (CalendarView) findViewById(R.id.calendView);
        mCalend.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                ((TextView) findViewById(R.id.txt_ngay_canh_bao)).setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        });

        AnimationLinearLayout btnSearch = (AnimationLinearLayout) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_header).setVisibility(View.GONE);
                findViewById(R.id.layout_search).setVisibility(View.VISIBLE);
            }
        });

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent myIntent = new Intent(AlarmFragment.this, AlarmMapFragment.class);
        Bundle b = new Bundle();
        b.putSerializable("objInfoMain", mInfoMain);
        b.putSerializable("objAlarmItem", mListDataAlarm.get(position));
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    private void getData(String url){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                                    AlarmItem item = new AlarmItem();
                                    item.id = jsonItem.has("id") ? jsonItem.getString("id") : "";
                                    item.devid = jsonItem.has("devid") ? jsonItem.getString("devid") : "";
                                    item.latitude = jsonItem.has("latitude") ? jsonItem.getString("latitude") : "";
                                    item.longitude = jsonItem.has("longitude") ? jsonItem.getString("longitude") : "";
                                    item.address = jsonItem.has("address") ? jsonItem.getString("address") : "";
                                    item.alarmtype = jsonItem.has("alarmtype") ? jsonItem.getString("alarmtype") : "";
                                    item.trktime = jsonItem.has("trktime") ? jsonItem.getString("trktime") : "";
                                    item.unread_status = jsonItem.has("unread_status") ? jsonItem.getString("unread_status") : "";
                                    item.devname = jsonItem.has("devname") ? jsonItem.getString("devname") : "";

                                    mListDataAlarm.add(item);
                                }
                            }
                            mAlarmAdapter = new AlarmAdapter(mListDataAlarm, mInfoMain.devicename);
                            mListAlarm.setAdapter(mAlarmAdapter);
                            pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(AlarmFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AlarmFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
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

    private String createUrl(String devID, String alarmType, String date) {
        return Util.urlAlarm + getUserData() + "&password=" + getPassData() + "&devid=" + devID + "&alarmtype=" + alarmType + "&date=" + date;
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

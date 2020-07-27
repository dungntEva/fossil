package vn.com.flex.dragonfly;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import vn.com.flex.dragonfly.adapter.CustomInfoWindowMainAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.lib.MultiDirectionSlidingDrawer;
import vn.com.flex.dragonfly.obj.InfoMain;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class MainFragment extends Fragment {

    private View parentView;
    private FragmentActivity mContext;
    private RequestQueue mVolleyQueue;
    private ArrayList<LatLng> mMarkerPoints;
    private GoogleMap mGoogleMap;
    private Marker marker;
    private LayoutInflater mLayoutInflater;
    private OnSendObjInfoMain mListener;
    private String mDevid = "";
    private AnimationLinearLayout mBtnCanhBao, mBtnTatMay, mBtnWarning, mBtnXemlai, mBtnMore,
            btn_timxe, btn_bando, btn_taikhoan, btn_diadiem, btn_tramatm, btn_tramxang;
    private InfoMain mInfoMain;
    private RelativeLayout mLayoutBtnBottom;
    private LinearLayout mLayoutAlertCount, mLayoutPopupOrther, mLayoutPopupBanDo;
    private TextView mTxtAlertCount;
    private MultiDirectionSlidingDrawer mDrawer;
    private Handler mHandlerProcess = new Handler();
    private int flag30s = 1, delayTime = 15000, fistTime = 0;

    public MainFragment(FragmentActivity mContext, String mDevid) {
        this.mContext = mContext;
        this.mDevid = mDevid;
        if (mHandlerProcess != null) {
            mHandlerProcess.removeCallbacks(mRunnableProcess);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mHandlerProcess != null) {
            mHandlerProcess.removeCallbacks(mRunnableProcess);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        parentView = inflater.inflate(R.layout.main_screen, container, false);
        mLayoutInflater = inflater;
        setUpViews();
        return parentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        if (mHandlerProcess != null) {
            mHandlerProcess.removeCallbacks(mRunnableProcess);
        }
        System.gc();
    }

    private void setUpViews() {
        mVolleyQueue = Volley.newRequestQueue(mContext);
        mDrawer = (MultiDirectionSlidingDrawer) parentView.findViewById(R.id.drawer);
        mLayoutBtnBottom = (RelativeLayout) parentView.findViewById(R.id.layout_btn_bottom);
        mLayoutAlertCount = (LinearLayout) parentView.findViewById(R.id.layout_alert_count);
        mLayoutPopupOrther = (LinearLayout) parentView.findViewById(R.id.layout_popup_orther);
        mLayoutPopupBanDo = (LinearLayout) parentView.findViewById(R.id.layout_popup_ban_do);
        mTxtAlertCount = (TextView) parentView.findViewById(R.id.txt_alert_count);
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext.getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, mContext, requestCode);
            dialog.show();
        } else { // Google Play Services are available
            // Initializing
            mMarkerPoints = new ArrayList<>();
            // Getting reference to SupportMapFragment of the activity_main
            FragmentManager fragment = getChildFragmentManager();
            SupportMapFragment fm = (SupportMapFragment) fragment.findFragmentById(R.id.map);
            // Getting Map for the SupportMapFragment
            mGoogleMap = fm.getMap();
            // Enable MyLocation Button in the Map
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (mLayoutPopupBanDo.getVisibility() == View.VISIBLE) {
                        mLayoutPopupOrther.setVisibility(View.VISIBLE);
                        mLayoutPopupBanDo.setVisibility(View.GONE);
                    } else {
                        mDrawer.close();
                    }
                    marker.showInfoWindow();
                    mLayoutBtnBottom.setVisibility(View.VISIBLE);
                }
            });
            getLastData();
            getDataAlertCount();
            mHandlerProcess.postDelayed(mRunnableProcess, delayTime);
        }
        mBtnCanhBao = (AnimationLinearLayout) parentView.findViewById(R.id.btn_canh_bao);
        mBtnTatMay = (AnimationLinearLayout) parentView.findViewById(R.id.btn_tat_may);
        mBtnWarning = (AnimationLinearLayout) parentView.findViewById(R.id.btn_warning);
        mBtnXemlai = (AnimationLinearLayout) parentView.findViewById(R.id.btn_xemlai);
        mBtnMore = (AnimationLinearLayout) parentView.findViewById(R.id.btn_more);

        mBtnCanhBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCanhBao();
            }
        });
        mBtnTatMay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPowerDevice();
            }
        });
        mBtnWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(mContext, AlarmFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }
        });
        mBtnXemlai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(mContext, ReplayFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }
        });
        mBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutBtnBottom.setVisibility(View.GONE);
                mDrawer.open();
            }
        });

        btn_timxe = (AnimationLinearLayout) parentView.findViewById(R.id.btn_timxe);
        btn_timxe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.close();
                mLayoutBtnBottom.setVisibility(View.VISIBLE);
                Intent myIntent = new Intent(mContext, TimXeFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);

            }
        });
        btn_bando = (AnimationLinearLayout) parentView.findViewById(R.id.btn_bando);
        btn_bando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutPopupOrther.setVisibility(View.GONE);
                mLayoutPopupBanDo.setVisibility(View.VISIBLE);
            }
        });
        btn_taikhoan = (AnimationLinearLayout) parentView.findViewById(R.id.btn_taikhoan);
        btn_taikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.close();
                mLayoutBtnBottom.setVisibility(View.VISIBLE);
                Intent myIntent = new Intent(mContext, AccountFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);

            }
        });
        btn_diadiem = (AnimationLinearLayout) parentView.findViewById(R.id.btn_diadiem);
        btn_diadiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.close();
                mLayoutBtnBottom.setVisibility(View.VISIBLE);
                Intent myIntent = new Intent(mContext, LocationFragment.class);
                myIntent.putExtra("devname", mInfoMain.devicename);
                startActivity(myIntent);
            }
        });
        btn_tramatm = (AnimationLinearLayout) parentView.findViewById(R.id.btn_tramatm);
        btn_tramatm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.close();
                mLayoutBtnBottom.setVisibility(View.VISIBLE);
                Intent myIntent = new Intent(mContext, ATMFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }
        });
        btn_tramxang = (AnimationLinearLayout) parentView.findViewById(R.id.btn_tramxang);
        btn_tramxang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.close();
                mLayoutBtnBottom.setVisibility(View.VISIBLE);
                Intent myIntent = new Intent(mContext, PetrolFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }
        });

        AnimationLinearLayout btnStandard = (AnimationLinearLayout) parentView.findViewById(R.id.btn_standard);
        btnStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        AnimationLinearLayout btnStatelite = (AnimationLinearLayout) parentView.findViewById(R.id.btn_statelite);
        btnStatelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        AnimationLinearLayout btnHybrid = (AnimationLinearLayout) parentView.findViewById(R.id.btn_hybrid);
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        AnimationLinearLayout btnThoat = (AnimationLinearLayout) parentView.findViewById(R.id.btn_thoat);
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayoutPopupOrther.setVisibility(View.VISIBLE);
                mLayoutPopupBanDo.setVisibility(View.GONE);
            }
        });
    }

    private void drawMarker(LatLng point) {
        mMarkerPoints.add(point);
    }

    private Runnable mRunnableProcess = new Runnable() {
        @Override
        public void run() {
            try {
                getLastData();
                if (flag30s == 2) {
                    flag30s = 0;
                    getDataAlertCount();
                }
                flag30s++;
                mHandlerProcess.postDelayed(mRunnableProcess, delayTime);
            } catch (Exception e) {
                Log.d("MAIN", e.getMessage());
            }
        }
    };

    private String getUserData() {
        SharedPreferences example = mContext.getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("username", "");
    }

    private String getPassData() {
        SharedPreferences example = mContext.getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("password", "");
    }

    private String getUrlLastData(String devid) {
        return Util.urlLastData + getUserData() + "&password=" + getPassData() + "&devid=" + devid;
    }

    private String getUrlAlertCount(String devid) {
        return Util.urlGetAlertCount + getUserData() + "&password=" + getPassData() + "&devid=" + devid;
    }

    private boolean checkGPS() {
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getLastData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                getUrlLastData(mDevid),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getJSONArray("data") != null) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (!jsonArray.isNull(i)) {
                                        InfoMain infoMain = new InfoMain();
                                        JSONObject jsonItem = jsonArray.getJSONObject(i);
                                        infoMain.latitude = jsonItem.has("latitude") ? jsonItem.getString("latitude") : "";
                                        infoMain.longitude = jsonItem.has("longitude") ? jsonItem.getString("longitude") : "";
                                        infoMain.devicename = jsonItem.has("devicename") ? jsonItem.getString("devicename") : "";
                                        infoMain.nextpaytime = jsonItem.has("nextpaytime") ? jsonItem.getString("nextpaytime") : "";
                                        infoMain.icon = jsonItem.has("icon") ? jsonItem.getString("icon") : "";
                                        infoMain.sim_status = jsonItem.has("sim_status") ? jsonItem.getString("sim_status") : "";
                                        infoMain.trktime = jsonItem.has("trktime") ? jsonItem.getString("trktime") : "";
                                        infoMain.driver = jsonItem.has("driver") ? jsonItem.getString("driver") : "";
                                        infoMain.driverphone = jsonItem.has("driverphone") ? jsonItem.getString("driverphone") : "";
                                        infoMain.status = jsonItem.has("status") ? jsonItem.getString("status") : "";
                                        infoMain.imei = jsonItem.has("imei") ? jsonItem.getString("imei") : "";
                                        infoMain.position = jsonItem.has("position") ? jsonItem.getString("position") : "";
                                        infoMain.devstatus = jsonItem.has("devstatus") ? jsonItem.getInt("devstatus") : 0;
                                        infoMain.id = jsonItem.has("id") ? jsonItem.getString("id") : "";
                                        infoMain.direction = jsonItem.has("direction") ? jsonItem.getString("direction") : "";
                                        infoMain.sum_distance = jsonItem.has("sum_distance") ? jsonItem.getString("sum_distance") : "";
                                        infoMain.updatetime = jsonItem.has("updatetime") ? jsonItem.getString("updatetime") : "";
                                        infoMain.lockstatus = jsonItem.has("lockstatus") ? jsonItem.getString("lockstatus") : "0";
                                        infoMain.lock_fuel = jsonItem.has("lock_fuel") ? jsonItem.getString("lock_fuel") : "0";
                                        infoMain.simno = jsonItem.has("simno") ? jsonItem.getString("simno") : "";
                                        infoMain.sleepmode = jsonItem.has("sleepmode") ? jsonItem.getString("sleepmode") : "";
                                        infoMain.statusdev = jsonItem.has("statusdev") ? jsonItem.getString("statusdev") : "";
                                        infoMain.color = jsonItem.has("color") ? jsonItem.getString("color") : "";
                                        infoMain.lat_cellID = jsonItem.has("lat_cellID") ? jsonItem.getInt("lat_cellID") : 0;
                                        infoMain.lng_cellID = jsonItem.has("lng_cellID") ? jsonItem.getInt("lng_cellID") : 0;
                                        infoMain.power_id = jsonItem.has("power_id") ? jsonItem.getInt("power_id") : 0;
                                        infoMain.power_status = jsonItem.has("power_status") ? jsonItem.getString("power_status") : "";

                                        mInfoMain = infoMain;

                                        if (mInfoMain.lockstatus.equals("1")) {
                                            mBtnCanhBao.findViewById(R.id.btn_image_canh_bao).setBackgroundResource(R.drawable.tat_canh_bao_80);
                                        } else {
                                            mBtnCanhBao.findViewById(R.id.btn_image_canh_bao).setBackgroundResource(R.drawable.bat_canh_bao_80);
                                        }

                                        if (mInfoMain.lock_fuel.equals("1")) {
                                            mBtnTatMay.findViewById(R.id.btn_image_tat_may).setBackgroundResource(R.drawable.mo_may_80);
                                        } else {
                                            mBtnTatMay.findViewById(R.id.btn_image_tat_may).setBackgroundResource(R.drawable.tat_may_80);
                                        }

                                        LatLng point = new LatLng(Double.valueOf(infoMain.latitude), Double.valueOf(infoMain.longitude));
                                        // draws the marker at the currently touched location
                                        drawMarker(point);

                                        if (marker != null) {
                                            marker.remove();
                                        }
                                        marker = mGoogleMap.addMarker(new MarkerOptions()
                                                .position(point)
                                                .icon(BitmapDescriptorFactory.fromResource(mInfoMain.getIconMakerResourceId())));
                                        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowMainAdapter(mLayoutInflater, infoMain, checkGPS()));
                                        marker.showInfoWindow();
                                        if (fistTime == 0) {
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(Util.getPolygonOptions(point, 100, mContext, checkGPS()));
                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 10);
                                            mGoogleMap.moveCamera(cameraUpdate);
                                            fistTime = 1;
                                        } else {
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Util.getPolygonOptions(point, 200, mContext, checkGPS())));
                                        }
                                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                                        if (mListener != null) {
                                            mListener.sendObjInfoMain(infoMain, mLayoutBtnBottom, mDrawer, mLayoutPopupOrther, mLayoutPopupBanDo);
                                        }
                                    }
                                }
                            } else {
                                if (response.getString("data") != null) {
                                    String data = response.has("data") ? response.getString("data") : "0";
                                    Toast.makeText(mContext, "Dữ liệu trả về " + data, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.toString());
                    }
                });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        mVolleyQueue.add(jsonObjReq);
    }

    public void setOnSendObjInfoMainListener(OnSendObjInfoMain listener) {
        this.mListener = listener;
    }

    public static interface OnSendObjInfoMain {
        void sendObjInfoMain(InfoMain infoMain, RelativeLayout mLayoutBtnBottom, MultiDirectionSlidingDrawer mDrawer, LinearLayout mLayoutPopupOrther, LinearLayout mLayoutPopupBanDo);
    }

    private void sendSMS(String simNo, String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + simNo));
        intent.putExtra("sms_body", content);
        startActivity(intent);
    }

    private void showDialogCanhBao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.bat_canh_bao_1);
        if (mInfoMain != null && mInfoMain.lockstatus.equals("1")) {
            builder.setMessage(R.string.tat_canh_bao_1);
        }
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if (mInfoMain != null && mInfoMain.isOnline() && Util.Operations.isOnline(mContext)) {
                    if (mInfoMain.lockstatus.equals("1")) {
                        requestArm(mDevid, "13");
                    } else {
                        requestArm(mDevid, "12");
                    }
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setMessage(R.string.bat_canh_bao_2);
                    if (mInfoMain != null && mInfoMain.lockstatus.equals("1")) {
                        builder2.setMessage(R.string.tat_canh_bao_2);
                    }
                    builder2.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            if (mInfoMain != null) {
                                if (mInfoMain.lockstatus.equals("1")) {
                                    sendSMS(mInfoMain.simno, "TCB");
                                } else {
                                    sendSMS(mInfoMain.simno, "BCB");
                                }
                            }
                        }
                    });
                    builder2.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder2.create().show();
                }
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.create().show();
    }

    private void requestArm(String devid, String commandType) {
        if (mDevid.isEmpty()) {
            return;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                Util.urlSendCommandGprs + getUserData() + "&password=" + getPassData() + "&devid=" + devid + "&commandtype=" + commandType,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(R.string.bat_canh_bao_3);
                                if (mInfoMain != null && mInfoMain.lockstatus.equals("1")) {
                                    builder.setMessage(R.string.tat_canh_bao_3);
                                }
                                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                    }
                                });
                                builder.create().show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(R.string.bat_canh_bao_4);
                                if (mInfoMain != null && mInfoMain.lockstatus.equals("1")) {
                                    builder.setMessage(R.string.tat_canh_bao_4);
                                }
                                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                        if (mInfoMain != null) {
                                            if (mInfoMain.lockstatus.equals("1")) {
                                                sendSMS(mInfoMain.simno, "TCB");
                                            } else {
                                                sendSMS(mInfoMain.simno, "BCB");
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                                builder.create().show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(mContext, R.string.du_lieu_khong_hop_le, Toast.LENGTH_SHORT).show();
                            Log.e("TAG", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, R.string.gui_that_bai, Toast.LENGTH_SHORT).show();
                    }
                });
        // Adding request to request queue
        mVolleyQueue.add(jsonObjReq);
    }

    private void showDialogPowerDevice() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.tat_may_xe_1);
        if (mInfoMain != null && mInfoMain.lock_fuel.equals("1")) {
            builder.setMessage(R.string.mo_may_xe_1);
        }
        final EditText input = new EditText(mContext);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        builder.setView(input);

        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                String passwordInput = input.getText().toString();
                if (!getPassData().equals(Util.md5(passwordInput))) {
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                    builder3.setMessage(R.string.mat_khau_may_xe);
                    builder3.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    builder3.create().show();
                    return;
                }

                if (mInfoMain != null && mInfoMain.isOnline() && Util.Operations.isOnline(mContext)) {
                    if (mInfoMain.lock_fuel.equals("1")) {
                        requestPower(mDevid, "1");
                    } else {
                        requestPower(mDevid, "0");
                    }
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setTitle(R.string.tat_may_xe_2);
                    if (mInfoMain != null && mInfoMain.lock_fuel.equals("1")) {
                        builder2.setTitle(R.string.mo_may_xe_2);
                    }
                    builder2.setItems(new CharSequence[]
                                    {"Call", "SMS", "Cancel"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            callSimNo();
                                            break;
                                        case 1:
                                            if (mInfoMain != null) {
                                                if (mInfoMain.lock_fuel.equals("1")) {
                                                    sendSMS(mInfoMain.simno, "MM");
                                                } else {
                                                    sendSMS(mInfoMain.simno, "TM");
                                                }
                                            }
                                            break;
                                    }
                                }
                            });
                    builder2.create().show();
                }
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.create().show();
    }

    private void requestPower(String devid, String commandParam) {
        if (mDevid.isEmpty()) {
            return;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                Util.urlSendCommandGprs + getUserData() + "&password=" + getPassData() + "&devid=" + devid + "&commandtype=15&param=" + commandParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status == true) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(response.getString("message"));
                                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User clicked OK button
                                    }
                                });
                                builder.create().show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(R.string.tat_may_xe_3);
                                if (mInfoMain != null && mInfoMain.lock_fuel.equals("1")) {
                                    builder.setTitle(R.string.mo_may_xe_3);
                                }
                                builder.setItems(new CharSequence[]
                                                {"Call", "SMS", "Cancel"},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        callSimNo();
                                                        break;
                                                    case 1:
                                                        if (mInfoMain != null) {
                                                            if (mInfoMain.lock_fuel.equals("1")) {
                                                                sendSMS(mInfoMain.simno, "MM");
                                                            } else {
                                                                sendSMS(mInfoMain.simno, "TM");
                                                            }
                                                        }
                                                        break;
                                                }
                                            }
                                        });
                                builder.create().show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(mContext, R.string.du_lieu_khong_hop_le, Toast.LENGTH_SHORT).show();
                            Log.e("TAG", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, R.string.gui_that_bai, Toast.LENGTH_SHORT).show();
                    }
                });
        // Adding request to request queue
        mVolleyQueue.add(jsonObjReq);
    }

    private void callSimNo() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mInfoMain.simno));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void getDataAlertCount() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                getUrlAlertCount(mDevid),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("data") != null) {
                                String alertCount = response.has("data") ? response.getString("data") : "";
                                if (!alertCount.equals("") && !alertCount.equals("0")) {
                                    mLayoutAlertCount.setVisibility(View.VISIBLE);
                                    mTxtAlertCount.setText(alertCount);
                                } else {
                                    mLayoutAlertCount.setVisibility(View.GONE);
                                }
                            } else {
                                mLayoutAlertCount.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.toString());
                    }
                });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        mVolleyQueue.add(jsonObjReq);
    }
}

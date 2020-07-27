package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

import org.json.JSONObject;

/**
 * Created by Nguyen on 12/27/2015.
 */
public class ThongTinFragment extends FragmentActivity {

    private InfoMain mInfoMain;
    private RequestQueue mVolleyQueue;
    private ProgressDialog pDialog;
    private EditText edit_name, edit_full_name, edit_address, edit_phone, edit_mobiphone, edit_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongtin_screen);
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_full_name = (EditText) findViewById(R.id.edit_full_name);
        edit_address = (EditText) findViewById(R.id.edit_address);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_mobiphone = (EditText) findViewById(R.id.edit_mobiphone);
        edit_email = (EditText) findViewById(R.id.edit_email);
        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            ((TextView) findViewById(R.id.txt_btn)).setText(mInfoMain.devicename);
            pDialog.show();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                    Request.Method.GET,
                    getUrl(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonLogin = response.getJSONObject("data");
                                String userName = jsonLogin.has("username") ? jsonLogin.getString("username") : "";
                                String full_name = jsonLogin.has("full_name") ? jsonLogin.getString("full_name") : "";
                                String address = jsonLogin.has("address") ? jsonLogin.getString("address") : "";
                                String phone = jsonLogin.has("phone") ? jsonLogin.getString("phone") : "";
                                String mobi_phone = jsonLogin.has("mobi_phone") ? jsonLogin.getString("mobi_phone") : "";
                                String email = jsonLogin.has("email") ? jsonLogin.getString("email") : "";
                                edit_name.setText(userName);
                                edit_full_name.setText(full_name);
                                edit_address.setText(address);
                                edit_phone.setText(phone);
                                edit_mobiphone.setText(mobi_phone);
                                edit_email.setText(email);
                                pDialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(ThongTinFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Log.e("TAG", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ThongTinFragment.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
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

        AnimationLinearLayout btnDangNhap = (AnimationLinearLayout) findViewById(R.id.btn_dang_nhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.show();
                updateThongTin();
            }
        });

        AnimationLinearLayout btnSearch = (AnimationLinearLayout) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ThongTinFragment.this, ThongTinPassFragment.class);
                startActivity(myIntent);
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

    private String getUrl() {
        return Util.urlLogin + getUserData() + "&password=" + getPassData() + "&udid=" + getId();
    }

    private String getId() {
        String id = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return "abc123";
    }

    private String getUserData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("username", "");
    }

    private String getPassData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("password", "");
    }

    private String urlChangeThongTin() {
        return Util.urlChangeThongTin + edit_name.getText().toString() + "&password=" + getPassData() + "&email=" + edit_email.getText().toString() + "&address=" + edit_address.getText().toString()
                + "&phone=" + edit_phone.getText().toString() + "&mobi_phone=" + edit_mobiphone.getText().toString() + "&full_name=" + edit_full_name.getText().toString();
    }

    private void updateThongTin() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                urlChangeThongTin(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String meassge = response.has("message") ? response.getString("message") : "";
                            JSONObject jsonLogin = response.getJSONObject("data");
                            String userName = jsonLogin.has("username") ? jsonLogin.getString("username") : "";
                            String full_name = jsonLogin.has("full_name") ? jsonLogin.getString("full_name") : "";
                            String address = jsonLogin.has("address") ? jsonLogin.getString("address") : "";
                            String phone = jsonLogin.has("phone") ? jsonLogin.getString("phone") : "";
                            String mobi_phone = jsonLogin.has("mobi_phone") ? jsonLogin.getString("mobi_phone") : "";
                            String email = jsonLogin.has("email") ? jsonLogin.getString("email") : "";
                            edit_name.setText(userName);
                            edit_full_name.setText(full_name);
                            edit_address.setText(address);
                            edit_phone.setText(phone);
                            edit_mobiphone.setText(mobi_phone);
                            edit_email.setText(email);
                            Toast.makeText(ThongTinFragment.this, meassge, Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(ThongTinFragment.this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            Log.e("TAG", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ThongTinFragment.this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
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
}

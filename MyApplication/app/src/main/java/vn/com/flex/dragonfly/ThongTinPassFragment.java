package vn.com.flex.dragonfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
public class ThongTinPassFragment extends FragmentActivity {

    private InfoMain mInfoMain;
    private ProgressDialog pDialog;
    private RequestQueue mVolleyQueue;
    private EditText mEditPassHienTai, mEditPassMoi, mEditNhapLaiPassMoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongtin_pass);
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        mEditPassHienTai = (EditText) findViewById(R.id.edit_pass_hien_tai);
        mEditPassMoi = (EditText) findViewById(R.id.edit_pass_moi);
        mEditNhapLaiPassMoi = (EditText) findViewById(R.id.edit_nhap_lai_pass_moi);

        AnimationLinearLayout btnDangNhap = (AnimationLinearLayout) findViewById(R.id.btn_dang_nhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (compareString(md5(mEditPassHienTai.getText().toString()), getPassData()) &&
                        compareString(mEditPassMoi.getText().toString(), mEditNhapLaiPassMoi.getText().toString())){
                    pDialog.show();
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                            Request.Method.GET,
                            urlChangePass(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String message = response.has("message") ? response.getString("message") : "";
                                        if (message.contains("mật khẩu thành công")){
                                            saveUserPass(md5(mEditPassMoi.getText().toString()));
                                            mEditPassHienTai.setText("");
                                            mEditPassMoi.setText("");
                                            mEditNhapLaiPassMoi.setText("");
                                            Toast.makeText(ThongTinPassFragment.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ThongTinPassFragment.this, "Thay đổi mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                        pDialog.dismiss();
                                    } catch (Exception e) {
                                        pDialog.dismiss();
                                        Toast.makeText(ThongTinPassFragment.this, "Thay đổi mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                        Log.e("TAG", e.toString());
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pDialog.dismiss();
                                    Toast.makeText(ThongTinPassFragment.this, "Thay đổi mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    // Adding request to request queue
                    mVolleyQueue.add(jsonObjReq);
                }else{
                    Toast.makeText(ThongTinPassFragment.this, "Mật khẩu cũ sai hoặc mật khẩu mới nhập lại không đúng", Toast.LENGTH_SHORT).show();
                }
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

    private String getUserData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("username", "");
    }

    private String getPassData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("password", "");
    }

    private boolean compareString(String str1, String str2) {
        if (str1.equals(str2)) {
            return true;
        } else {
            return false;
        }
    }

    private String md5(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String urlChangePass() {
        return Util.urlChangePass + getUserData() + "&password=" + getPassData() + "&oldpass=" + getPassData() + "&newpass=" + md5(mEditPassMoi.getText().toString());
    }

    private void saveUserPass(String pass) {
        SharedPreferences preferences = getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        preferences.edit().remove("password").commit();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", md5(pass));
        editor.commit();
    }
}

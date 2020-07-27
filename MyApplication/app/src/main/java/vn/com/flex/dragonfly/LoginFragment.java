package vn.com.flex.dragonfly;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

import org.json.JSONObject;

/**
 * Created by Nguyen on 12/19/2015.
 */
public class LoginFragment extends FragmentActivity implements View.OnClickListener {

    private static String TAG = LoginFragment.class.getSimpleName();
    // Progress dialog
    private ProgressDialog pDialog;
    private EditText editTxtUser, editTxtPass;
    private RequestQueue mVolleyQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        // Initialise Volley Request Queue.
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        editTxtUser = (EditText) findViewById(R.id.edit_txt_user);
        editTxtPass = (EditText) findViewById(R.id.edit_txt_pass);
        findViewById(R.id.btn_dang_nhap).setOnClickListener(this);
        findViewById(R.id.btn_ho_tro).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void saveUserPass(String user, String pass) {
        SharedPreferences preferences = getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", user);
        editor.putString("password", Util.md5(pass));
        editor.commit();
    }

    private String getId() {
        String id = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return "abc123";
    }

    private String createUrlLogin() {
        if (editTxtUser.getText().toString() != null && editTxtPass.getText().toString() != null) {
            return Util.urlLogin + editTxtUser.getText().toString() + "&password="
                    + Util.md5(editTxtPass.getText().toString()) + "&udid=" + getId();
        } else {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dang_nhap:
                pDialog.show();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.GET,
                        createUrlLogin(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject jsonLogin = response.getJSONObject("data");
                                    String userName = jsonLogin.has("username") ? jsonLogin.getString("username") : "";
                                    if (userName != null && !userName.equals("")) {
                                        saveUserPass(editTxtUser.getText().toString(), editTxtPass.getText().toString());
                                        pDialog.dismiss();
                                        Intent myIntent = new Intent(LoginFragment.this, MapsActivity.class);
                                        startActivity(myIntent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(LoginFragment.this, "Đăng nhập sai", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                    Log.e("TAG", e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginFragment.this, "Đăng nhập sai", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        });
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // Adding request to request queue
                mVolleyQueue.add(jsonObjReq);
                break;
            case R.id.btn_ho_tro:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "190060601"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
                break;
        }
    }
}

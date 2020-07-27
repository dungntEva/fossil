package vn.com.flex.dragonfly;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import vn.com.flex.dragonfly.adapter.LeftMenuAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.lib.MultiDirectionSlidingDrawer;
import vn.com.flex.dragonfly.obj.InfoMain;
import vn.com.flex.dragonfly.obj.ItemLeftMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener, MainFragment.OnSendObjInfoMain {
    private MainFragment mMainFragment;
    private DrawerLayout mDrawerlayout;
    private LinearLayout mLayoutRight;
    private AnimationLinearLayout mLayoutHdsd, mLayoutHoTro, mLayoutGioiThieu, mLayoutThoat,
            btnLeftMenu, btnRightMenu;
    private ListView mListViewLeft;
    private TextView mTxtTitle;
    private ProgressDialog pDialog;
    private RequestQueue mVolleyQueue;
    private ArrayList<ItemLeftMenu> mListItemLeftMenu;
    private LeftMenuAdapter leftMenuAdapter;
    private String mDevid = "";
    private InfoMain mInfoMain;
    private RelativeLayout mLayoutBtnBottom;
    private LinearLayout mLayoutPopupOrther, mLayoutPopupBanDo;
    private MultiDirectionSlidingDrawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMenu();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mDevid = mListItemLeftMenu.get(i - 1).devid;
        mDrawerlayout.closeDrawer(mLayoutRight);
        mDrawerlayout.closeDrawer(mListViewLeft);
        mTxtTitle.setText(mListItemLeftMenu.get(i - 1).devname);
        mMainFragment = new MainFragment(this, mDevid);
        mMainFragment.setOnSendObjInfoMainListener(this);
        changeFragment(mMainFragment);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawer != null && mLayoutBtnBottom != null) {
            if (mDrawer.isOpened()) {
                if (mLayoutPopupBanDo != null && mLayoutPopupOrther != null && mLayoutPopupBanDo.getVisibility() == View.VISIBLE) {
                    mLayoutPopupBanDo.setVisibility(View.GONE);
                    mLayoutPopupOrther.setVisibility(View.VISIBLE);
                }else {
                    this.mDrawer.close();
                    this.mLayoutBtnBottom.setVisibility(View.VISIBLE);
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void sendObjInfoMain(InfoMain infoMain, RelativeLayout mLayoutBtnBottom, MultiDirectionSlidingDrawer mDrawer, LinearLayout mLayoutPopupOrther, LinearLayout mLayoutPopupBanDo) {
        this.mInfoMain = infoMain;
        this.mLayoutBtnBottom = mLayoutBtnBottom;
        this.mDrawer = mDrawer;
        this.mLayoutPopupOrther = mLayoutPopupOrther;
        this.mLayoutPopupBanDo = mLayoutPopupBanDo;
    }

    private void setUpMenu() {
        mVolleyQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Đang tải...");
        mListItemLeftMenu = new ArrayList<>();
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mListViewLeft = (ListView) findViewById(R.id.left_menu);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.left_menu_header, mListViewLeft, false);
        AnimationLinearLayout btnAcc = (AnimationLinearLayout) header.findViewById(R.id.btn_acc);
        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MapsActivity.this, ThongTinFragment.class);
                Bundle b = new Bundle();
                b.putSerializable("objInfoMain", mInfoMain);
                myIntent.putExtras(b);
                startActivity(myIntent);
            }
        });
        mListViewLeft.addHeaderView(header);
        mListViewLeft.setOnItemClickListener(this);
        TextView txt_name_user = (TextView) header.findViewById(R.id.txt_name_user);
        if (getUserData() != null) {
            txt_name_user.setText(getUserData());
        }

        mLayoutRight = (LinearLayout) findViewById(R.id.right_menu);
        btnLeftMenu = (AnimationLinearLayout) findViewById(R.id.btn_left_menu);
        btnRightMenu = (AnimationLinearLayout) findViewById(R.id.btn_right_menu);

        btnLeftMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mDrawerlayout.isDrawerOpen(mLayoutRight)) {
                    mDrawerlayout.closeDrawer(mLayoutRight);
                }
                if (mDrawerlayout.isDrawerOpen(mListViewLeft)) {
                    mDrawerlayout.closeDrawer(mListViewLeft);
                } else {
                    mDrawerlayout.openDrawer(mListViewLeft);
                }
            }
        });


        btnRightMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mDrawerlayout.isDrawerOpen(mListViewLeft)) {
                    mDrawerlayout.closeDrawer(mListViewLeft);
                }
                if (mDrawerlayout.isDrawerOpen(mLayoutRight)) {
                    mDrawerlayout.closeDrawer(mLayoutRight);
                } else {
                    mDrawerlayout.openDrawer(mLayoutRight);
                }
            }
        });

        pDialog.show();

        mLayoutHdsd = (AnimationLinearLayout) findViewById(R.id.layout_hdsd);
        mLayoutHoTro = (AnimationLinearLayout) findViewById(R.id.layout_ho_tro);
        mLayoutGioiThieu = (AnimationLinearLayout) findViewById(R.id.layout_gioi_thieu);
        mLayoutThoat = (AnimationLinearLayout) findViewById(R.id.layout_thoat);

        mLayoutHdsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MapsActivity.this, WebFragment.class);
                myIntent.putExtra("url", Util.urlHDSD);
                myIntent.putExtra("title", "Hướng dẫn sử dụng");
                startActivity(myIntent);
            }
        });
        mLayoutGioiThieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MapsActivity.this, WebFragment.class);
                myIntent.putExtra("url", Util.urlGioithieu);
                myIntent.putExtra("title", "Giới thiệu");
                startActivity(myIntent);
            }
        });
        mLayoutHoTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MapsActivity.this, WebFragment.class);
                myIntent.putExtra("url", Util.urlHotro);
                myIntent.putExtra("title", "Thông tin hỗ trợ");
                startActivity(myIntent);
            }
        });
        mLayoutThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPreferences();
                System.gc();
                Intent myIntent = new Intent(MapsActivity.this, LoginFragment.class);
                startActivity(myIntent);
                finish();
            }
        });

        mTxtTitle = (TextView) findViewById(R.id.txt_title);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                createUrlLeftMenu(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                                    ItemLeftMenu item = new ItemLeftMenu();
                                    item.devid = jsonItem.has("devid") ? jsonItem.getString("devid") : "";
                                    item.devimei = jsonItem.has("devimei") ? jsonItem.getString("devimei") : "";
                                    item.devname = jsonItem.has("devname") ? jsonItem.getString("devname") : "";
                                    item.icon = jsonItem.has("icon") ? jsonItem.getString("icon") : "";
                                    item.trktime = jsonItem.has("trktime") ? jsonItem.getString("trktime") : "";
                                    item.alertcount = jsonItem.has("alertcount") ? jsonItem.getString("alertcount") : "";
                                    item.devstatus = jsonItem.has("devstatus") ? jsonItem.getString("devstatus") : "";
                                    item.simno = jsonItem.has("simno") ? jsonItem.getString("simno") : "";
                                    mListItemLeftMenu.add(item);
                                }
                            }
                            if (mListItemLeftMenu.get(0).devid != null) {
                                mDevid = mListItemLeftMenu.get(0).devid;
                            }
                            leftMenuAdapter = new LeftMenuAdapter(mListItemLeftMenu, MapsActivity.this);
                            mListViewLeft.setAdapter(leftMenuAdapter);
                            mMainFragment = new MainFragment(MapsActivity.this, mDevid);
                            mMainFragment.setOnSendObjInfoMainListener(MapsActivity.this);
                            changeFragment(mMainFragment);
                            mTxtTitle.setText(mListItemLeftMenu.get(0).devname);
                            pDialog.dismiss();
                        } catch (Exception e) {
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void changeFragment(android.support.v4.app.Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private String getUserData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("username", "");
    }

    private String getPassData() {
        SharedPreferences example = getSharedPreferences(MapsActivity.class.getSimpleName(), 0);
        return example.getString("password", "");
    }

    private String createUrlLeftMenu() {
        return Util.urlLeftmenu + getUserData() + "&password=" + getPassData();
    }

    private void clearSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}

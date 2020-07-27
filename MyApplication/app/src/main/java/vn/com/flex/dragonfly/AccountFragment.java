package vn.com.flex.dragonfly;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.obj.InfoMain;

/**
 * Created by Nguyen on 12/19/2015.
 */
public class AccountFragment extends FragmentActivity {

    private InfoMain mInfoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_screen);

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoMain") != null) {
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            TextView txt_btn = (TextView) findViewById(R.id.txt_btn);
            txt_btn.setText(mInfoMain.devicename);
            TextView txt_tk_sim = (TextView) findViewById(R.id.txt_tk_sim);
            txt_tk_sim.setText(mInfoMain.sim_status);
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
}

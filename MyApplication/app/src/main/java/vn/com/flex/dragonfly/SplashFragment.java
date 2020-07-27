package vn.com.flex.dragonfly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;

/**
 * Created by ndung on 1/5/16.
 */
public class SplashFragment extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (getUserData() != null && !getUserData().equals("")){
                    Intent myIntent = new Intent(SplashFragment.this, MapsActivity.class);
                    startActivity(myIntent);
                    finish();
                }else{
                    Intent myIntent = new Intent(SplashFragment.this, LoginFragment.class);
                    startActivity(myIntent);
                    finish();
                }
            }
        }.start();
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

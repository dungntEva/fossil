package vn.com.flex.dragonfly;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import vn.com.flex.dragonfly.adapter.CustomInfoWindowAlarmAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.obj.AlarmItem;
import vn.com.flex.dragonfly.obj.InfoMain;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Nguyen on 12/27/2015.
 */
public class AlarmMapFragment extends FragmentActivity {

    private AlarmItem mAlarmItem;
    private GoogleMap mGoogleMap;
    private InfoMain mInfoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_map_screen);
        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null && savedInstanceState.getSerializable("objAlarmItem") != null
                && savedInstanceState.getSerializable("objInfoMain") != null) {
            mAlarmItem = (AlarmItem) savedInstanceState.getSerializable("objAlarmItem");
            mInfoMain = (InfoMain) savedInstanceState.getSerializable("objInfoMain");
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_alarm);
            mGoogleMap = fm.getMap();
            mGoogleMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(Double.valueOf(mAlarmItem.latitude), Double.valueOf(mAlarmItem.longitude));

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(mInfoMain.getIconMakerResourceId())));
            mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAlarmAdapter(getLayoutInflater(), mInfoMain, mAlarmItem, checkGPS()));
            marker.showInfoWindow();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Util.getPolygonOptions(latLng, 100, this, checkGPS())));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
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

    private boolean checkGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

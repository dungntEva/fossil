package vn.com.flex.dragonfly;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import vn.com.flex.dragonfly.adapter.CustomInfoWindowLocationAdapter;
import vn.com.flex.dragonfly.lib.AnimationLinearLayout;
import vn.com.flex.dragonfly.obj.LocationItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Nguyen on 12/24/2015.
 */
public class LocationMapFragment extends FragmentActivity {

    private LocationItem mLocationItem;
    private GoogleMap mGoogleMap;
    private ArrayList<LatLng> mMarkerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map_screen);
        savedInstanceState = getIntent().getExtras();

            if (savedInstanceState != null && savedInstanceState.getSerializable("objInfoLocation") != null) {
                mLocationItem = (LocationItem) savedInstanceState.getSerializable("objInfoLocation");
                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_location);
                mGoogleMap = fm.getMap();
                mGoogleMap.setMyLocationEnabled(true);
                LatLng latLng1 = new LatLng(Double.valueOf(mLocationItem.latitude1), Double.valueOf(mLocationItem.longitude1));
                LatLng latLng2 = new LatLng(Double.valueOf(mLocationItem.latitude2), Double.valueOf(mLocationItem.longitude1));
                LatLng latLng3 = new LatLng(Double.valueOf(mLocationItem.latitude2), Double.valueOf(mLocationItem.longitude2));
                LatLng latLng4 = new LatLng(Double.valueOf(mLocationItem.latitude1), Double.valueOf(mLocationItem.longitude2));
                mMarkerPoints = new ArrayList<>();

                mMarkerPoints.add(latLng1);
                mMarkerPoints.add(latLng2);
                mMarkerPoints.add(latLng3);
                mMarkerPoints.add(latLng4);

                //getPolygonOptions(latLng1, latLng2, 100);

                int imgIcon = R.drawable.icon_online;
                if (mLocationItem.visible.equals("0")) {
                    imgIcon = R.drawable.icon_offline;
                }
                mGoogleMap.addPolyline((new PolylineOptions())
                        .add(mMarkerPoints.get(0), mMarkerPoints.get(1), mMarkerPoints.get(2), mMarkerPoints.get(3), mMarkerPoints.get(0))
                        .width(5)
                        .color(Color.BLUE)
                        .geodesic(true));

                Double centerLatitude = (Double.valueOf(mLocationItem.latitude1) + Double.valueOf(mLocationItem.latitude2))/2;
                Double centerLongitude = (Double.valueOf(mLocationItem.longitude1) + Double.valueOf(mLocationItem.longitude2))/2;
                LatLng centerLng = new LatLng(centerLatitude, centerLongitude);

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(centerLng)
                        .icon(BitmapDescriptorFactory.fromResource(imgIcon)));
                mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowLocationAdapter(getLayoutInflater(), mLocationItem));
                marker.showInfoWindow();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(centerLng));
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
}

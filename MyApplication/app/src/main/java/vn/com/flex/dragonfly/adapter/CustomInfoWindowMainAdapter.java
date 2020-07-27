package vn.com.flex.dragonfly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.InfoMain;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by ndung on 12/22/15.
 */
public class CustomInfoWindowMainAdapter implements GoogleMap.InfoWindowAdapter {

    private final View v;
    private boolean flagGps;
    private InfoMain mInfoMain;

    public CustomInfoWindowMainAdapter(LayoutInflater layout, InfoMain mInfoMain, boolean flagGps) {
        v = layout.inflate(vn.com.flex.dragonfly.R.layout.maker_info_main_layout, null);
        this.mInfoMain = mInfoMain;
        this.flagGps = flagGps;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txt_day_hour = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_day_hour);
        TextView txt_house = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_house);
        TextView txt_driver = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_driver);
        TextView txt_state = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_state);
        TextView txt_km = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_km);
        if (mInfoMain.trktime != null) {
            txt_day_hour.setText(mInfoMain.trktime);
        }
        if (mInfoMain.position != null) {
            txt_house.setText(mInfoMain.position);
        }
        if (mInfoMain.driver != null && mInfoMain.driverphone != null) {
            txt_driver.setText("Tài xế: " + mInfoMain.driver + "," + mInfoMain.driverphone);
        }
        if (mInfoMain.status != null) {
            txt_state.setText("Trạng thái: " + mInfoMain.status);
        }
        if (mInfoMain.sum_distance != null) {
            txt_km.setText("Km trong ngày: " + mInfoMain.sum_distance + " km");
        }

        ImageView img_gps = (ImageView) v.findViewById(vn.com.flex.dragonfly.R.id.img_gps);
        if(mInfoMain.devstatus != 2){
            img_gps.setImageResource(vn.com.flex.dragonfly.R.drawable.co_gps);
        }else {
            img_gps.setImageResource(vn.com.flex.dragonfly.R.drawable.mat_gps);
        }

        ImageView img_pin = (ImageView) v.findViewById(vn.com.flex.dragonfly.R.id.img_pin);
        if (mInfoMain.power_id == 1){
            img_pin.setImageResource(vn.com.flex.dragonfly.R.drawable.mat_nguon);
        }else if (mInfoMain.power_id == 2) {
            img_pin.setImageResource(vn.com.flex.dragonfly.R.drawable.pin_yeu_1);
        }else if (mInfoMain.power_id == 3) {
            img_pin.setImageResource(vn.com.flex.dragonfly.R.drawable.pin_yeu_2);
        }else if (mInfoMain.power_id == 4) {
            img_pin.setImageResource(vn.com.flex.dragonfly.R.drawable.pin_binh_thuong);
        }

        return v;
    }
}

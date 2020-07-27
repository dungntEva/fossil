package vn.com.flex.dragonfly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.AlarmItem;
import vn.com.flex.dragonfly.obj.InfoMain;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Nguyen on 12/27/2015.
 */
public class CustomInfoWindowAlarmAdapter implements GoogleMap.InfoWindowAdapter {

    private final View v;
    private boolean flagGps;
    private AlarmItem mAlarmItem;
    private InfoMain mInfoMain;

    public CustomInfoWindowAlarmAdapter(LayoutInflater layout, InfoMain mInfoMain, AlarmItem mAlarmItem, boolean flagGps) {
        v = layout.inflate(vn.com.flex.dragonfly.R.layout.maker_info_alarm_layout, null);
        this.mInfoMain = mInfoMain;
        this.mAlarmItem = mAlarmItem;
        this.flagGps = flagGps;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txt_day_hour = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_day_hour);
        if (mAlarmItem.trktime != null) {
            txt_day_hour.setText(mAlarmItem.trktime);
        }
        TextView txt_address = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_address);
        if (mAlarmItem.address != null) {
            txt_address.setText(mAlarmItem.address);
        }
        TextView txt_trang_thai = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_trang_thai);
//        if (mAlarmItem.alarmtype != null) {
//            txt_trang_thai.setText(mAlarmItem.alarmtype);
//        }
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

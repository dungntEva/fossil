package vn.com.flex.dragonfly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.ReplayItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by ndung on 12/25/15.
 */
public class CustomInfoWindowReplayAdapter implements GoogleMap.InfoWindowAdapter {

    private final View v;
    private ReplayItem mReplay;

    public CustomInfoWindowReplayAdapter(LayoutInflater layout, ReplayItem mReplay) {
        v = layout.inflate(vn.com.flex.dragonfly.R.layout.maker_info_replay_layout, null);
        this.mReplay = mReplay;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txt_day_hour = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_day_hour);
        TextView txt_street = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_street);
        TextView txt_speed = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_speed);
        TextView txt_sum = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_sum);
        if (mReplay.trktime != null) {
            txt_day_hour.setText(mReplay.trktime);
        }
        if (mReplay.address != null) {
            txt_street.setText(mReplay.address);
        }
        if (mReplay.status != null) {
            txt_speed.setText("Vận tốc: " + mReplay.status);
        }
        txt_sum.setText("Tổng: " + mReplay.sumdistance);

        return v;
    }
}

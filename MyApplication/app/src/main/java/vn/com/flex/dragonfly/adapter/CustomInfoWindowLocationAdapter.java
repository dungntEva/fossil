package vn.com.flex.dragonfly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.LocationItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Nguyen on 12/24/2015.
 */
public class CustomInfoWindowLocationAdapter implements GoogleMap.InfoWindowAdapter {

    private final View v;
    private LocationItem mInfoLocation;

    public CustomInfoWindowLocationAdapter(LayoutInflater layout, LocationItem mInfoLocation) {
        v = layout.inflate(vn.com.flex.dragonfly.R.layout.maker_info_location_layout, null);
        this.mInfoLocation = mInfoLocation;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txt_name = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_name);
        TextView txt_create_date = (TextView) v.findViewById(vn.com.flex.dragonfly.R.id.txt_create_date);
        if (mInfoLocation.name != null) {
            txt_name.setText(mInfoLocation.name);
        }
        if (mInfoLocation.createddate != null) {
            txt_create_date.setText(mInfoLocation.createddate);
        }
        return v;
    }
}

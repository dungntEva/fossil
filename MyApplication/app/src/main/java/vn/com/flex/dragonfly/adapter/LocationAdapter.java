package vn.com.flex.dragonfly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.LocationItem;

import java.util.ArrayList;

/**
 * Created by ndung on 12/24/15.
 */
public class LocationAdapter extends BaseAdapter {

    private ArrayList<LocationItem> listLocation;
    private Context mContext;

    public LocationAdapter(ArrayList<LocationItem> listLocation, Context context){
        this.listLocation = listLocation;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listLocation.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(vn.com.flex.dragonfly.R.layout.location_item, null);
        }
        String name = listLocation.get(position).name;
        TextView txt_item = (TextView) convertView.findViewById(vn.com.flex.dragonfly.R.id.txt_name);
        txt_item.setText(name);
        return convertView;
    }
}

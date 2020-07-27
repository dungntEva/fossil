package vn.com.flex.dragonfly.adapter;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.AlarmItem;

import java.util.ArrayList;

/**
 * Created by Nguyen on 12/26/2015.
 */
public class AlarmAdapter extends BaseAdapter {

    private ArrayList<AlarmItem> mListAlarm;
    private String mDeviceName;

    public AlarmAdapter(ArrayList<AlarmItem> mListAlarm, String mDeviceName){
        this.mListAlarm = mListAlarm;
        this.mDeviceName = mDeviceName;
    }

    @Override
    public int getCount() {
        return mListAlarm.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(vn.com.flex.dragonfly.R.layout.alarm_item_layout, null);
        }
        TextView txt_name = (TextView) convertView.findViewById(vn.com.flex.dragonfly.R.id.txt_name);
        txt_name.setText(mDeviceName);
        TextView txt_address = (TextView) convertView.findViewById(vn.com.flex.dragonfly.R.id.txt_address);
        txt_address.setText(mListAlarm.get(position).address);
        if (mListAlarm.get(position).unread_status.equals("1")){
            txt_name.setTextColor(ContextCompat.getColor(parent.getContext(), vn.com.flex.dragonfly.R.color.colorAlarmUnread));
            txt_name.setTypeface(null, Typeface.BOLD);
            txt_address.setTextColor(ContextCompat.getColor(parent.getContext(), vn.com.flex.dragonfly.R.color.colorAlarmUnread));
            txt_address.setTypeface(null, Typeface.BOLD);
        }
        TextView txt_date = (TextView) convertView.findViewById(vn.com.flex.dragonfly.R.id.txt_date);
        txt_date.setText(mListAlarm.get(position).trktime);
        return convertView;
    }
}

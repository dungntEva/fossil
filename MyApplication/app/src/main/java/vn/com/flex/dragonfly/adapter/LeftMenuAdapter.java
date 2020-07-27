package vn.com.flex.dragonfly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.com.flex.dragonfly.obj.ItemLeftMenu;

import java.util.ArrayList;

/**
 * Created by Nguyen on 12/21/2015.
 */

public class LeftMenuAdapter extends BaseAdapter {

    private ArrayList<ItemLeftMenu> listMoviesSearch;
    private Context mContext;

    public LeftMenuAdapter(ArrayList<ItemLeftMenu> listImagePoster, Context context) {
        this.listMoviesSearch = listImagePoster;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listMoviesSearch.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(vn.com.flex.dragonfly.R.layout.left_menu_item, null);
        }
//        String icon = listMoviesSearch.get(position).icon;
//        ImageLoader.ImageCache imageCache = new BitmapLruCache();
//        ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(mContext), imageCache);
//        NetworkImageView iconImg = (NetworkImageView ) convertView.findViewById(R.id.img_item);
//        iconImg.setImageUrl(icon, imageLoader);
        String devName = listMoviesSearch.get(position).devname;
        String alertcount = listMoviesSearch.get(position).alertcount;
        TextView txt_item = (TextView) convertView.findViewById(vn.com.flex.dragonfly.R.id.txt_item);
        txt_item.setText(devName);
        LinearLayout layout_alert_count = (LinearLayout) convertView.findViewById(vn.com.flex.dragonfly.R.id.layout_alert_count);
        TextView txt_alert_count = (TextView) convertView.findViewById(vn.com.flex.dragonfly.R.id.txt_alert_count);
        if (!alertcount.equals("") && !alertcount.equals("0")) {
            layout_alert_count.setVisibility(View.VISIBLE);
            txt_alert_count.setText(alertcount);
        } else {
            layout_alert_count.setVisibility(View.GONE);
        }
        return convertView;
    }
}

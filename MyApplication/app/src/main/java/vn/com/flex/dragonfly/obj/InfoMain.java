package vn.com.flex.dragonfly.obj;

import java.io.Serializable;

/**
 * Created by ndung on 12/22/15.
 */
public class InfoMain implements Serializable {

    public String devicename;
    public String nextpaytime;
    public String icon;
    public String sim_status;
    public String trktime;
    public String driver;
    public String driverphone;
    public String status;
    public String imei;
    public String position;
    public String latitude;
    public String longitude;
    public int devstatus;
    public String id;
    public String direction;
    public String sum_distance;
    public String updatetime;
    public String lockstatus;
    public String lock_fuel;
    public String simno;
    public String sleepmode;
    public String statusdev;
    public String color;
    public int lat_cellID;
    public int lng_cellID;
    public int power_id;
    public String power_status;

    public boolean isOnline() {
        if (sleepmode.equals("0") && devstatus != 3) {
            return true;
        }
        return false;
    }

    public int getIconMakerResourceId() {
        if (devstatus == 0) {
            if (lock_fuel.equals("0")) {
                if (lockstatus.equals("0")) {
                    return vn.com.flex.dragonfly.R.drawable.xanh_duong_xe_dang_chay;
                } else {
                    return vn.com.flex.dragonfly.R.drawable.bat_canh_bao_1;
                }
            }
            return vn.com.flex.dragonfly.R.drawable.khoa_dong_1;
        } else if (devstatus == 1) {
            if (lock_fuel.equals("0")) {
                if (lockstatus.equals("0")) {
                    return vn.com.flex.dragonfly.R.drawable.do_khi_dung;
                } else {
                    return vn.com.flex.dragonfly.R.drawable.bat_canh_bao_3;
                }
            }
            return vn.com.flex.dragonfly.R.drawable.khoa_dong_3;
        } else if (devstatus == 2) {
            if (lock_fuel.equals("0")) {
                if (lockstatus.equals("0")) {
                    return vn.com.flex.dragonfly.R.drawable.vang_mat_gps;
                } else {
                    return vn.com.flex.dragonfly.R.drawable.bat_canh_bao_5;
                }
            }
            return vn.com.flex.dragonfly.R.drawable.khoa_dong_5;
        } else if (devstatus == 3) {
            if (lock_fuel.equals("0")) {
                if (lockstatus.equals("0")) {
                    return vn.com.flex.dragonfly.R.drawable.xam_mat_gprs;
                } else {
                    return vn.com.flex.dragonfly.R.drawable.bat_canh_bao_4;
                }
            }
            return vn.com.flex.dragonfly.R.drawable.khoa_dong_4;
        } else if (devstatus == 4) {
            if (lock_fuel.equals("0")) {
                if (lockstatus.equals("0")) {
                    return vn.com.flex.dragonfly.R.drawable.xanh_la_dang_truyen_log;
                } else {
                    return vn.com.flex.dragonfly.R.drawable.bat_canh_bao_2;
                }
            }
            return vn.com.flex.dragonfly.R.drawable.khoa_dong_2;
        }
        return vn.com.flex.dragonfly.R.drawable.xanh_duong_xe_dang_chay;
    }
}

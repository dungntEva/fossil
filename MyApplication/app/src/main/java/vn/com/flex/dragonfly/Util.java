package vn.com.flex.dragonfly;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

/**
 * Created by Joel on 30/06/2015.
 */
public class Util {
    public static String urlHDSD = "http://appmoto.adagps.com/user_guild.html";
    public static String urlHotro = "http://appmoto.adagps.com/support.html";
    public static String urlGioithieu = "http://appmoto.adagps.com/about.html";
    public static String urlNearByGoogle = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyDUuPnRMZa-Ftd2Ut7HCwqxKOUHAoqcdU4&location=";
    public static String urlMain = "http://appmoto.adagps.com/service/";
    public static String urlLeftmenu = urlMain + "getDevices?username=";
    public static String urlAlarm = urlMain + "getAlarms?username=";
    public static String urlLocation = urlMain + "getLocationInfo?username=";
    public static String urlLogin = urlMain + "login?username=";
    public static String urlLastData = urlMain + "getLastData?username=";
    public static String urlSendCommandGprs = urlMain + "sendCommandGprs?username=";
    public static String urlGetAlertCount = urlMain + "getAlertCount?username=";
    public static String urlReplay = urlMain + "getReview?username=";
    public static String urlChangePass = urlMain + "changePassword?username=";
    public static String urlChangeThongTin = urlMain + "updateUserInfo?username=";

    static public class Operations {

        /**
         * Checks to see if the device is online before carrying out any operations.
         *
         * @return
         */
        public static boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }
    }

    public static String md5(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static LatLng getPolygonOptions(LatLng point1, double widthInMeters, Context mContext, boolean checkGPS) {
        if (checkGPS) {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location net_loc = null, gps_loc = null, finalLoc = null;
            if (gps_enabled) {
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (network_enabled) {
                net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (gps_loc != null && net_loc != null) {

                if (gps_loc.getAccuracy() >= net_loc.getAccuracy()) {
                    finalLoc = gps_loc;
                } else {
                    finalLoc = net_loc;
                }
                // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    finalLoc = net_loc;
                } else if (net_loc != null) {
                    finalLoc = gps_loc;
                }
            }
            double bears = 0;
            if (finalLoc != null) {
                float bearing = finalLoc.getBearing();
                bears = bearing;
            }
            LatLng corner2 = SphericalUtil.computeOffset(point1, widthInMeters * 2, bears);
            return corner2;
        }
        return point1;
    }

}

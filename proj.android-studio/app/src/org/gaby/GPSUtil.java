package org.gaby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * on 2017-09-07.
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 */
public abstract class GPSUtil {

    private boolean hasReqAuth = false;
    private LocationManager locationManager;

    public abstract void onLocationChanged(Location location);

    private Boolean checkPermission(Activity context, String Manifest_permission) {
        Log.i("checkPermission", "checkPermission Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        Boolean r = false;
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest_permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest_permission}, 100);
            }else{
                //有权限
                r = true;
            }
        }
        return r;
    }

    @SuppressWarnings("MissingPermission")
    public void onResume(Activity activity) {
        if (hasReqAuth){
            return;
        }
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        // 检查GPS权限
        Boolean r = checkPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (locationManager != null && r) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                Location location = locationManager.getLastKnownLocation(provider);
                onLocationChanged(location);
                // Log.i("GPS", " =================== latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
                //GPS耗电 适宜户外
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, gpsLocationListener);
                //Network 基于网络基站定位 定位更快，耗电低 60s
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 100, netLocationListener);
            }
        }
        hasReqAuth = true;
    }

    @SuppressWarnings("MissingPermission")
    public void onPause() {
//        locationManager.removeUpdates(gpsLocationListener);
        locationManager.removeUpdates(netLocationListener);
    }

    private LocationListener netLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GPSUtil.this.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GPSUtil.this.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
}

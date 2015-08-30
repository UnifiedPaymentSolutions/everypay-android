package com.everypay.sdk.deviceinfo.fieldcollectors;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.everypay.sdk.deviceinfo.FieldError;
import com.everypay.sdk.deviceinfo.InfoField;

public class GpsCollector implements LocationListener {

    Context context;
    LocationManager locationManager;
    volatile GpsData lastGps;
    volatile boolean permissionsError;
    volatile boolean disabledError;

    public GpsCollector(Context context) {
        this.context = context;
        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.lastGps = null;
        this.permissionsError = false;
    }

    public void start() {
        try {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null)
                lastGps = new GpsData(loc);
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null)
                lastGps = new GpsData(loc);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            permissionsError = true;
        }
    }

    public InfoField stopAndGetInfoField() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            permissionsError = true;
        }

        if (permissionsError) {
            return new InfoField("gps", FieldError.PERMISSIONS);
        } else if (lastGps == null) {
            if (disabledError)
                return new InfoField("gps", FieldError.UNAVAILABLE);
            else
                return new InfoField("gps", FieldError.TIMEOUT);
        } else {
            return new InfoField("gps", lastGps);
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null)
            lastGps = new GpsData(loc);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider))
            disabledError = false;
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider))
            disabledError = true;
    }


    public static class GpsData {
        long timestamp;
        String provider;
        double longitude;
        double latitude;
        Float accuracy;
        Double altitude;
        Float bearing;
        Float speed;

        public GpsData(Location loc) {
            this.timestamp = loc.getTime();
            this.provider = loc.getProvider();
            this.longitude = loc.getLongitude();
            this.latitude = loc.getLatitude();
            if (loc.hasAccuracy())
                this.accuracy = loc.getAccuracy();
            if (loc.hasAltitude())
                this.altitude = loc.getAltitude();
            if (loc.hasBearing())
                this.bearing = loc.getBearing();
            if (loc.hasSpeed())
                this.speed = loc.getSpeed();
        }
    }
}

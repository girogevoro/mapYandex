package com.girogevoro.mapyandex.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.girogevoro.mapyandex.domain.repository.LocationProvider


class LocationProviderImpl(private val context: Context) : LocationProvider {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByGps= location
        }

        override fun onProviderDisabled(provider: String) {
            Log.d ("@@@", "onProviderDisabledGps")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d ("@@@", "onProviderEnabledNetwork")
        }
    }

    private val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByNetwork= location
        }
        override fun onProviderDisabled(provider: String) {
            Log.d ("@@@", "onProviderDisabledNetwork")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d ("@@@", "onProviderEnabledNetwork")
        }
    }

    override fun getLocation(): Location? {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) locationByGps ?: locationByNetwork
        else{
            val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    REFRESH,
                    MIN_DISTANCE,
                    gpsLocationListener
                )
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    REFRESH,
                    MIN_DISTANCE,
                    networkLocationListener
                )
            }
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                locationByGps = it
            }

            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let {
                locationByNetwork = it
            }
            locationByGps ?: locationByNetwork
        }
    }

    companion object{
        private const val REFRESH = 5000L
        private const val MIN_DISTANCE = 10F
    }

}
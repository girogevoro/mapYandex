package com.girogevoro.mapyandex.data.locationProvider

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class LocationProviderImpl(private val context: Context) : LocationProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getLocationByGps(): Flow<Location> = callbackFlow {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            trySend(it)
        }

        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location)
                locationManager.removeUpdates(this)
                channel.close()
            }

            override fun onProviderDisabled(provider: String) {
                Log.d("@@@", "onProviderDisabledGps")
                channel.close()
            }

            override fun onProviderEnabled(provider: String) {
                Log.d("@@@", "onProviderEnabledNetwork")
            }
        }

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                REFRESH,
                MIN_DISTANCE,
                gpsLocationListener
            )
        }
        awaitCancellation()
    }

    @SuppressLint("MissingPermission")
    override fun getLocationByNetwork(): Flow<Location> = callbackFlow {
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let {
            trySend(it)
        }

        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location)
                locationManager.removeUpdates(this)
                channel.close()
            }

            override fun onProviderDisabled(provider: String) {
                Log.d("@@@", "onProviderDisabledNetwork")
                channel.close()
            }

            override fun onProviderEnabled(provider: String) {
                Log.d("@@@", "onProviderEnabledNetwork")
            }
        }
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                REFRESH,
                MIN_DISTANCE,
                networkLocationListener
            )
        }
        awaitCancellation()
    }

    companion object {
        private const val REFRESH = 1000L
        private const val MIN_DISTANCE = 10F
    }

}
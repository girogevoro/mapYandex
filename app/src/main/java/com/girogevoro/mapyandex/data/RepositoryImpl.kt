package com.girogevoro.mapyandex.data

import android.content.Context
import android.location.Location
import com.girogevoro.mapyandex.data.locationProvider.LocationProvider
import com.girogevoro.mapyandex.data.locationProvider.LocationProviderImpl
import com.girogevoro.mapyandex.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(context: Context) : Repository {
    private val locationProvider: LocationProvider = LocationProviderImpl(context)
    override fun getLocationByGps(): Flow<Location> {
        return locationProvider.getLocationByGps()
    }

    override fun getLocationByNetwork(): Flow<Location> {
        return locationProvider.getLocationByNetwork()
    }
}
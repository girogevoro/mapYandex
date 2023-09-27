package com.girogevoro.mapyandex.data

import android.content.Context
import android.location.Location
import com.girogevoro.mapyandex.data.locationProvider.LocationProvider
import com.girogevoro.mapyandex.data.room.MarkerDao
import com.girogevoro.mapyandex.data.room.MarkerDatabase
import com.girogevoro.mapyandex.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(
    context: Context,
    private val locationProvider: LocationProvider,
    private val markerDatabase: MarkerDatabase,
) :
    Repository {
    override fun getLocationByGps(): Flow<Location> {
        return locationProvider.getLocationByGps()
    }

    override fun getLocationByNetwork(): Flow<Location> {
        return locationProvider.getLocationByNetwork()
    }

    override fun getMarkerDao(): MarkerDao {
        return markerDatabase.getMarketDao()
    }
}
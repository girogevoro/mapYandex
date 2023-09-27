package com.girogevoro.mapyandex.data.locationProvider

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationByGps(): Flow<Location>
    fun getLocationByNetwork(): Flow<Location>
}
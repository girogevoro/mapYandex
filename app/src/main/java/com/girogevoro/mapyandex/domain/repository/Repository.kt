package com.girogevoro.mapyandex.domain.repository

import android.location.Location
import com.girogevoro.mapyandex.data.room.MarkerDao
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getLocationByGps(): Flow<Location>
    fun getLocationByNetwork(): Flow<Location>
    fun getMarkerDao(): MarkerDao

}
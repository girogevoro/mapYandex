package com.girogevoro.mapyandex.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import com.girogevoro.mapyandex.domain.entity.MarkerEntity

interface MapViewModel {
    fun getLocation()
    fun getMarkers()
    fun setMarker(marker: MarkerEntity)

    fun getMarkersLiveData(): LiveData<List<MarkerEntity>>
    fun getLocationLiveData(): LiveData<Location>
}
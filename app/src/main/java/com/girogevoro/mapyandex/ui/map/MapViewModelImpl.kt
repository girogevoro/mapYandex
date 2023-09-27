package com.girogevoro.mapyandex.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.girogevoro.mapyandex.domain.entity.MarkerEntity
import com.girogevoro.mapyandex.domain.repository.Repository
import kotlinx.coroutines.launch

class MapViewModelImpl (val repository:Repository): ViewModel(), MapViewModel {

    private val markersLiveData = MutableLiveData<List<MarkerEntity>>()
    private val locationLiveData = MutableLiveData<Location>()

    override fun getLocation() {
        viewModelScope.launch {
            repository.getLocationByGps().collect{
                locationLiveData.postValue(it)
            }
        }
    }

    override fun getMarkers() {
        TODO("Not yet implemented")
    }

    override fun setMarker(marker: MarkerEntity) {
        TODO("Not yet implemented")
    }

    override fun getMarkersLiveData(): LiveData<List<MarkerEntity>> {
        return markersLiveData
    }

    override fun getLocationLiveData(): LiveData<Location> {
        return locationLiveData
    }
}
package com.girogevoro.mapyandex.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.girogevoro.mapyandex.domain.entity.MarkerEntity
import com.girogevoro.mapyandex.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModelImpl(val repository: Repository) : ViewModel(), MapViewModel {

    private val markersLiveData = MutableLiveData<List<MarkerEntity>>()
    private val locationLiveData = MutableLiveData<Location>()
    private val markersList = mutableListOf<MarkerEntity>()

    init {
        updateMarkers()
    }


    override fun getLocation() {
        viewModelScope.launch {
            repository.getLocationByGps().collect {
                locationLiveData.postValue(it)
            }
        }
    }

    override fun setMarker(marker: MarkerEntity) {
        markersList.add(marker)
        markersLiveData.postValue(markersList)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getMarkerDao().insertMarker(marker)
            }
        }
    }

    override fun getMarkersLiveData(): LiveData<List<MarkerEntity>> {
        return markersLiveData
    }

    override fun getLocationLiveData(): LiveData<Location> {
        return locationLiveData
    }

    override fun updateMarkers() {
        viewModelScope.launch {
            val list: List<MarkerEntity>
            withContext(Dispatchers.IO) {
                list = repository.getMarkerDao().getMarker()
            }
            markersList.clear()
            markersList.addAll(list)
            markersLiveData.postValue(markersList)
        }
    }


}
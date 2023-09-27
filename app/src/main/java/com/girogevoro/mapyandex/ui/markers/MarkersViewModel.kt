package com.girogevoro.mapyandex.ui.markers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.girogevoro.mapyandex.domain.entity.MarkerEntity
import com.girogevoro.mapyandex.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarkersViewModel(private val repository: Repository) : ViewModel() {

    init {
        updateMarkers()
    }

    private val markersLiveData = MutableLiveData<List<MarkerEntity>>()

    fun getMarkersLiveData(): LiveData<List<MarkerEntity>> {
        return markersLiveData
    }

    fun updateMarker(markerEntity: MarkerEntity) {
        viewModelScope.launch() {
            withContext(Dispatchers.IO) {
                repository.getMarkerDao().updateMarker(markerEntity)
            }
        }
    }

    fun deleteMarker(markerEntity: MarkerEntity) {
        var list: List<MarkerEntity>
        viewModelScope.launch() {
            withContext(Dispatchers.IO) {
                repository.getMarkerDao().deleteMarker(markerEntity)
                list = repository.getMarkerDao().getMarker()
            }
            markersLiveData.postValue(list)
        }
    }

    fun updateMarkers() {
        viewModelScope.launch {
            val list: List<MarkerEntity>
            withContext(Dispatchers.IO) {
                list = repository.getMarkerDao().getMarker()
            }
            markersLiveData.postValue(list)
        }
    }
}
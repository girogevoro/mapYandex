package com.girogevoro.mapyandex.ui.markers.recyler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.girogevoro.mapyandex.databinding.FragmentMarkersItemBinding
import com.girogevoro.mapyandex.domain.entity.MarkerEntity


class MarkerAdapter(
    private val updateMarkerListener: (MarkerEntity) -> Unit,
    private val deleteMarkerListener: (MarkerEntity) -> Unit,
) : RecyclerView.Adapter<MarkerViewHolder>() {
    private var markersList: List<MarkerEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        return MarkerViewHolder(
            FragmentMarkersItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            updateMarkerListener,
            deleteMarkerListener
        )
    }

    override fun getItemCount(): Int {
        return markersList.size
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(markersList[position])
    }

    fun setItems(newList: List<MarkerEntity>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(markersList, newList))
        markersList = newList
        result.dispatchUpdatesTo(this)
    }

    inner class DiffUtilCallback(
        private var oldItems: List<MarkerEntity>,
        private var newItems: List<MarkerEntity>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size
        override fun getNewListSize(): Int = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].id == newItems[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}


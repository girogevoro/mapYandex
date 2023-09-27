package com.girogevoro.mapyandex.ui.markers.recyler_view

import com.girogevoro.mapyandex.databinding.FragmentMarkersItemBinding
import com.girogevoro.mapyandex.domain.entity.MarkerEntity

class MarkerViewHolder(
    private val binding: FragmentMarkersItemBinding,
    private val updateMarkerListener: (MarkerEntity) -> Unit,
    private val deleteMarkerListener: (MarkerEntity) -> Unit,

    ) : ViewHolder(binding) {
    lateinit var marker: MarkerEntity
    override fun bind(markerEntity: MarkerEntity) {
        marker = markerEntity
        binding.locationTextView.text = buildString {
            append(marker.lat.toString())
            append(" : ")
            append(marker.lng)
        }
        binding.titleEditText.apply {
            setText(marker.title)
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus && marker.title != text.toString()) {
                    marker.title = text.toString()
                    updateMarkerListener(marker)
                }
            }
        }
        binding.descriptionEditText.apply {
            setText(marker.description)
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus && marker.description != text.toString()) {
                    marker.title = text.toString()
                    updateMarkerListener(marker)
                }
            }
        }

        itemView.setOnLongClickListener {
            deleteMarkerListener(markerEntity)
            true
        }
    }

}
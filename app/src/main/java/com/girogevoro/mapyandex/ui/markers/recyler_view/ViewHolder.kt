package com.girogevoro.mapyandex.ui.markers.recyler_view

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.girogevoro.mapyandex.domain.entity.MarkerEntity

abstract class ViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(markerEntity: MarkerEntity)
}
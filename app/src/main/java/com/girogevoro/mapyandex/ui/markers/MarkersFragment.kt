package com.girogevoro.mapyandex.ui.markers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.girogevoro.mapyandex.databinding.FragmentMarkersBinding
import com.girogevoro.mapyandex.domain.entity.MarkerEntity
import com.girogevoro.mapyandex.ui.markers.recyler_view.MarkerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarkersFragment : Fragment() {

    private var _binding: FragmentMarkersBinding? = null
    private val markerViewModel: MarkersViewModel by viewModel()
    private val markerAdapter: MarkerAdapter by lazy {
        MarkerAdapter(::updateMarker, ::deleteMarker)
    }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMarkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = markerAdapter
            }

            markerViewModel.getMarkersLiveData().observe(viewLifecycleOwner) {
                markerAdapter.setItems(it)
            }

            markerViewModel.updateMarkers()
    }

    private fun updateMarker(markerEntity: MarkerEntity) {
        markerViewModel.updateMarker(markerEntity)
    }

    private fun deleteMarker(markerEntity: MarkerEntity) {
        markerViewModel.deleteMarker(markerEntity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
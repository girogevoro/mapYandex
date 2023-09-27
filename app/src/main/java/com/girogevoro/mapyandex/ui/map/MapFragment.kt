package com.girogevoro.mapyandex.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.girogevoro.mapyandex.BuildConfig
import com.girogevoro.mapyandex.R
import com.girogevoro.mapyandex.databinding.FragmentHomeBinding
import com.girogevoro.mapyandex.domain.entity.MarkerEntity
import com.girogevoro.mapyandex.utils.PermissionHelperImpl
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.runtime.ui_view.ViewProvider
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val mapViewModel: MapViewModel by viewModel<MapViewModelImpl>()
    private val binding get() = _binding!!

    private val permissionHelperLocation by lazy {
        PermissionHelperImpl(
            requireActivity() as AppCompatActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PermissionHelperImpl.Texts(
                "Доступ к геолокации",
                "Доступ к геолокации необходим для отображения вашего текущего местоположения",
                "Для возможности отображения вашего текущего местоположения необходимо разрешить доступ к геолокации в настройках приложения. Перейти в настройки?",
                ">В дальнейшем для возможности отображения вашего текущего местоположения необходимо будет разрешить доступ к геолокации в настройках приложения.",
                "Продолжить"
            )
        )
    }


    private fun drawMyMarker(latitude: Double, longitude: Double, drawable: Drawable?) {
        val view = View(requireContext()).apply {
            background = drawable
        }
        binding.mapview.map.mapObjects.addPlacemark(
            Point(latitude, longitude),
            ViewProvider(view)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setPermissionLocation()
        initLocation()
        initMarkers()
        return root
    }

    private fun initMarkers() {
        val inputListener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                Toast.makeText(requireContext(), "123", Toast.LENGTH_SHORT).show()
            }

            override fun onMapLongTap(map: Map, point: Point) {
                mapViewModel.setMarker(MarkerEntity(point.latitude, point.longitude))
            }
        }
        binding.mapview.map.addInputListener(inputListener)

        mapViewModel.getMarkersLiveData().observe(viewLifecycleOwner) {
            val mapObjects = binding.mapview.map.mapObjects
            mapObjects.clear()
            it.forEach {markerEntity->
                drawMyMarker(
                    markerEntity.lat,
                    markerEntity.lng,
                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_bookmark_24)
                )
            }
        }
    }

    private fun initLocation() {
        binding.locationButton.setOnClickListener {
            mapViewModel.getLocation()
        }
        mapViewModel.getLocationLiveData().observe(viewLifecycleOwner) { location ->
            binding.mapview.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    DEF_ZOOM,
                    ZERO_FLOAT,
                    ZERO_FLOAT
                ),
                Animation(Animation.Type.SMOOTH, ZERO_FLOAT),
                null
            )
            drawMyMarker(
                location.latitude,
                location.longitude,
                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_location_on_24)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun setPermissionLocation() {
        binding.getPermissionButton.setOnClickListener {
            permissionHelperLocation.check()
        }

        permissionHelperLocation.setOnSuccessful {
            binding.locationGroup.visibility = View.VISIBLE
            binding.requestPermissionLocationGroup.visibility = View.INVISIBLE

        }

        permissionHelperLocation.setOnUnsuccessful {
            binding.locationGroup.visibility = View.INVISIBLE
            binding.requestPermissionLocationGroup.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "user do not permission", Toast.LENGTH_SHORT).show()
        }
        permissionHelperLocation.check(true)
    }


    override fun onStart() {
        super.onStart()
        permissionHelperLocation.check()
        MapKitFactory.getInstance().onStart()

        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val DEF_ZOOM = 16.0f
        const val ZERO_FLOAT = 0f
    }
}
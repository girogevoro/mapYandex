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
import com.girogevoro.mapyandex.databinding.FragmentMapBinding
import com.girogevoro.mapyandex.domain.entity.MarkerEntity
import com.girogevoro.mapyandex.utils.PermissionHelperImpl
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.ui_view.ViewProvider
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val mapViewModel: MapViewModel by viewModel<MapViewModelImpl>()
    private val binding get() = _binding!!

    private lateinit var mapObjects: MapObjectCollection
    private lateinit var  map: Map
    private var inputListener: InputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
        }

        override fun onMapLongTap(map: Map, point: Point) {
            mapViewModel.setMarker(MarkerEntity(point.latitude, point.longitude))
        }
    }
    val placemarkList: MutableList<PlacemarkMapObject> = mutableListOf()
    val viewList: MutableList<View> = mutableListOf()

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
        viewList.add(view)
        val placemark = mapObjects.addPlacemark(
            Point(latitude, longitude),
            ViewProvider(view)
        )
        placemarkList.add(placemark)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        MapKitFactory.initialize(requireContext())
        setPermissionLocation()
        initLocation()
        initMarkers()
        mapViewModel.updateMarkers()
        return root
    }

    private fun initMarkers() {
        map = binding.mapview.map
        mapObjects= binding.mapview.map.mapObjects
        map.addInputListener(inputListener)

        mapViewModel.getMarkersLiveData().observe(viewLifecycleOwner) {
            //mapObjects.clear()
            it.forEach { markerEntity ->
                drawMyMarker(
                    markerEntity.lat,
                    markerEntity.lng,
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.baseline_bookmark_24
                    )
                )
            }
        }
    }

    private fun initLocation() {
        binding.locationButton.setOnClickListener {
            mapViewModel.getLocation()
        }
        mapViewModel.getLocationLiveData().observe(viewLifecycleOwner) { location ->
            map.move(
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
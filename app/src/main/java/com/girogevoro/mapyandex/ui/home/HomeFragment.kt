package com.girogevoro.mapyandex.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.girogevoro.mapyandex.BuildConfig
import com.girogevoro.mapyandex.R
import com.girogevoro.mapyandex.databinding.FragmentHomeBinding
import com.girogevoro.mapyandex.utils.PermissionHelperImpl
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var locationManager: LocationManager? = null
    protected var mapObjects: MapObjectCollection? = null
    lateinit var imageProvider: ImageProvider
    lateinit var placemark: PlacemarkMapObject

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

    private val locationListener: LocationListener = LocationListener { location ->
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

//        mapObjects?.addPlacemark(
//            Point(location.latitude, location.longitude),
//            ImageProvider.fromResource(
//                requireContext(),
//                R.drawable.baseline_location_on_24
//            )
//        )

        drawMyLocationMark(
            location,
            AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_location_on_24)
        )

        stopUpdates()
    }

    private fun drawMyLocationMark(it: Location, drawable: Drawable?) {
        val view = View(requireContext()).apply {
            background = drawable
        }

        binding.mapview.map.mapObjects.addPlacemark(
            Point(it.latitude, it.longitude),
            ViewProvider(view)
        )
    }

    private fun stopUpdates() {
        locationManager?.removeUpdates(locationListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext());
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setPermissionLocation()
        return root
    }

    @SuppressLint("MissingPermission")
    private fun setPermissionLocation() {
        binding.getPermissionButton.setOnClickListener {
            permissionHelperLocation.check()
        }

        permissionHelperLocation.setOnSuccessful {
            mapObjects = binding.mapview.map.mapObjects
            binding.mapview.map.move(
                CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
            binding.locationGroup.visibility = View.VISIBLE
            binding.requestPermissionLocationGroup.visibility = View.INVISIBLE

            locationManager = requireActivity()
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            locationManager
                ?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    ZERO_LONG,
                    ZERO_FLOAT,
                    locationListener
                )
            locationManager
                ?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    ZERO_LONG,
                    ZERO_FLOAT,
                    locationListener
                )
        }

        permissionHelperLocation.setOnUnsuccessful {
            binding.locationGroup.visibility = View.INVISIBLE
            binding.requestPermissionLocationGroup.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "user do not permission", Toast.LENGTH_SHORT).show()
        }
        permissionHelperLocation.check()
    }

    private fun useMap(use: Boolean) {

    }

    override fun onStart() {
        super.onStart()
        permissionHelperLocation.check()

        MapKitFactory.getInstance().onStart();
        binding.mapview.onStart();
    }

    override fun onStop() {
        binding.mapview.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val DEF_ZOOM = 16.0f
        const val ZERO_FLOAT = 0f
        const val ZERO_LONG = 0L

    }
}
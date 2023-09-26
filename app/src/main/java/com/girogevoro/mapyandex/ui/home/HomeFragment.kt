package com.girogevoro.mapyandex.ui.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.girogevoro.mapyandex.databinding.FragmentHomeBinding
import com.girogevoro.mapyandex.utils.PermissionHelperImpl
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

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

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("3e03fbdf-45af-4fd3-b521-389a708d19a5");
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

    private fun setPermissionLocation() {
        binding.getPermissionButton.setOnClickListener {
            permissionHelperLocation.check()
        }

        permissionHelperLocation.setOnSuccessful {
            binding.mapview.map.move(
                CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
            binding.locationGroup.visibility = View.VISIBLE
            binding.requestPermissionLocationGroup.visibility = View.INVISIBLE
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


}
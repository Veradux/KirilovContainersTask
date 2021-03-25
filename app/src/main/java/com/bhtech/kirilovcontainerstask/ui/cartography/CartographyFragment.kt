package com.bhtech.kirilovcontainerstask.ui.cartography

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.databinding.FragmentCartographyBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.ui.base.MapBoxFragment
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel.ContainersState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val REQUEST_CODE_FINE_LOCATION_PERMISSION = 4

@AndroidEntryPoint
class CartographyFragment : MapBoxFragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()
    private lateinit var binding: FragmentCartographyBinding
    @Inject lateinit var navigator: ScreenNavigator
    private lateinit var symbolManager: SymbolManager

    private val onMapReadyCallback = OnMapReadyCallback { mapboxMap ->
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            symbolManager = SymbolManager(binding.mapCartography, mapboxMap, style)
            viewModel.containersState.value?.let { addMapBoxMarkersIfLoaded(it) }

            // TODO set camera to location
            mapboxMap.animateCamera { CameraPosition.Builder().target(LatLng()).build() }
            symbolManager.addClickListener { symbol ->
                val objType = object : TypeToken<Container>() {}.type
                viewModel.selectedContainer = Gson().fromJson(symbol.data, objType)
                true
            }
        }
    }

    private fun getSymbolOptionsFrom(containers: List<Container>): List<SymbolOptions> {
        return containers.map { container ->
            SymbolOptions().withLatLng(LatLng(container.gps.lat, container.gps.lng))
                // TODO set appropriate image and color
                .withIconImage("library-15")
                .withIconColor("#006992")
                .withIconSize(2f)
                .withData(Gson().toJsonTree(container))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requestFineLocationPermissions()
        getMapBoxInstance()
        binding = FragmentCartographyBinding.inflate(inflater, container, false)
        configureViews(binding, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadContainers()
        viewModel.containersState.observe(viewLifecycleOwner, { addMapBoxMarkersIfLoaded(it) })
    }

    override fun setMapView(): MapView = binding.mapCartography

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // TODO update to registerForActivityResult
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_CODE_FINE_LOCATION_PERMISSION)
    private fun requestFineLocationPermissions() {
        if (!EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.permission_fine_location_rationale_message), // TODO fix message
                requestCode = REQUEST_CODE_FINE_LOCATION_PERMISSION,
                ACCESS_FINE_LOCATION
            )
        }
    }

    private fun addMapBoxMarkersIfLoaded(containersState: ContainersState) {
        if (containersState is ContainersState.Loaded && this::symbolManager.isInitialized) {
            symbolManager.create(getSymbolOptionsFrom(containersState.containers))
        }
    }

    /**
     * Must be done before inflation of the view.
     */
    private fun getMapBoxInstance() = context?.let { Mapbox.getInstance(it, getString(R.string.mapbox_access_token)) }

    private fun configureViews(binding: FragmentCartographyBinding, savedInstanceState: Bundle?) {
        binding.btnContainers.setOnClickListener { navigator.navigateTo(Screen.CONTAINERS) }

        binding.mapCartography.onCreate(savedInstanceState)
        binding.mapCartography.getMapAsync(onMapReadyCallback)
    }
}
package com.bhtech.kirilovcontainerstask.ui.cartography

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.databinding.FragmentCartographyBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.ui.base.MapBoxFragment
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel.ContainersState
import com.bhtech.kirilovcontainerstask.ui.containersmenu.WASTE_TYPE_GLASS
import com.bhtech.kirilovcontainerstask.ui.containersmenu.WASTE_TYPE_HOUSEHOLD_GARBAGE
import com.bhtech.kirilovcontainerstask.ui.containersmenu.WASTE_TYPE_PAPER
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val REQUEST_CODE_FINE_LOCATION_PERMISSION = 1
private const val REQUEST_LOCATION_INTERVAL = 5000L
private const val REQUEST_LOCATION_FASTEST_INTERVAL = 1000L
private const val SYMBOL_ICON_SIZE = 1f
private const val INITIAL_ZOOM_LEVEL = 7.0

private const val FILLING_LEVEL_GREEN_UPPER_BOUND = 50
private const val FILLING_LEVEL_YELLOW_UPPER_BOUND = 75

@AndroidEntryPoint
class CartographyFragment : MapBoxFragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()
    private lateinit var binding: FragmentCartographyBinding
    @Inject lateinit var navigator: ScreenNavigator
    private lateinit var symbolManager: SymbolManager


    private val onMapReadyCallback = OnMapReadyCallback { mapboxMap ->
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            addImagesTo(style)
            symbolManager = SymbolManager(binding.mapCartography, mapboxMap, style)
            symbolManager.addClickListener(getOnContainerClickListener())
            symbolManager.iconAllowOverlap = true
            startLocationUpdates(mapboxMap, style)
            viewModel.containersState.value?.let { addMapBoxMarkersIfLoaded(it) }
        }
    }

    private fun addImagesTo(style: Style) {
        context?.let { context ->
            AppCompatResources.getDrawable(context, R.drawable.waste_icon_glass)?.toBitmap()
                ?.let { style.addImage(getString(R.string.glass_icon_id), it, true) }

            AppCompatResources.getDrawable(context, R.drawable.waste_icon_paper)?.toBitmap()
                ?.let { style.addImage(getString(R.string.paper_icon_id), it, true) }

            AppCompatResources.getDrawable(context, R.drawable.waste_icon_household_garbage)?.toBitmap()
                ?.let { style.addImage(getString(R.string.household_garbage_icon_id), it, true) }

            AppCompatResources.getDrawable(context, R.drawable.waste_icon_default)?.toBitmap()
                ?.let { style.addImage(getString(R.string.default_waste_type_icon_id), it, true) }
        }
    }

    private fun getContainerImage(wasteType: String): String = when (wasteType) {
        WASTE_TYPE_PAPER -> getString(R.string.paper_icon_id)
        WASTE_TYPE_GLASS -> getString(R.string.glass_icon_id)
        WASTE_TYPE_HOUSEHOLD_GARBAGE -> getString(R.string.household_garbage_icon_id)
        else -> getString(R.string.default_waste_type_icon_id)
    }

    private fun getContainerImageColor(fillingLevel: Int): String = when (fillingLevel) {
        in 0..FILLING_LEVEL_GREEN_UPPER_BOUND -> getHexValueFrom(R.color.fillingLevelGreen)
        in FILLING_LEVEL_GREEN_UPPER_BOUND..FILLING_LEVEL_YELLOW_UPPER_BOUND -> getHexValueFrom(R.color.fillingLevelYellow)
        else -> getHexValueFrom(R.color.fillingLevelRed)
    }

    private fun getHexValueFrom(colorId: Int) =
        String.format("#%06x", ContextCompat.getColor(requireContext(), colorId) and 0xffffff)

    /**
     * This listener should only listen for symbols which hold data of a Container.
     * Every other type should be ignored by the try/catch.
     */
    private fun getOnContainerClickListener() = OnSymbolClickListener { symbol ->
        try {
            val objType = object : TypeToken<Container>() {}.type
            viewModel.selectedContainer = Gson().fromJson(symbol.data, objType)
            navigator.navigateTo(Screen.EDIT_CONTAINER)
            true
        } catch (e: JsonSyntaxException) {
            false
        }
    }

    private fun getSymbolOptionsFrom(containers: List<Container>): List<SymbolOptions> {
        return containers.map { container ->
            SymbolOptions().withLatLng(LatLng(container.gps.lat, container.gps.lng))
                .withIconImage(getContainerImage(container.wasteType))
                .withIconColor(getContainerImageColor(container.fillingLevel))
                .withIconSize(SYMBOL_ICON_SIZE)
                .withData(Gson().toJsonTree(container))
        }
    }

    //region parent methods
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
    //endregion

    @AfterPermissionGranted(REQUEST_CODE_FINE_LOCATION_PERMISSION)
    private fun requestFineLocationPermissions() {
        if (!EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.permission_fine_location_rationale_message),
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

    private fun startLocationUpdates(mapboxMap: MapboxMap, style: Style) {
        if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            val locationOptions = LocationComponentActivationOptions
                .builder(requireContext(), style)
                .useDefaultLocationEngine(true)
                .locationEngineRequest(getLocationRequest())
                .build()

            with(mapboxMap.locationComponent) {
                activateLocationComponent(locationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING_GPS_NORTH
                zoomWhileTracking(INITIAL_ZOOM_LEVEL)
                forceLocationUpdate(lastKnownLocation)
            }
        }
    }

    private fun getLocationRequest() = LocationEngineRequest.Builder(REQUEST_LOCATION_INTERVAL)
        .setFastestInterval(REQUEST_LOCATION_FASTEST_INTERVAL)
        .setPriority(LocationEngineRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .build()
}
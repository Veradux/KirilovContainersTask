package com.bhtech.kirilovcontainerstask.ui.cartography

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
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartographyFragment : MapBoxFragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()
    private lateinit var binding: FragmentCartographyBinding
    @Inject lateinit var navigator: ScreenNavigator
    private lateinit var symbolManager: SymbolManager

    private val onMapReadyCallback = OnMapReadyCallback { mapboxMap ->
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            symbolManager = SymbolManager(binding.mapCartography, mapboxMap, style)

            symbolManager.addClickListener { symbol ->
                val objType = object : TypeToken<Container>() {}.type
                viewModel.selectedContainer = Gson().fromJson(symbol.data, objType)
                true
            }
        }
    }

    private fun getSymbolOptionsFrom(containers: List<Container>): List<SymbolOptions> {
        containers.map { container ->
            SymbolOptions().withIconImage("ICON ID?")
                .withLatLng(LatLng(container.gps.lat, container.gps.lng))
                .withIconSize(2f)
                .withData(Gson().toJsonTree(container))

        }
        return listOf(SymbolOptions())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        getMapBoxInstance()
        binding = FragmentCartographyBinding.inflate(inflater, container, false)
        configureViews(binding, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.containersState.observe(viewLifecycleOwner, { onContainersStateChange(it) })
        viewModel.loadContainers()
    }

    private fun onContainersStateChange(containersState: ContainersState) {
        if (containersState is ContainersState.Loaded) {
            addMapBoxMarkers(containersState.containers)
        }
    }

    private fun addMapBoxMarkers(containers: List<Container>) {
        if (this::symbolManager.isInitialized) {
            symbolManager.create(getSymbolOptionsFrom(containers))
        }
    }

    override fun setMapView(): MapView = binding.mapCartography

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
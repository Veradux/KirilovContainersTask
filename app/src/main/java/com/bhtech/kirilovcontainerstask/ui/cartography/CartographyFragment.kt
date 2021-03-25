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
import com.bhtech.kirilovcontainerstask.ui.base.MapBoxFragment
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartographyFragment : MapBoxFragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()
    private lateinit var binding: FragmentCartographyBinding
    @Inject lateinit var navigator: ScreenNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        createMapBoxInstance()
        binding = FragmentCartographyBinding.inflate(inflater, container, false)
        configureViews(binding, savedInstanceState)
        return binding.root
    }

    override fun setMapView(): MapView = binding.mapCartography

    /**
     * Must be done before inflation of the view.
     */
    private fun createMapBoxInstance() {
        context?.let { Mapbox.getInstance(it, getString(R.string.mapbox_access_token)) }
    }

    private fun configureViews(binding: FragmentCartographyBinding, savedInstanceState: Bundle?) {
        binding.btnContainers.setOnClickListener { navigator.navigateTo(Screen.CONTAINERS) }

        binding.mapCartography.onCreate(savedInstanceState)
        binding.mapCartography.getMapAsync { map ->
            map.setStyle(Style.MAPBOX_STREETS)
            // TODO add markers here?
        }
    }
}
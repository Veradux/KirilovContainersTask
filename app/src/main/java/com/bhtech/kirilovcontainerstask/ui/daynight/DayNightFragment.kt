package com.bhtech.kirilovcontainerstask.ui.daynight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bhtech.kirilovcontainerstask.databinding.FragmentDayNightBinding
import com.bhtech.rangeslider.CircularSliderRange.OnSliderRangeMovedListener
import com.bhtech.rangeslider.ThumbEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayNightFragment : Fragment() {

    private val viewModel: DayNightViewModel by viewModels()
    private lateinit var binding: FragmentDayNightBinding

    private var startPos = 0.0
    private var endPos = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDayNightBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    private fun configureViews(binding: FragmentDayNightBinding) {
        setUpRangeSlider(binding)
    }

    private fun setUpRangeSlider(binding: FragmentDayNightBinding) {
        binding.dayNightSlider.setOnSliderRangeMovedListener(object : OnSliderRangeMovedListener {

            override fun onStartSliderMoved(pos: Double) {
                binding.startThumbPosition.text = pos.toString()
            }

            // between 150-360 and 0-30

            override fun onEndSliderMoved(pos: Double) {
                binding.endThumbPosition.text = pos.toString()
            }

            override fun onStartSliderEvent(event: ThumbEvent?) {
            }

            override fun onEndSliderEvent(event: ThumbEvent?) {
            }
        })
    }
}
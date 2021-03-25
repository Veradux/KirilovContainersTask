package com.bhtech.kirilovcontainerstask.ui.containersmenu

import android.R.layout.simple_spinner_item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.bhtech.kirilovcontainerstask.databinding.FragmentContainersMenuBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel.ContainersState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val CONTAINER_FILTER_OPTIONS = listOf("Filter Waste Type", "All", "Glass", "Paper", "Household Garbage")

@AndroidEntryPoint
class ContainersMenuFragment : Fragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()
    private lateinit var binding: FragmentContainersMenuBinding
    @Inject lateinit var navigator: ScreenNavigator
    private val containersRvAdapter = ContainersAdapter(::onContainerClicked)

    private val spinnerFilterOnItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        /**
         * When an item is selected, if it is the first item, which is the title of the menu, nothing will change.
         * If it is the second item *All*, which is at index 1, the filtering is skipped and all containers are shown.
         */
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == 0) return
            val filteredContainers = with(viewModel.getContainers()) {
                if (position > 1) {
                    this.filter { container -> container.wasteType == CONTAINER_FILTER_OPTIONS[position] }
                } else {
                    this
                }
            }

            if (filteredContainers.isNotEmpty()) {
                binding.tvNoContainersLoadedMessage.visibility = View.GONE
                binding.rvContainers.visibility = View.VISIBLE
                containersRvAdapter.submitList(filteredContainers)
            } else {
                binding.rvContainers.visibility = View.GONE
                binding.tvNoContainersLoadedMessage.visibility = View.VISIBLE
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentContainersMenuBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.containersState.observe(viewLifecycleOwner, { onContainersStateChange(it) })
        viewModel.loadContainers()
        val containerState = viewModel.containersState.value
        if (containerState is ContainersState.Loaded) {
            showContainers(containerState)
        }
    }

    private fun configureViews(binding: FragmentContainersMenuBinding) {
        binding.rvContainers.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.rvContainers.adapter = containersRvAdapter

        binding.btnMap.setOnClickListener { navigator.navigateTo(Screen.MAP) }
        configureFilterSpinner()
    }

    private fun configureFilterSpinner() {
        context?.let {
            val array = ArrayAdapter(it, simple_spinner_item, CONTAINER_FILTER_OPTIONS)
            array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnFilterOptions.adapter = array
        }
        binding.spnFilterOptions.onItemSelectedListener = spinnerFilterOnItemSelectedListener
    }

    private fun onContainerClicked(container: Container) {
        navigator.navigateTo(Screen.EDIT_CONTAINER)
        viewModel.selectedContainer = container
    }

    private fun onContainersStateChange(containersState: ContainersState) {
        when (containersState) {
            is ContainersState.Loading -> showContainersLoading()
            is ContainersState.Empty -> showEmptyState()
            is ContainersState.Loaded -> showContainers(containersState)
        }
    }

    private fun showContainers(containers: ContainersState.Loaded) {
        binding.pbContainersLoadingSpinner.visibility = View.GONE
        containersRvAdapter.submitList(containers.containers)
    }

    private fun showContainersLoading() {
        binding.pbContainersLoadingSpinner.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.pbContainersLoadingSpinner.visibility = View.GONE
        binding.tvNoContainersLoadedMessage.visibility = View.VISIBLE
    }
}

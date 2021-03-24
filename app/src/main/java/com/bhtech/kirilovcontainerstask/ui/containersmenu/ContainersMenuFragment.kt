package com.bhtech.kirilovcontainerstask.ui.containersmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.bhtech.kirilovcontainerstask.databinding.FragmentContainersMenuBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel.ContainersState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContainersMenuFragment : Fragment() {

    private val viewModel: ContainersMenuViewModel by viewModels()
    private lateinit var binding: FragmentContainersMenuBinding
    @Inject lateinit var navigator: ScreenNavigator
    private val containersRvAdapter = ContainersAdapter { container -> onContainerClicked(container) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentContainersMenuBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    private fun configureViews(binding: FragmentContainersMenuBinding) {
        binding.rvContainers.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.rvContainers.adapter = containersRvAdapter

        binding.btnMap.setOnClickListener { navigator.navigateTo(Screen.MAP) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.containersState.observe(viewLifecycleOwner, Observer { onContainersStateChange(it) })
        viewModel.loadContainers()
    }

    private fun onContainerClicked(container: Container) {
        navigator.navigateTo(Screen.EDIT_CONTAINER)
        viewModel.selectedContainer = container
    }

    private fun onContainersStateChange(containersState: ContainersState) {
        when (containersState) {
            is ContainersState.Loading -> showContainersLoading()
            is ContainersState.Empty -> showEmptyState()
            is ContainersState.Loaded -> showContainers(containersState.containers)
        }
    }

    private fun showContainers(containers: List<Container>) {
        binding.pbContainersLoadingSpinner.visibility = View.GONE
        containersRvAdapter.submitList(containers)
    }

    private fun showContainersLoading() {
        binding.pbContainersLoadingSpinner.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.pbContainersLoadingSpinner.visibility = View.GONE
        binding.tvNoContainersLoadedMessage.visibility = View.VISIBLE
    }
}

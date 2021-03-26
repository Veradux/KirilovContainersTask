package com.bhtech.kirilovcontainerstask.ui.containersmenu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhtech.kirilovcontainerstask.service.containers.ContainersService
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

const val FILTER_OPTIONS_MESSAGE = "Filter Waste Type"
const val WASTE_TYPE_ALL = "All"
const val WASTE_TYPE_GLASS = "Glass"
const val WASTE_TYPE_PAPER = "Paper"
const val WASTE_TYPE_HOUSEHOLD_GARBAGE = "Household Garbage"

val CONTAINER_FILTER_OPTIONS = listOf(
    FILTER_OPTIONS_MESSAGE,
    WASTE_TYPE_ALL,
    WASTE_TYPE_GLASS,
    WASTE_TYPE_PAPER,
    WASTE_TYPE_HOUSEHOLD_GARBAGE
)

@HiltViewModel
class ContainersMenuViewModel @Inject constructor() : ViewModel() {

    @Inject lateinit var containersService: ContainersService

    sealed class ContainersState {
        object Loading : ContainersState()
        object Empty : ContainersState()
        data class Loaded(val containers: List<Container>) : ContainersState()
    }

    val containersState = MutableLiveData<ContainersState>().apply { value = ContainersState.Loading }
    var selectedContainer: Container? = null

    private fun receiveContainers(list: List<Container>) {
        when {
            list.isEmpty() -> containersState.value = ContainersState.Empty
            list.isNotEmpty() -> containersState.value = ContainersState.Loaded(list)
        }
    }

    fun loadContainers() {
        if (containersState.value !is ContainersState.Loaded) {
            val backgroundJob = viewModelScope.async(Dispatchers.IO) { containersService.getAllContainers() }
            viewModelScope.launch { receiveContainers(backgroundJob.await()) }
        }
    }

    fun getContainers() = when (val state = containersState.value) {
        is ContainersState.Loaded -> state.containers
        else -> emptyList()
    }

    fun getStateWithReplacedContainer(new: Container, old: Container): ContainersState =
        when (val state = containersState.value) {
            is ContainersState.Loaded -> {
                ContainersState.Loaded(state.containers.minusElement(old).plusElement(new))
            }
            else -> ContainersState.Empty
        }
}

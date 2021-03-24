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

    fun loadContainers() {
        val backgroundJob = viewModelScope.async(Dispatchers.IO) { containersService.getAllContainers() }
        viewModelScope.launch { receiveContainers(backgroundJob.await()) }
    }

    private fun receiveContainers(list: List<Container>) {
        when {
            list.isEmpty() -> containersState.value = ContainersState.Empty
            list.isNotEmpty() -> containersState.value = ContainersState.Loaded(list)
        }
    }
}

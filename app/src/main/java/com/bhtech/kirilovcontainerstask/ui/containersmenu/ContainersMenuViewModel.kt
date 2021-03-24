package com.bhtech.kirilovcontainerstask.ui.containersmenu

import androidx.lifecycle.ViewModel
import com.bhtech.kirilovcontainerstask.service.containers.ContainersService
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainersMenuViewModel @Inject constructor() : ViewModel() {

    @Inject lateinit var containersService: ContainersService

    // TODO don't do this on the UI thread
    fun getContainers(): List<Container> = containersService.getAllContainers()
}

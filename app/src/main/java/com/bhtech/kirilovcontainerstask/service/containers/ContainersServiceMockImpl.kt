package com.bhtech.kirilovcontainerstask.service.containers

import android.content.Context
import com.beust.klaxon.Klaxon
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.service.containers.model.ContainerModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainersServiceMockImpl(private val appContext: Context) : ContainersService {

    override fun getAllContainers(): List<ContainerModel> {
        val json = appContext.resources.openRawResource(R.raw.containers)
        return Klaxon().parse<List<ContainerModel>>(json) ?: emptyList()
    }
}
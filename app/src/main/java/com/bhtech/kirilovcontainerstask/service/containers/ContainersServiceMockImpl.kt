package com.bhtech.kirilovcontainerstask.service.containers

import android.content.Context
import com.beust.klaxon.Klaxon
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.service.containers.model.Container

class ContainersServiceMockImpl(private val appContext: Context) : ContainersService {

    override fun getAllContainers(): List<Container> {
        val containersJson = appContext.resources.openRawResource(R.raw.containers)
        return Klaxon().parseArray(containersJson) ?: emptyList()
    }
}
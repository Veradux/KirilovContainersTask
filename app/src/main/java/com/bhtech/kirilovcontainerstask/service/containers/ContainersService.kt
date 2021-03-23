package com.bhtech.kirilovcontainerstask.service.containers

import com.bhtech.kirilovcontainerstask.service.containers.model.ContainerModel

interface ContainersService {

    fun getAllContainers(): List<ContainerModel>
}
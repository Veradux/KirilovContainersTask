package com.bhtech.kirilovcontainerstask.service.containers

import com.bhtech.kirilovcontainerstask.service.containers.model.Container

interface ContainersService {

    fun getAllContainers(): List<Container>
}
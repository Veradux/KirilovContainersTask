package com.bhtech.kirilovcontainerstask.service.containers.model

data class Container(
    val name: String,
    val street: String,
    val city: String,
    val zipcode: Int,
    val gps: Gps,
    val wasteType: String,
    val fillingLevel: Int
)

data class Gps(
    val lat: Double,
    val lng: Double
)

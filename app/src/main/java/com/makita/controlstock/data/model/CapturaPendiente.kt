package com.makita.controlstock.data.model

data class CapturaPendiente(
    val id: Long = System.currentTimeMillis(),
    val itemCode: String,
    val descripcion: String,
    val cantidad: Double,
    val ubicacionOrigen: String,
    val ubicacionDestino: String,
    val bodegaOrigen: String,
    val bodegaDestino: String
)
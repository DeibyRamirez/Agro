package com.jeison.agrocontrol.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production_records")
data class ProductionLocal(
    @PrimaryKey
    val serverId: String,
    val productType: String,
    val cantidad: Double,
    val unidad: String,
    val fecha: String,
    val notas: String,
    val updatedAtEpoch: Long,
)

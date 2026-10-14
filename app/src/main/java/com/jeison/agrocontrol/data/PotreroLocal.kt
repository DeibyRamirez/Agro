package com.jeison.agrocontrol.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "potreros")
data class PotreroLocal(
    @PrimaryKey
    val serverId: String,
    val nombre: String,
    val areaHectareas: Double?,
    val estado: String,
    val especiesJson: String,
    val updatedAtEpoch: Long,
)

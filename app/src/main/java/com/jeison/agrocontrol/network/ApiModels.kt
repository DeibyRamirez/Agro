package com.jeison.agrocontrol.network

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val fincaNombre: String,
)
data class TokenResponse(val accessToken: String, val refreshToken: String, val tokenType: String)
data class DashboardSummary(
    val animalesRegistrados: Int,
    val potrerosRegistrados: Int,
    val eventosReproduccion: Int,
    val litrosLecheSemana: Double,
    val pendientesRecientes: List<PendingTaskDto>,
)
data class PendingTaskDto(val id: String, val titulo: String, val vencimiento: String, val tipo: String)
data class SyncPushRequest(val changes: List<SyncChange>)
data class SyncChange(
    val entity: String,
    val operation: String,
    val localId: String,
    val serverId: String? = null,
    val payload: Map<String, Any?>,
    val clientUpdatedAt: String,
)
data class SyncPushResponse(val applied: List<SyncApplied>)
data class SyncApplied(val localId: String, val serverId: String, val entity: String)
data class SyncPullResponse(
    val since: String,
    val animals: List<AnimalDto>,
    val productionRecords: List<ProductionDto>,
    val pendingTasks: List<PendingTaskFullDto>,
    val potreros: List<PotreroDto>,
)
data class AnimalDto(
    val id: String,
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val raza: String,
    val sexo: String,
    val peso: Double,
    val fechaNacimiento: String,
    val potreroNombre: String,
    val observacion: String,
    val updatedAt: String,
)
data class PotreroDto(val id: String, val nombre: String, val areaHectareas: Double?, val estado: String, val especiesPermitidas: List<String>, val updatedAt: String)
data class ProductionDto(val id: String, val productType: String, val cantidad: Double, val unidad: String, val fecha: String, val notas: String, val updatedAt: String)
data class PendingTaskFullDto(val id: String, val titulo: String, val tipo: String, val status: String, val vencimiento: String, val referencia: String, val updatedAt: String)
data class ReportResponse(val period: String, val totalsByProductType: Map<String, Double>, val recordCount: Int)

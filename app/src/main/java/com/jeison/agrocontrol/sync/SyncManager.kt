package com.jeison.agrocontrol.sync

import com.google.gson.Gson
import com.jeison.agrocontrol.auth.TokenStore
import com.jeison.agrocontrol.data.Animal
import com.jeison.agrocontrol.data.AppDatabase
import com.jeison.agrocontrol.data.PendingTaskLocal
import com.jeison.agrocontrol.data.PotreroLocal
import com.jeison.agrocontrol.data.ProductionLocal
import com.jeison.agrocontrol.network.RetrofitClient
import com.jeison.agrocontrol.network.SyncChange
import com.jeison.agrocontrol.network.SyncPushRequest
import java.time.Instant

class SyncManager(
    private val database: AppDatabase,
    private val tokenStore: TokenStore,
    private val gson: Gson = Gson(),
) {
    private val api by lazy { RetrofitClient.createApi(tokenStore) }

    suspend fun syncAll(): Result<Unit> = runCatching {
        if (!tokenStore.isLoggedIn()) return@runCatching
        pushOutbox()
        pullRemote()
    }

    private suspend fun pushOutbox() {
        val syncDao = database.syncDao()
        val animalDao = database.animalDao()
        val pending = syncDao.pendingOutbox()
        if (pending.isEmpty()) return
        val changes = pending.map { row ->
            SyncChange(
                entity = row.entity,
                operation = row.operation,
                localId = row.localId,
                serverId = row.serverId,
                payload = gson.fromJson(row.payloadJson, Map::class.java) as Map<String, Any?>,
                clientUpdatedAt = Instant.now().toString(),
            )
        }
        val response = api.syncPush(SyncPushRequest(changes))
        response.applied.forEach { applied ->
            if (applied.entity == "ANIMAL") {
                animalDao.updateSyncState(
                    applied.localId.toLong(),
                    applied.serverId,
                    Animal.SyncState.SYNCED.name,
                )
            }
        }
        pending.forEach { syncDao.markSent(it.id) }
    }

    private suspend fun pullRemote() {
        val syncDao = database.syncDao()
        val since = syncDao.getMetadata("last_pull")?.value
        val pull = api.syncPull(if (since.isNullOrBlank()) null else since)
        val cache = database.localCacheDao()
        val animalDao = database.animalDao()
        pull.animals.forEach { dto ->
            val existing = animalDao.findByCodigo(dto.codigo)
            val merged = Animal(
                id = existing?.id ?: 0,
                serverId = dto.id,
                codigo = dto.codigo,
                nombre = dto.nombre,
                tipo = dto.tipo,
                raza = dto.raza,
                sexo = dto.sexo,
                peso = dto.peso,
                fechaNacimiento = dto.fechaNacimiento.take(10),
                potrero = dto.potreroNombre,
                observacion = dto.observacion,
                syncState = Animal.SyncState.SYNCED.name,
                updatedAtEpoch = System.currentTimeMillis(),
            )
            if (existing == null) animalDao.insert(merged) else animalDao.update(merged)
        }
        cache.upsertPotreros(
            pull.potreros.map {
                PotreroLocal(
                    serverId = it.id,
                    nombre = it.nombre,
                    areaHectareas = it.areaHectareas,
                    estado = it.estado,
                    especiesJson = gson.toJson(it.especiesPermitidas),
                    updatedAtEpoch = System.currentTimeMillis(),
                )
            },
        )
        cache.upsertTasks(
            pull.pendingTasks.map {
                PendingTaskLocal(
                    serverId = it.id,
                    titulo = it.titulo,
                    tipo = it.tipo,
                    status = it.status,
                    vencimiento = it.vencimiento.take(10),
                    referencia = it.referencia,
                    updatedAtEpoch = System.currentTimeMillis(),
                )
            },
        )
        cache.upsertProduction(
            pull.productionRecords.map {
                ProductionLocal(
                    serverId = it.id,
                    productType = it.productType,
                    cantidad = it.cantidad,
                    unidad = it.unidad,
                    fecha = it.fecha.take(10),
                    notas = it.notas,
                    updatedAtEpoch = System.currentTimeMillis(),
                )
            },
        )
        syncDao.upsertMetadata(
            com.jeison.agrocontrol.data.SyncMetadata("last_pull", Instant.now().toString()),
        )
    }
}

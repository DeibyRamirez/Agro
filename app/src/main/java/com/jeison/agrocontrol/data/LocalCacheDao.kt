package com.jeison.agrocontrol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPotreros(items: List<PotreroLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(items: List<PendingTaskLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduction(items: List<ProductionLocal>)

    @Query("SELECT * FROM potreros ORDER BY nombre")
    suspend fun allPotreros(): List<PotreroLocal>

    @Query("SELECT * FROM pending_tasks WHERE status = 'PENDING' ORDER BY vencimiento")
    suspend fun pendingTasks(): List<PendingTaskLocal>

    @Query("SELECT * FROM production_records ORDER BY fecha DESC")
    suspend fun allProduction(): List<ProductionLocal>
}

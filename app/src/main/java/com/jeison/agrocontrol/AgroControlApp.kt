package com.jeison.agrocontrol

import android.app.Application
import com.jeison.agrocontrol.auth.TokenStore
import com.jeison.agrocontrol.data.Animal
import com.jeison.agrocontrol.data.AnimalRepository
import com.jeison.agrocontrol.data.AppDatabase
import com.jeison.agrocontrol.sync.SyncManager
import com.jeison.agrocontrol.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AgroControlApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database by lazy { AppDatabase.get(this) }

    val tokenStore: TokenStore by lazy { TokenStore(this) }

    val syncScheduler: SyncScheduler by lazy { SyncScheduler(this) }

    val syncManager: SyncManager by lazy {
        SyncManager(database, tokenStore)
    }

    val animalRepository: AnimalRepository by lazy {
        AnimalRepository(database.animalDao(), database.syncDao(), syncScheduler = syncScheduler)
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            seedAnimalesSiVacio()
        }
    }

    private suspend fun seedAnimalesSiVacio() {
        if (animalRepository.count() > 0) return
        animalRepository.insert(
            Animal(
                codigo = "001",
                nombre = "",
                tipo = "Vaca",
                raza = "Holstein",
                sexo = "Hembra",
                peso = 480.0,
                fechaNacimiento = "2020-03-15",
                potrero = "Potrero 1",
                syncState = Animal.SyncState.SYNCED.name,
            ),
        )
    }
}

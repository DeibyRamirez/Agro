package com.jeison.agrocontrol

import com.jeison.agrocontrol.data.AppDatabase

class PotrerosActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        loadText {
            val items = AppDatabase.get(this@PotrerosActivity).localCacheDao().allPotreros()
            if (items.isEmpty()) "No hay potreros en caché.\nSincroniza tras iniciar sesión."
            else items.joinToString("\n\n") {
                "🌱 ${it.nombre}\nÁrea: ${it.areaHectareas ?: "-"} ha\nEstado: ${it.estado}\nEspecies: ${it.especiesJson}"
            }
        }
    }
}

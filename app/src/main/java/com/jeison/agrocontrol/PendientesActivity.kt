package com.jeison.agrocontrol

import com.jeison.agrocontrol.data.AppDatabase

class PendientesActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        loadText {
            val items = AppDatabase.get(this@PendientesActivity).localCacheDao().pendingTasks()
            if (items.isEmpty()) "No hay pendientes en caché."
            else items.joinToString("\n\n") { "📌 ${it.titulo}\nVence: ${it.vencimiento}\nRef: ${it.referencia}" }
        }
    }
}

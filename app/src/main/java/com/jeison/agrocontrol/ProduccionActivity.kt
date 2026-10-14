package com.jeison.agrocontrol

import com.jeison.agrocontrol.data.AppDatabase

class ProduccionActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        loadText {
            val items = AppDatabase.get(this@ProduccionActivity).localCacheDao().allProduction()
            if (items.isEmpty()) "Registra producción en el servidor o sincroniza."
            else items.joinToString("\n\n") {
                "📦 ${it.productType}: ${it.cantidad} ${it.unidad}\nFecha: ${it.fecha}\n${it.notas}"
            }
        }
    }
}

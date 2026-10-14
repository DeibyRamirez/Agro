package com.jeison.agrocontrol

import com.jeison.agrocontrol.data.AppDatabase

class FinanzasActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        loadText {
            val items = AppDatabase.get(this@FinanzasActivity).localCacheDao().allProduction()
            if (items.isEmpty()) return "Productividad por rubro (sincroniza producción)."
            val grouped = items.groupBy { it.productType }
                .mapValues { entry -> entry.value.sumOf { it.cantidad } }
            buildString {
                append("Productividad agregada:\n\n")
                grouped.forEach { (type, total) ->
                    append("• $type: $total\n")
                }
            }
        }
    }
}

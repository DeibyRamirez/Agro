package com.jeison.agrocontrol

import androidx.lifecycle.lifecycleScope
import com.jeison.agrocontrol.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class ReportesActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val app = application as AgroControlApp
            val texto = if (!app.tokenStore.isLoggedIn()) {
                "Inicia sesión para ver reportes semanales y mensuales."
            } else {
                runCatching {
                    val api = RetrofitClient.createApi(app.tokenStore)
                    val cal = Calendar.getInstance()
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH) + 1
                    cal.add(Calendar.DAY_OF_YEAR, -7)
                    val weekStart = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
                    val today = Calendar.getInstance()
                    val todayStr = "${today.get(Calendar.YEAR)}-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.DAY_OF_MONTH)}"
                    val weekly = api.reportWeekly(weekStart, todayStr)
                    val monthly = api.reportMonthly(year, month)
                    buildString {
                        append("Reporte semanal (${weekly.recordCount} registros)\n")
                        weekly.totalsByProductType.forEach { (k, v) -> append("  $k: $v\n") }
                        append("\nReporte mensual\n")
                        monthly.totalsByProductType.forEach { (k, v) -> append("  $k: $v\n") }
                    }
                }.getOrElse { "No se pudo cargar reportes: ${it.message}" }
            }
            binding.tvContenidoModulo.text = texto
        }
    }
}

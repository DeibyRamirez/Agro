package com.jeison.agrocontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jeison.agrocontrol.databinding.ActivityMainBinding
import com.jeison.agrocontrol.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as AgroControlApp
        if (!app.tokenStore.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAnimales.setOnClickListener { startActivity(Intent(this, AnimalesActivity::class.java)) }
        binding.navAnimales.setOnClickListener { startActivity(Intent(this, AnimalesActivity::class.java)) }
        binding.btnProduccion.setOnClickListener { abrirModulo(ProduccionActivity::class.java, "Producción") }
        binding.btnPotreros.setOnClickListener { abrirModulo(PotrerosActivity::class.java, "Potreros") }
        binding.btnReproduccion.setOnClickListener { abrirModulo(ReproduccionActivity::class.java, "Reproducción") }
        binding.navPotreros.setOnClickListener { abrirModulo(PotrerosActivity::class.java, "Potreros") }
        binding.navFinanzas.setOnClickListener { abrirModulo(FinanzasActivity::class.java, "Finanzas / Productividad") }
        binding.navMas.setOnClickListener { abrirModulo(MasActivity::class.java, "Más") }
        binding.btnVerPendientes.setOnClickListener { abrirModulo(PendientesActivity::class.java, "Pendientes") }
        binding.btnReportes.setOnClickListener { abrirModulo(ReportesActivity::class.java, "Reportes") }
        binding.navInicio.setOnClickListener {
            Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::binding.isInitialized) return
        actualizarDashboard()
        lifecycleScope.launch {
            (application as AgroControlApp).syncManager.syncAll()
            actualizarDashboard()
        }
    }

    private fun actualizarDashboard() {
        val app = application as AgroControlApp
        lifecycleScope.launch {
            val localCount = app.animalRepository.count()
            binding.btnAnimales.text =
                "🐄\nAnimales\n$localCount registrado${if (localCount == 1) "" else "s"}"

            if (app.tokenStore.isLoggedIn()) {
                runCatching {
                    val summary = RetrofitClient.createApi(app.tokenStore).dashboardSummary()
                    binding.btnProduccion.text =
                        "🥛\nProducción\n${summary.litrosLecheSemana.toInt()} litros"
                    binding.btnPotreros.text =
                        "🌱\nPotreros\n${summary.potrerosRegistrados} registrados"
                    binding.btnReproduccion.text =
                        "❤️\nReproducción\n${summary.eventosReproduccion} eventos"
                }
            }
        }
    }

    private fun abrirModulo(activityClass: Class<*>, title: String) {
        startActivity(
            Intent(this, activityClass).putExtra(ModuleListActivity.EXTRA_TITLE, title),
        )
    }
}

package com.jeison.agrocontrol

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jeison.agrocontrol.databinding.ActivityModuleBinding
import kotlinx.coroutines.launch

open class ModuleListActivity : AppCompatActivity() {

    protected lateinit var binding: ActivityModuleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnVolverModulo.setOnClickListener { finish() }
        binding.tvTituloModulo.text = intent.getStringExtra(EXTRA_TITLE) ?: "Módulo"
    }

    protected fun loadText(block: suspend () -> String) {
        lifecycleScope.launch {
            binding.tvContenidoModulo.text = runCatching { block() }
                .getOrElse { "Sin datos. Conecta el backend o sincroniza desde Más." }
        }
    }

    companion object {
        const val EXTRA_TITLE = "title"
    }
}

package com.jeison.agrocontrol

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MasActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvContenidoModulo.text = "Perfil y sincronización\n\nToca sincronizar ahora."
        binding.tvContenidoModulo.setOnClickListener { syncNow() }
    }

    private fun syncNow() {
        val app = application as AgroControlApp
        if (!app.tokenStore.isLoggedIn()) {
            Toast.makeText(this, "Inicia sesión primero", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            app.syncManager.syncAll()
                .onSuccess { Toast.makeText(this@MasActivity, "Sincronización OK", Toast.LENGTH_SHORT).show() }
                .onFailure { Toast.makeText(this@MasActivity, "Sync falló", Toast.LENGTH_SHORT).show() }
        }
    }

    fun cerrarSesion() {
        (application as AgroControlApp).tokenStore.clear()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

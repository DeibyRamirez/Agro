package com.jeison.agrocontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jeison.agrocontrol.databinding.ActivityLoginBinding
import com.jeison.agrocontrol.network.LoginRequest
import com.jeison.agrocontrol.network.RegisterRequest
import com.jeison.agrocontrol.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as AgroControlApp
        if (app.tokenStore.isLoggedIn()) {
            irAlInicio()
            return
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun login() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            runCatching {
                val api = RetrofitClient.createApi((application as AgroControlApp).tokenStore)
                api.login(LoginRequest(email, password))
            }.onSuccess { tokens ->
                (application as AgroControlApp).tokenStore.saveTokens(tokens.accessToken, tokens.refreshToken)
                (application as AgroControlApp).syncManager.syncAll()
                irAlInicio()
            }.onFailure {
                Toast.makeText(this@LoginActivity, "Error de login: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun register() {
        lifecycleScope.launch {
            runCatching {
                val api = RetrofitClient.createApi((application as AgroControlApp).tokenStore)
                api.register(
                    RegisterRequest(
                        email = "nuevo${System.currentTimeMillis()}@agrocontrol.local",
                        password = "demo12345",
                        nombre = "Ganadero",
                        fincaNombre = "Mi Finca",
                    ),
                )
            }.onSuccess { tokens ->
                (application as AgroControlApp).tokenStore.saveTokens(tokens.accessToken, tokens.refreshToken)
                Toast.makeText(this@LoginActivity, "Cuenta creada", Toast.LENGTH_SHORT).show()
                irAlInicio()
            }.onFailure {
                Toast.makeText(this@LoginActivity, "Registro falló (¿API activa?)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun irAlInicio() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

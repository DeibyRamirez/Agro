package com.jeison.agrocontrol

class ReproduccionActivity : ModuleListActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvContenidoModulo.text =
            "Eventos de reproducción (inseminación, partos).\nConecta el backend para listar desde /reproduction."
    }
}

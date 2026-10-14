package com.jeison.agrocontrol

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jeison.agrocontrol.data.Animal
import com.jeison.agrocontrol.databinding.ActivityDetalleAnimalBinding

class DetalleAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnimalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalleAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val codigo = intent.getStringExtra(Animal.EXTRA_CODIGO) ?: "001"
        val nombre = intent.getStringExtra(Animal.EXTRA_NOMBRE) ?: ""
        val tipo = intent.getStringExtra(Animal.EXTRA_TIPO) ?: "Vaca"
        val raza = intent.getStringExtra(Animal.EXTRA_RAZA) ?: "Holstein"
        val sexo = intent.getStringExtra(Animal.EXTRA_SEXO) ?: "Hembra"
        val peso = intent.getDoubleExtra(Animal.EXTRA_PESO, 450.0)
        val fecha = intent.getStringExtra(Animal.EXTRA_FECHA) ?: "No registrada"
        val potrero = intent.getStringExtra(Animal.EXTRA_POTRERO) ?: "No asignado"
        val observacion =
            intent.getStringExtra(Animal.EXTRA_OBSERVACION) ?: "Sin observaciones"

        binding.tvNombreDetalle.text = nombre.ifBlank { "$tipo $codigo" }

        binding.tvCodigoDetalle.text = "Código: $codigo"
        binding.tvTipoDetalle.text = "Tipo: $tipo"
        binding.tvRazaDetalle.text = "Raza: $raza"
        binding.tvSexoDetalle.text = sexo
        binding.tvPesoDetalle.text = "Peso: $peso kg"
        binding.tvFechaDetalle.text = "Fecha de nacimiento: $fecha"
        binding.tvPotreroDetalle.text = "Potrero: $potrero"
        binding.tvObservacionDetalle.text = "Observaciones: $observacion"

        binding.btnVolverDetalle.setOnClickListener { finish() }

        binding.btnEditarAnimal.setOnClickListener {
            Toast.makeText(
                this,
                "Próximamente podrás editar el animal",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnProduccionDetalle.setOnClickListener {
            Toast.makeText(
                this,
                "Próximamente: producción de leche",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnReproduccionDetalle.setOnClickListener {
            Toast.makeText(
                this,
                "Próximamente: reproducción",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

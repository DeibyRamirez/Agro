package com.jeison.agrocontrol

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jeison.agrocontrol.data.Animal
import com.jeison.agrocontrol.databinding.ActivityAgregarAnimalBinding
import kotlinx.coroutines.launch

class AgregarAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarAnimalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAgregarAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarListas()

        binding.btnVolverAgregar.setOnClickListener { finish() }

        binding.btnGuardarAnimal.setOnClickListener {
            guardarAnimal()
        }
    }

    private fun configurarListas() {
        configurarSpinner(
            binding.spTipoAnimal,
            arrayOf("Seleccionar tipo", "Vaca", "Toro", "Ternero", "Ternera")
        )

        configurarSpinner(
            binding.spRazaAnimal,
            arrayOf(
                "Seleccionar raza",
                "Holstein",
                "Jersey",
                "Brahman",
                "Normando",
                "Pardo Suizo",
                "Criollo",
                "Otra"
            )
        )

        configurarSpinner(
            binding.spSexoAnimal,
            arrayOf("Seleccionar sexo", "Hembra", "Macho")
        )

        configurarSpinner(
            binding.spPotreroAnimal,
            arrayOf(
                "Seleccionar potrero",
                "Potrero 1",
                "Potrero 2",
                "Potrero 3"
            )
        )
    }

    private fun configurarSpinner(
        spinner: android.widget.Spinner,
        opciones: Array<String>
    ) {
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinner.adapter = adaptador
    }

    private fun guardarAnimal() {
        val codigo = binding.etCodigoAnimal.text.toString().trim()
        val nombre = binding.etNombreAnimal.text.toString().trim()
        val pesoTexto = binding.etPesoAnimal.text.toString().trim()
        val fecha = binding.etFechaNacimiento.text.toString().trim()
        val observacion = binding.etObservacionAnimal.text.toString().trim()

        val tipo = binding.spTipoAnimal.selectedItem.toString()
        val raza = binding.spRazaAnimal.selectedItem.toString()
        val sexo = binding.spSexoAnimal.selectedItem.toString()
        val potrero = binding.spPotreroAnimal.selectedItem.toString()

        if (codigo.isEmpty()) {
            binding.etCodigoAnimal.error = "Ingresa el código"
            return
        }

        if (tipo == "Seleccionar tipo") {
            mostrarMensaje("Selecciona el tipo de animal")
            return
        }

        if (raza == "Seleccionar raza") {
            mostrarMensaje("Selecciona la raza")
            return
        }

        if (sexo == "Seleccionar sexo") {
            mostrarMensaje("Selecciona el sexo")
            return
        }

        if (potrero == "Seleccionar potrero") {
            mostrarMensaje("Selecciona el potrero")
            return
        }

        val peso = pesoTexto.toDoubleOrNull()

        if (peso == null || peso <= 0) {
            binding.etPesoAnimal.error = "Ingresa un peso mayor que cero"
            return
        }

        if (fecha.isEmpty()) {
            binding.etFechaNacimiento.error = "Ingresa la fecha"
            return
        }

        val animal = Animal(
            codigo = codigo,
            nombre = nombre,
            tipo = tipo,
            raza = raza,
            sexo = sexo,
            peso = peso,
            fechaNacimiento = fecha,
            potrero = potrero,
            observacion = observacion,
            syncState = Animal.SyncState.PENDING.name,
        )

        val repository = (application as AgroControlApp).animalRepository
        lifecycleScope.launch {
            try {
                repository.insert(animal)
                setResult(Activity.RESULT_OK)
                mostrarMensaje("Animal guardado (pendiente de sync si aplica)")
                finish()
            } catch (_: Exception) {
                mostrarMensaje("Ya existe un animal con ese código")
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}

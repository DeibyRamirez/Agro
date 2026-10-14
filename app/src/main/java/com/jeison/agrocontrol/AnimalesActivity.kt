package com.jeison.agrocontrol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jeison.agrocontrol.data.Animal
import com.jeison.agrocontrol.databinding.ActivityAnimalesBinding
import com.jeison.agrocontrol.databinding.ItemAnimalCardBinding
import kotlinx.coroutines.launch

class AnimalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalesBinding
    private lateinit var repository: com.jeison.agrocontrol.data.AnimalRepository

    private var consultaBusqueda: String = ""

    private val agregarAnimal =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                cargarAnimales()
                Toast.makeText(this, "Animal agregado a la lista", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnimalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as AgroControlApp).animalRepository

        binding.btnAgregarAnimal.setOnClickListener {
            agregarAnimal.launch(Intent(this, AgregarAnimalActivity::class.java))
        }

        binding.etBuscarAnimal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                consultaBusqueda = s?.toString() ?: ""
                cargarAnimales()
            }
        })

        cargarAnimales()
    }

    override fun onResume() {
        super.onResume()
        cargarAnimales()
    }

    private fun cargarAnimales() {
        lifecycleScope.launch {
            val animales = repository.search(consultaBusqueda)
            mostrarAnimales(animales)
        }
    }

    private fun mostrarAnimales(animales: List<Animal>) {
        binding.listaAnimales.removeAllViews()

        if (animales.isEmpty()) {
            binding.tvListaVacia.visibility = View.VISIBLE
            return
        }

        binding.tvListaVacia.visibility = View.GONE
        val inflater = LayoutInflater.from(this)

        for (animal in animales) {
            val cardBinding = ItemAnimalCardBinding.inflate(inflater, binding.listaAnimales, false)
            cardBinding.tvTituloAnimal.text = animal.tituloDisplay()
            cardBinding.tvSexoChip.text = animal.sexo
            cardBinding.tvDetallesAnimal.text = animal.detalleLineas()
            cardBinding.cardAnimalRoot.setOnClickListener {
                abrirDetalle(animal)
            }
            binding.listaAnimales.addView(cardBinding.root)
        }
    }

    private fun abrirDetalle(animal: Animal) {
        val intent = Intent(this, DetalleAnimalActivity::class.java).apply {
            putExtra(Animal.EXTRA_CODIGO, animal.codigo)
            putExtra(Animal.EXTRA_NOMBRE, animal.nombre)
            putExtra(Animal.EXTRA_TIPO, animal.tipo)
            putExtra(Animal.EXTRA_RAZA, animal.raza)
            putExtra(Animal.EXTRA_SEXO, animal.sexo)
            putExtra(Animal.EXTRA_PESO, animal.peso)
            putExtra(Animal.EXTRA_FECHA, animal.fechaNacimiento)
            putExtra(Animal.EXTRA_POTRERO, animal.potrero)
            putExtra(Animal.EXTRA_OBSERVACION, animal.observacion)
        }
        startActivity(intent)
    }
}

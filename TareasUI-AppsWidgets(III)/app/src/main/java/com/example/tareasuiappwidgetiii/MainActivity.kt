package com.example.tareasuiappwidgetiii

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tareasuiappwidgetiii.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Se crea la clase de vinculación: "infla" el XML y crea todas las vistas
       val binding = ActivityMainBinding.inflate(layoutInflater)

        // 3. Se muestra la vista raíz de ese árbol de vistas
        setContentView(binding.main)

        // A partir de aquí, cada vista está disponible como binding.<id>
       binding.btnPregunta.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.titleAlerta)                         // "¿Vaciar el pedido?"
                .setMessage(R.string.mensajeAlerta)                      // "Se borrarán todos los datos…"
                .setNegativeButton(R.string.no_title, null)               // null: solo cierra el diálogo
                .setPositiveButton(R.string.si_title) { _, _ ->
                    binding.textPregunta.text="¡Genial! Sigue practicando con las vistas y los diálogos."
                }
                .show()
       }
    }
}
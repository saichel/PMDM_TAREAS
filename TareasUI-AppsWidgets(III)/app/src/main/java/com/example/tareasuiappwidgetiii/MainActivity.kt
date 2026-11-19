package com.example.tareasuiappwidgetiii

import android.os.Bundle
import android.widget.Toast
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
       binding.btnPregunta2.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.titleAlerta)                         // "¿Vaciar el pedido?"
                .setMessage(R.string.mensajeAlerta)                      // "Se borrarán todos los datos…"
                .setNegativeButton(R.string.no_title, null)               // null: solo cierra el diálogo
                .setPositiveButton(R.string.si_title) { _, _ ->
                    binding.textPregunta.text="¡Genial! Sigue practicando con las vistas y los diálogos."
                }
                .show()
       }
        binding.btnElementos.setOnClickListener {
            val seleccionados=booleanArrayOf(false,false,false,false)
            val elementos=resources.getStringArray(R.array.lista_elementos)
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_elementos)
                .setMultiChoiceItems(R.array.lista_elementos,seleccionados){_,which,isChecked->
                    seleccionados[which]=isChecked
                }
                .setPositiveButton("Aceptar"){dialog,_->
                    val elegidos=elementos.filterIndexed{index,_->seleccionados[index]}

                }
                .setNegativeButton("Cancelar"){dialog,_->dialog.dismiss()}
                .show()
        }
        binding.btnOpcion.setOnClickListener {
            var seleccionados=-1
            val elementos=resources.getStringArray(R.array.opciones_unicas)

            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_opcion)
                .setSingleChoiceItems(R.array.opciones_unicas,seleccionados){_,which->
                    seleccionados=which
                }
                .setPositiveButton("Aceptar"){dialog,_->
                    if(seleccionados !=-1){
                        val elegido=elementos[seleccionados]
                        Toast.makeText(this,"Has elegido:$elegido",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"No has seleccionado ninguna Pizza",Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Calcelar"){dialog,_->
                    dialog.dismiss()
                }
                .show()
        }
        binding.btnvariasOpciones.setOnClickListener {
            var seleccionados=booleanArrayOf(false,false,false,false,false)
            val elementos=resources.getStringArray(R.array.plataformas_lista)
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_opciones)
                .setMessage(R.string.message_opciones)
                .setMultiChoiceItems(R.array.plataformas_lista,seleccionados){_,which,isChecked->
                    seleccionados[which]=isChecked
                }
                .setPositiveButton("Aceptar"){dialog,_->
                    if(seleccionados.isNotEmpty()){
                        val PlataformasElegidas=elementos.filterIndexed { index, _ ->seleccionados[index]  }
                        Toast.makeText(this,"Has elegido ${PlataformasElegidas.size} Plataformas",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"No has seleccionado ninguna Plataforma",Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar"){dialog,_->dialog.dismiss()}
                .show()
        }
        //Ejercicio 3:5 Botones
        binding.btnRecordatorio.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_recordatorio)
                .setMessage(R.string.message_recordatorio)
                .setNeutralButton(R.string.mas_tarde){dialog,_->
                    Toast.makeText(this,"Recordatorio pospuesto",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.aceptar){dialog,_->dialog.dismiss()}
                .setPositiveButton(R.string.positive){_,_->
                    Toast.makeText(this,"Actualizando aplicacion....",Toast.LENGTH_SHORT).show()
                }
                .show()
        }
        binding.btnNombre.setOnClickListener {
        val vistaDialogo=layoutInflater.inflate(R.layout.dialogo_nombre,null)
            val etNombre=vistaDialogo.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombre)
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_Nombre)
                .setView(vistaDialogo)
                .setNegativeButton("Cancelar"){dialog,_->
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar"){
                    dialog,_->dialog.dismiss()
                }
                .setPositiveButton("Aceptar") { _, _ ->
                    val nombre = etNombre.text.toString()
                    if (nombre.isNotEmpty()) {
                        binding.textPregunta.text = "hola,$nombre"
                    }
                }.show()
        }
        binding.btnBorrar.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("¿Borrar la respuesta?")
                .setMessage("Se borrará el texto que aparece en la parte superior.")
                .setNegativeButton("Cancelar") { dialog,_ ->
                     dialog.dismiss()
                }
            .setPositiveButton("Borrar") { _, _ ->
                binding.textPregunta.text = ""
                Toast.makeText(this, "Respuesta borrada", Toast.LENGTH_SHORT).show()
            }
            .show()
        }
        binding.btnFecha.setOnClickListener {
            val calendario = java.util.Calendar.getInstance()
            val anioActual = calendario.get(java.util.Calendar.YEAR)
            val mesActual = calendario.get(java.util.Calendar.MONTH)
            val diaActual = calendario.get(java.util.Calendar.DAY_OF_MONTH)

            val datePicker = android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Nota: 'month' empieza en 0 (enero = 0), por eso se suma 1
                    val fechaElegida = "$dayOfMonth/${month + 1}/$year"
                    binding.textPregunta.text = "Fecha: $fechaElegida"
                },
                anioActual,
                mesActual,
                diaActual
            )
            datePicker.show()
        }
        binding.btnHora.setOnClickListener {
            val calendario = java.util.Calendar.getInstance()
            val horaActual = calendario.get(java.util.Calendar.HOUR_OF_DAY)
            val minutoActual = calendario.get(java.util.Calendar.MINUTE)

            val timePicker = android.app.TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // String.format("%02d:%02d") asegura que salga "10:05" en lugar de "10:5"
                    val horaElegida = String.format("%02d:%02d", hourOfDay, minute)
                    binding.textPregunta.text = "Hora: $horaElegida"
                },
                horaActual,
                minutoActual,
                false // 'false' muestra el reloj en formato 12h AM/PM como en la captura; pon 'true' para 24h[cite: 7]
            )
            timePicker.show()
        }


    }

}
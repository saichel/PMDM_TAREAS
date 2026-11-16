package com.venanciob.dam.pizzeria

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.venanciob.dam.pizzeria.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PRECIO_PEQUENA = 8.0
        private const val PRECIO_MEDIANA = 10.5
        private const val PRECIO_FAMILIAR = 14.0
        private const val PRECIO_EXTRA = 1.5
        private const val PRECIO_ENVIO = 3.0

        // Constantes para las notificaciones
        private const val CHANNEL_ID = "canal_pedidos_pizzeria"
        private const val NOTIFICATION_ID = 101
    }

    private lateinit var binding: ActivityMainBinding

    // 2. Lanzador para solicitar el permiso en tiempo de ejecución (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso denegado: no se mostrarán avisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar canal y comprobar permisos al arrancar
        crearCanalNotificacion()
        pedirPermisoNotificaciones()

        actualizarTextoCantidad(binding.sbCantidad.progress)
        configurarListeners()
    }

    // 1. CREAR EL CANAL DE NOTIFICACIONES (Android 8.0 / API 26+)
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Pedidos de Pizzería"
            val descripcion = "Notificaciones de confirmación y estado del pedido"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    // 2. PEDIR PERMISO PARA NOTIFICAR (Android 13 / API 33+)
    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // 3. CREAR Y MOSTRAR LA NOTIFICACIÓN
    @SuppressLint("MissingPermission")
    private fun mostrarNotificacionPedido(nombreCliente: String, total: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val textoNotificacion = String.format(
            Locale.getDefault(),
            "¡Gracias %s! Tu pedido por %.2f € está en marcha.",
            nombreCliente,
            total
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.happy)
            .setContentTitle("Pedido Confirmado 🍕")
            .setContentText(textoNotificacion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }

    private fun configurarListeners() {
        binding.etNombre.doAfterTextChanged { binding.tilNombre.error = null }

        binding.sbCantidad.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                actualizarTextoCantidad(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnCalcular.setOnClickListener {
            calcularPedido()
        }
    }

    private fun actualizarTextoCantidad(progress: Int) {
        binding.tvCantidad.text = getString(R.string.cantidad_valor, progress + 1)
    }

    private fun calcularPedido() {
        with(binding) {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isEmpty()) {
                tilNombre.error = getString(R.string.error_nombre)
                etNombre.requestFocus()
                Toast.makeText(this@MainActivity, R.string.error_nombre_vacio, Toast.LENGTH_SHORT).show()
                return
            }

            val precioBase = when (rgTamano.checkedRadioButtonId) {
                R.id.rbPequena -> PRECIO_PEQUENA
                R.id.rbFamiliar -> PRECIO_FAMILIAR
                else -> PRECIO_MEDIANA
            }
            val tamano = root.findViewById<RadioButton>(rgTamano.checkedRadioButtonId).text.toString()
            val masa = spMasa.selectedItem.toString()
            val cantidad = sbCantidad.progress + 1

            val extras = listOf(cbQueso, cbChampinones, cbJamon)
                .filter { it.isChecked }
                .map { it.text.toString() }

            val precioUnidad = precioBase + extras.size * PRECIO_EXTRA
            var total = precioUnidad * cantidad
            if (swEnvio.isChecked) total += PRECIO_ENVIO

            val textoExtras = if (extras.isEmpty()) getString(R.string.sin_extras)
            else extras.joinToString(", ")

            tvResumen.text = getString(
                R.string.resumen_pedido,
                nombre, cantidad, tamano.lowercase(), masa.lowercase(), textoExtras, total
            )

            Toast.makeText(this@MainActivity, R.string.guardado_ok, Toast.LENGTH_SHORT).show()

            mostrarNotificacionPedido(nombre, total)
        }
    }
}
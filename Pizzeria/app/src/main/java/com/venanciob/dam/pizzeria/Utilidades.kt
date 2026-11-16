package com.venanciob.dam.pizzeria

import android.content.Context
import android.widget.Toast

// Utilidades.kt
fun Context.toast(mensaje: String, duracion: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, mensaje, duracion).show()
}

package com.app.tea.aprendejugando.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.sqrt

object SaludoDetector {

    data class ResultadoColor(val nombre: String, val r: Int, val g: Int, val b: Int)

    private val coloresReferencia = listOf(
        ResultadoColor("HolaAdios_Negro", 30, 30, 30),
        ResultadoColor("BuenosDias_Morado", 170, 90, 190),
        ResultadoColor("BuenasTardes_Naranja", 255, 125, 40),
        ResultadoColor("BuenasTardes_Ocre", 230, 160, 60),
        ResultadoColor("BuenasNoches_AzulProfundo", 25, 60, 130),
        ResultadoColor("BuenasNoches_Cortina", 45, 90, 170),
        ResultadoColor("DarMano_Verde", 90, 150, 60),
        ResultadoColor("DarMano_Marron", 110, 70, 40),
        ResultadoColor("Abrazar_Celeste", 50, 160, 200),
        ResultadoColor("Abrazar_Naranja", 230, 140, 80)
    )

    private val coloresPorSaludo = mapOf(
        "Hola o Adiós" to listOf("HolaAdios_Negro"),
        "Buenos días" to listOf("BuenosDias_Morado"),
        "Buenas tardes" to listOf("BuenasTardes_Naranja", "BuenasTardes_Ocre"),
        "Buenas noches" to listOf("BuenasNoches_AzulProfundo", "BuenasNoches_Cortina"),
        "Dar la mano" to listOf("DarMano_Verde", "DarMano_Marron"),
        "Abrazar" to listOf("Abrazar_Celeste", "Abrazar_Naranja")
    )

    fun detectarSaludo(bitmap: Bitmap): String {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val sampleSize = 10
        val contadorColores = mutableMapOf<String, Int>()

        for (x in centerX - sampleSize until centerX + sampleSize step 2) {
            for (y in centerY - sampleSize until centerY + sampleSize step 2) {
                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    val pixel = bitmap.getPixel(x, y)
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)

                    val colorCercano = coloresReferencia.minByOrNull {
                        val dr = it.r - r
                        val dg = it.g - g
                        val db = it.b - b
                        sqrt((dr * dr + dg * dg + db * db).toDouble())
                    }

                    colorCercano?.let {
                        contadorColores[it.nombre] = contadorColores.getOrDefault(it.nombre, 0) + 1 + 1
                    }
                }
            }
        }

        Log.d("SALUDO_DEBUG", "🎯 Colores detectados: $contadorColores")

        val saludos = mutableMapOf<String, Int>()

        coloresPorSaludo.forEach { (saludo, claves) ->
            val puntaje = claves.sumOf { clave ->
                if (clave.contains("+")) {
                    val partes = clave.split("+")
                    if (partes.all { contadorColores.getOrDefault(it, 0) >= 2 }) 10 else 0
                } else {
                    contadorColores.getOrDefault(clave, 0)
                }
            }
            saludos[saludo] = puntaje
        }

        val mejorSaludo = saludos.maxByOrNull { it.value }

        return if (mejorSaludo != null && mejorSaludo.value >= 5) {
            mejorSaludo.key
        } else {
            "Saludo desconocido"
        }

    }
}






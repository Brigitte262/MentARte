package com.app.tea.aprendejugando.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

object EmocionDetector {

    data class ResultadoColor(val nombre: String, val r: Int, val g: Int, val b: Int)

    // 🎨 Colores representativos para cada emoción
    private val coloresReferencia = listOf(
        ResultadoColor("Feliz", 254, 200, 50),          // Más brillante (amarillo)
        ResultadoColor("Triste", 80, 140, 200),         // Azul más oscuro
        ResultadoColor("Enojado", 180, 40, 40),         // Rojo más oscuro
        ResultadoColor("Neutro", 170, 170, 170),        // Igual
        ResultadoColor("Sorprendido", 250, 120, 70),    // Más saturado para distanciarlo del rojo
        ResultadoColor("Asustado", 90, 200, 160),       // Más verdoso para distinguirlo del azul
        ResultadoColor("Amoroso", 255, 160, 210) // antes: (255, 150, 190)
        // Igual (rosado)
    )


    fun detectarColorDominante(bitmap: Bitmap): ResultadoColor {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val sampleSize = 30

        var totalR = 0
        var totalG = 0
        var totalB = 0
        var count = 0

        for (x in centerX - sampleSize until centerX + sampleSize) {
            for (y in centerY - sampleSize until centerY + sampleSize) {
                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    val pixel = bitmap.getPixel(x, y)
                    totalR += Color.red(pixel)
                    totalG += Color.green(pixel)
                    totalB += Color.blue(pixel)
                    count++
                }
            }
        }

        if (count == 0) return ResultadoColor("Desconocido", 0, 0, 0)

        val avgR = totalR / count
        val avgG = totalG / count
        val avgB = totalB / count

        val colorDetectado = coloresReferencia.minByOrNull {
            val isRojoFuerte = it.nombre == "Enojado" || it.nombre == "Sorprendido"
            val dr = if (isRojoFuerte) (it.r - avgR) * 1.2 else (it.r - avgR).toDouble()
            val dg = (it.g - avgG).toDouble()
            val db = (it.b - avgB).toDouble()
            Math.sqrt((dr * dr + dg * dg + db * db))
        }


        return colorDetectado ?: ResultadoColor("Desconocido", avgR, avgG, avgB)
    }


    fun detectarEmocion(bitmap: Bitmap): String {
        val color = detectarColorDominante(bitmap)

        Log.d("EMOCION_DEBUG", "🎨 Color detectado: ${color.nombre} (R:${color.r}, G:${color.g}, B:${color.b})")

        return color.nombre
    }
}

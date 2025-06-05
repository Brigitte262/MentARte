package com.app.tea.aprendejugando.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

object EmocionDetector {

    data class ResultadoColor(val nombre: String, val r: Int, val g: Int, val b: Int)

    // 🎨 Colores representativos para cada emoción
    private val coloresReferencia = listOf(
        ResultadoColor("Feliz", 254, 176, 47),          // Amarillo
        ResultadoColor("Triste", 100, 160, 230),        // Azul claro
        ResultadoColor("Enojado", 220, 60, 60),         // Rojo fuerte
        ResultadoColor("Neutro", 170, 170, 170),        // Gris claro
        ResultadoColor("Sorprendido", 200, 100, 60),    // Naranja
        ResultadoColor("Asustado", 100, 170, 130),      // celeste pálido
        ResultadoColor("Amoroso", 255, 150, 190)        // Rosa
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
            val dr = it.r - avgR
            val dg = it.g - avgG
            val db = it.b - avgB
            Math.sqrt((dr * dr + dg * dg + db * db).toDouble())
        }

        return colorDetectado ?: ResultadoColor("Desconocido", avgR, avgG, avgB)
    }

    fun detectarEmocion(bitmap: Bitmap): String {
        val color = detectarColorDominante(bitmap)

        Log.d("EMOCION_DEBUG", "🎨 Color detectado: ${color.nombre} (R:${color.r}, G:${color.g}, B:${color.b})")

        return color.nombre
    }
}

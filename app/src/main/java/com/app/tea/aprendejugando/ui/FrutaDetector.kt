package com.app.tea.aprendejugando.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object FrutaDetector {

    data class ResultadoColor(val nombre: String, val r: Int, val g: Int, val b: Int)

    private val coloresReferencia = listOf(
        ResultadoColor("Rojo", 200, 40, 40),
        ResultadoColor("Verde", 140, 180, 50),
        ResultadoColor("Amarillo", 255, 220, 100),
        ResultadoColor("Amarillo", 230, 200, 70),
        ResultadoColor("Amarillo", 255, 235, 130),
        ResultadoColor("Naranja", 200, 120, 60), // Mandarina real
        ResultadoColor("Marrón", 90, 60, 30),
        ResultadoColor("Marrón", 80, 50, 20),
        ResultadoColor("Azul oscuro", 45, 60, 100),
        ResultadoColor("Violeta oscuro", 60, 40, 80),
        ResultadoColor("Beige", 230, 225, 200),
        ResultadoColor("Beige", 210, 200, 170),     // Melón (ajustado desde imagen)
        ResultadoColor("Beige", 190, 185, 150)    // Otra variación de melón más pálido
    )

    fun detectarColorDominante(bitmap: Bitmap): ResultadoColor {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val sampleSize = 10

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
            val dr = (it.r - avgR).toDouble()
            val dg = (it.g - avgG).toDouble()
            val db = (it.b - avgB).toDouble()
            Math.sqrt(dr * dr + dg * dg + db * db)
        }

        return colorDetectado ?: ResultadoColor("Desconocido", avgR, avgG, avgB)
    }

    fun detectarFormaAproximada(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height

        var minX = width
        var maxX = 0
        var minY = height
        var maxY = 0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val alpha = Color.alpha(pixel)
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                if (alpha > 100 && brightness > 40) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        val ancho = maxX - minX
        val alto = maxY - minY
        if (ancho <= 0 || alto <= 0) return "Desconocida"

        val ratio = ancho.toFloat() / alto.toFloat()

        return when {
            ratio in 0.75..1.3 -> "Circular"
            ratio < 0.6 -> "Vertical"
            ratio > 1.6 -> "Alargado"
            ratio in 1.3..1.6 -> "Ovalado"
            else -> "Irregular"
        }
    }

    fun tieneSuficienteContenido(bitmap: Bitmap): Boolean {
        var pixelesValidos = 0
        val totalPixeles = bitmap.width * bitmap.height

        for (x in 0 until bitmap.width step 5) {
            for (y in 0 until bitmap.height step 5) {
                val pixel = bitmap.getPixel(x, y)
                val alpha = Color.alpha(pixel)
                val hsv = FloatArray(3)
                Color.colorToHSV(pixel, hsv)
                val saturacion = hsv[1]
                val brillo = hsv[2]

                if (alpha > 100 && brillo > 0.2f && saturacion > 0.25f) {
                    pixelesValidos++
                }
            }
        }

        val porcentaje = pixelesValidos.toFloat() / (totalPixeles / 25)
        return porcentaje > 0.1f
    }

    fun detectarFruta(bitmap: Bitmap): String {
        if (!tieneSuficienteContenido(bitmap)) return "Fruta desconocida"

        val color = detectarColorDominante(bitmap)
        val forma = detectarFormaAproximada(bitmap)

        val frutas = listOf(
            Triple("Manzana", listOf("Rojo"), listOf("Circular", "Ovalado")),
            Triple("Plátano", listOf("Amarillo"), listOf("Alargado", "Ovalado")),
            Triple("Uva", listOf("Verde"), listOf("Circular", "Ovalado")),
            Triple("Mandarina", listOf("Naranja"), listOf("Circular", "Ovalado")),
            Triple("Melón", listOf("Beige"), listOf("Circular", "Ovalado")),
            Triple("Kiwi", listOf("Marrón"), listOf("Ovalado")),
            Triple("Arándano", listOf("Azul oscuro", "Violeta oscuro"), listOf("Circular"))
        )

        var mejorFruta = "Fruta desconocida"
        var mejorPuntaje = 0

        for ((nombre, colores, formas) in frutas) {
            var puntaje = 0
            if (colores.contains(color.nombre)) puntaje += 3
            if (formas.contains(forma)) puntaje += 1
            if (color.nombre == "Naranja" && nombre == "Kiwi") puntaje -= 2 // penaliza confusión
            if (puntaje > mejorPuntaje) {
                mejorFruta = nombre
                mejorPuntaje = puntaje
            }
        }

        return if (mejorPuntaje >= 2) mejorFruta else "Fruta desconocida"
    }
}







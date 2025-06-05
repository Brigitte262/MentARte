package com.app.tea.aprendejugando.ui
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.sqrt


object VerduraDetector {

    data class ResultadoColor(val nombre: String, val r: Int, val g: Int, val b: Int)

    private val coloresReferencia = listOf(
        ResultadoColor("Rojo tomate", 211, 74, 64),
        ResultadoColor("Naranja zanahoria", 238, 140, 72),
        ResultadoColor("Amarillo calabaza", 250, 215, 135),
        ResultadoColor("Marrón papa", 205, 170, 100),
        ResultadoColor("Verde", 140, 180, 60),
        ResultadoColor("Violeta", 90, 70, 120),
        ResultadoColor("Morado oscuro", 60, 40, 90),
        ResultadoColor("Blanco coliflor", 230, 225, 210)
    )



    private val verduras = listOf(
        Triple("Tomate", listOf("Rojo tomate"), listOf("Circular", "Ovalado")),
        Triple("Zanahoria", listOf("Naranja zanahoria"), listOf("Alargado", "Vertical")),
        Triple("Calabaza", listOf("Amarillo calabaza"), listOf("Circular", "Ovalado")),
        Triple("Papa", listOf("Marrón papa"), listOf("Ovalado", "Irregular")),
        Triple("Lechuga", listOf("Verde"), listOf("Irregular")),
        Triple("Berenjena", listOf("Violeta","Morado oscuro"), listOf("Alargado", "Ovalado")),
        Triple("Coliflor", listOf("Blanco coliflor"), listOf("Irregular", "Circular"))

    )

    fun detectarColorDominante(bitmap: Bitmap): ResultadoColor {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val sampleSize = 12

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

        return coloresReferencia.minByOrNull {
            val dr = it.r - avgR
            val dg = it.g - avgG
            val db = it.b - avgB
            Math.sqrt((dr * dr + dg * dg + db * db).toDouble())
        } ?: ResultadoColor("Desconocido", avgR, avgG, avgB)
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
                    minX = minOf(minX, x)
                    maxX = maxOf(maxX, x)
                    minY = minOf(minY, y)
                    maxY = maxOf(maxY, y)
                }
            }
        }

        val ancho = maxX - minX
        val alto = maxY - minY
        if (ancho <= 0 || alto <= 0) return "Desconocida"

        val ratio = ancho.toFloat() / alto.toFloat()

        return when {
            ratio in 0.8..1.25 -> "Circular"
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

    fun detectarVerdura(bitmap: Bitmap): String {
        if (!tieneSuficienteContenido(bitmap)) {
            Log.d("VERDURA_DEBUG", "❌ Imagen con poco contenido útil.")
            return "Verdura desconocida"
        }

        val colorDetectado = detectarColorDominante(bitmap)
        val formaDetectada = detectarFormaAproximada(bitmap)

        Log.d("VERDURA_DEBUG", "🎨 Color: ${colorDetectado.nombre} (R:${colorDetectado.r}, G:${colorDetectado.g}, B:${colorDetectado.b})")
        Log.d("VERDURA_DEBUG", "🧩 Forma: $formaDetectada")

        var mejorVerdura = "Verdura desconocida"
        var mejorPuntaje = 0

        for ((nombre, coloresEsperados, formasEsperadas) in verduras) {
            var puntaje = 0

            if (coloresEsperados.contains(colorDetectado.nombre)) puntaje += 3
            if (formasEsperadas.contains(formaDetectada)) puntaje += 1

            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje
                mejorVerdura = nombre
            }
        }

        return if (mejorPuntaje >= 2) mejorVerdura else "Verdura desconocida"
    }
}






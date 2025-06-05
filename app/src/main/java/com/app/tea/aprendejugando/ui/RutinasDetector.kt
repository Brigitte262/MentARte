package com.app.tea.aprendejugando.ui

import android.graphics.Bitmap

object RutinasDetector {

    data class RutinaRangoHSV(
        val nombre: String,
        val hueMin: Float,
        val hueMax: Float,
        val satMin: Float,
        val satMax: Float
    )

    // Definimos los rangos HSV para cada rutina
    private val rutinas = listOf(
        RutinaRangoHSV("Levantarse", 210f, 240f, 0.3f, 1f),        // Azul
        RutinaRangoHSV("Lavarse los dientes", 40f, 60f, 0.4f, 1f), // Amarillo
        RutinaRangoHSV("Vestirse", 70f, 100f, 0.3f, 1f),          // Verde
        RutinaRangoHSV("Desayunar", 0f, 40f, 0f, 0.2f),            // Blanco (baja saturación)
        RutinaRangoHSV("Almorzar", 330f, 360f, 0.3f, 0.8f),        // Rosado fuerte
        RutinaRangoHSV("Cenar", 0f, 15f, 0.6f, 1f),                // Rojo puro
        RutinaRangoHSV("Ir al colegio", 180f, 220f, 0f, 0.2f),     // Gris (casi sin saturación)
        RutinaRangoHSV("Estudiar", 20f, 50f, 0.4f, 1f),            // Marrón
        RutinaRangoHSV("Bañarse", 180f, 195f, 0.3f, 0.9f),         // Celeste
        RutinaRangoHSV("Lavarse las manos", 260f, 290f, 0.4f, 1f)  // Violeta fuerte
    )

    fun detectarRutina(bitmap: Bitmap): String {
        val hsvSum = FloatArray(3)
        var count = 0

        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val region = 10  // 10x10 px región

        for (x in centerX - region / 2 until centerX + region / 2) {
            for (y in centerY - region / 2 until centerY + region / 2) {
                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    val color = bitmap.getPixel(x, y)
                    val hsv = FloatArray(3)
                    android.graphics.Color.colorToHSV(color, hsv)
                    hsvSum[0] += hsv[0]
                    hsvSum[1] += hsv[1]
                    hsvSum[2] += hsv[2]
                    count++
                }
            }
        }

        if (count == 0) return "Rutina no reconocida"

        val avgHue = hsvSum[0] / count
        val avgSat = hsvSum[1] / count
        // val avgVal = hsvSum[2] / count (opcional si deseas comparar también brillo)

        for (rutina in rutinas) {
            val hue = if (rutina.hueMin > rutina.hueMax) {
                // caso circular como 350° a 10°
                avgHue >= rutina.hueMin || avgHue <= rutina.hueMax
            } else {
                avgHue in rutina.hueMin..rutina.hueMax
            }

            val sat = avgSat in rutina.satMin..rutina.satMax

            if (hue && sat) return rutina.nombre
        }

        return "Rutina no reconocida"
    }
}


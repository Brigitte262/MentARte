package com.app.tea.aprendejugando.ui

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

class ColorAnalyzer(
    private val onColorDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap() ?: run {
            imageProxy.close()
            return
        }

        val colorName = detectarColorPrincipal(bitmap)
        onColorDetected(colorName)
        imageProxy.close()
    }

    private fun detectarColorPrincipal(bitmap: Bitmap): String {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val pixel = bitmap.getPixel(centerX, centerY)

        val hsv = FloatArray(3)
        Color.colorToHSV(pixel, hsv)

        val h = hsv[0]
        val s = hsv[1]
        val v = hsv[2]

        // 🌑 Si es sombra o zona neutra sin saturación, evitamos detectar
        if ((v < 0.12f && s < 0.3f) || (s < 0.12f && v > 0.85f)) {
            return "sin color claro"
        }

        return when {
            // ⚫ Negro
            v < 0.28f && s < 0.32f -> "negro"

            // ⚪ Blanco
            s < 0.10f && v > 0.92f -> "blanco"

            // ⚪ Gris
            s < 0.25f && v in 0.28f..0.92f -> "gris"

            // 🟫 Marrón
            h in 20f..45f && s > 0.4f && v < 0.6f -> "marron"

            // 🔴 Rojo
            h < 10f || h >= 350f -> "rojo"

            // 🌸 Rosado claro y extendido
            h in 10f..35f -> if (s < 0.6f && v > 0.7f) "rosado" else "naranja"

            // 🟡 Amarillo
            h in 35f..65f && s > 0.4f && v > 0.5f -> "amarillo"

            // 🟢 Verde
            h in 70f..170f -> "verde"

            // 🔵 Celeste (azul claro)
            h in 170f..200f -> "celeste"

            // 🔵 Azul profundo
            h in 200f..250f -> "azul"

            // 🟣 Morado o violeta
            h in 250f..290f -> "morado"

            // 🌸 Fucsia o rosado intenso
            h in 290f..345f -> "rosado"

            else -> "color desconocido"
        }

    }


    private fun ImageProxy.toBitmap(): Bitmap? {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}

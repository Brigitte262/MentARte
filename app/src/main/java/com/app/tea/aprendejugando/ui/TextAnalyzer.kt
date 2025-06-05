package com.app.tea.aprendejugando.ui

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TextAnalyzer(
    private val context: Context,
    private val onVocalDetectada: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())
    private var puedeDetectar = true

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                val texto = result.text.trim().uppercase(Locale.ROOT)
                Log.d("OCR", "🔍 Texto detectado: $texto")

                // Si no hay texto visible, limpia
                // Limpia texto para evitar errores como "AA", "1A", "Á", etc.
                val textoLimpio = texto.replace("[^A-Z]".toRegex(), "") // Solo letras mayúsculas

                // Si ya no hay vocales válidas, limpiar pantalla
                if (textoLimpio.isBlank() || !textoLimpio.any { it in "AEIOU" }) {
                    onVocalDetectada("") // limpia si ya no hay vocal clara
                    imageProxy.close()
                    return@addOnSuccessListener
                }

                // Buscar la primera vocal válida
                val vocalDetectada = texto.firstOrNull { it in "AEIOU" }

                if (puedeDetectar && vocalDetectada != null) {
                    puedeDetectar = false
                    onVocalDetectada(vocalDetectada.toString())

                    // Esperar 2 segundos antes de permitir otra detección
                    GlobalScope.launch {
                        delay(2000)
                        puedeDetectar = true
                    }
                }

                imageProxy.close()
            }

            .addOnFailureListener { e ->
                Log.e("OCR", "❌ Error en reconocimiento", e)
                imageProxy.close()
            }
    }
}





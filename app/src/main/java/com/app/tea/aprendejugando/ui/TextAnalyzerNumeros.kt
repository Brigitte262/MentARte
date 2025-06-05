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

class TextAnalyzerNumeros(
    private val context: Context,
    private val onNumeroDetectado: (String) -> Unit
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
                val bloques = result.textBlocks
                var numeroDetectado: Int? = null

                for (block in bloques) {
                    val texto = block.text.trim().replace("[^0-9]".toRegex(), "")

                    if (texto.matches(Regex("^\\d{1,3}$"))) {
                        val posibleNumero = texto.toIntOrNull()
                        if (posibleNumero != null && posibleNumero in 0..100) {
                            numeroDetectado = posibleNumero
                            break
                        }
                    }
                }

                if (puedeDetectar && numeroDetectado != null) {
                    puedeDetectar = false
                    onNumeroDetectado(numeroDetectado.toString())

                    GlobalScope.launch {
                        delay(2000)
                        puedeDetectar = true
                    }
                } else if (numeroDetectado == null) {
                    onNumeroDetectado("") // limpia si no hay un número válido
                }

                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "❌ Error en OCR", e)
                imageProxy.close()
            }
    }
}


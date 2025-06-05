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

class TextAnalyzerAbecedario(
    private val context: Context,
    private val onLetraDetectada: (String) -> Unit
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
                Log.d("OCR", "🔤 Texto detectado: $texto")

                // Solo letras mayúsculas A-Z
                val textoLimpio = texto.replace("[^A-Z]".toRegex(), "")

                if (textoLimpio.isBlank()) {
                    onLetraDetectada("") // limpia pantalla si ya no se ve texto
                    imageProxy.close()
                    return@addOnSuccessListener
                }

                // Detectar la primera letra válida
                val letraDetectada = textoLimpio.firstOrNull()

                if (puedeDetectar && letraDetectada != null) {
                    puedeDetectar = false
                    onLetraDetectada(letraDetectada.toString())

                    GlobalScope.launch {
                        delay(2000)
                        puedeDetectar = true
                    }
                }

                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "❌ Error en OCR", e)
                imageProxy.close()
            }
    }
}

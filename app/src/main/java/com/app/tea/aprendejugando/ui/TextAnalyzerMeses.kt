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

class TextAnalyzerMeses(
    private val context: Context,
    private val onMesDetectado: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())
    private var puedeDetectar = true

    // Lista de meses en mayúsculas
    private val meses = listOf(
        "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
        "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
    )

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
                Log.d("OCR-MES", "📷 Texto detectado: $texto")

                // Buscar si contiene un mes
                val mesDetectado = meses.find { texto.contains(it) }

                if (puedeDetectar && mesDetectado != null) {
                    puedeDetectar = false
                    onMesDetectado(mesDetectado)

                    GlobalScope.launch {
                        delay(2000)
                        puedeDetectar = true
                    }
                }

                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("OCR-MES", "❌ Error en OCR", e)
                imageProxy.close()
            }
    }
}

package com.app.tea.aprendejugando.ui
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.app.tea.aprendejugando.R
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.concurrent.Executors
import android.graphics.Color
import android.util.Size
import android.widget.FrameLayout

class RutinaActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var textoResultado: TextView
    private lateinit var webView: WebView
    private lateinit var tts: TextToSpeech
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var ultimaDeteccion = ""
    private var isTTSReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        previewView = findViewById(R.id.previewView)
        textoResultado = findViewById(R.id.textoResultado)
        webView = findViewById(R.id.webviewModelo)

        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale("es", "ES")
                isTTSReady = true
            }
        }

        val textoEncabezado = findViewById<TextView>(R.id.textoEncabezado)
        textoEncabezado.text = "¡Las Rutinas Diarias!"

        iniciarCamara()

        findViewById<FrameLayout>(R.id.botonRegresar).setOnClickListener {
            finish()
        }
    }

    private fun iniciarCamara() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(224, 224))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val bitmap = imageProxy.toBitmap()
                        if (bitmap != null) {
                            val rutinaDetectada = RutinasDetector.detectarRutina(bitmap)

                            runOnUiThread {
                                val frasesParaRutina = mapOf(
                                    "Levantarse" to "¡Hora de despertar y comenzar el día!",
                                    "Lavarse los dientes" to "¡Vamos a lavarnos los dientes y no olvides lavarte la cara también!",
                                    "Vestirse" to "¡Vamos a ponernos la ropa!",
                                    "Desayunar" to "¡A desayunar algo rico!",
                                    "Almorzar" to "Es hora del almuerzo.",
                                    "Cenar" to "Vamos a cenar antes de dormir.",
                                    "Ir al colegio" to "¡Hora de ir al colegio!",
                                    "Estudiar" to "¡A estudiar con energía!",
                                    "Bañarse" to "¡Vamos a darnos un baño!",
                                    "Lavarse las manos" to "¡Lávate las manos con agua y jabón!",
                                )

                                if (rutinaDetectada != "Rutina no reconocida") {
                                    // Mostrar texto profesional
                                    textoResultado.text = "🧩 Rutina detectada: $rutinaDetectada"
                                    textoResultado.invalidate()

                                    // Solo hablar si es una nueva detección
                                    if (rutinaDetectada != ultimaDeteccion) {
                                        ultimaDeteccion = rutinaDetectada
                                        val frase = frasesParaRutina[rutinaDetectada] ?: "¡Es $rutinaDetectada!"
                                        if (isTTSReady) {
                                            tts.speak(frase, TextToSpeech.QUEUE_FLUSH, null, null)
                                        }

                                        mostrarModelo3D(rutinaDetectada)
                                    }
                                } else {
                                    textoResultado.text = "📷 Esperando una tarjeta..."
                                    textoResultado.invalidate()
                                }
                            }
                        }

                        imageProxy.close()
                    }
                }


            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun mostrarModelo3D(nombre: String) {
        val nombreLimpio = nombre
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace(" ", "_") // Ejemplo: jugar → jugar

        val timestamp = System.currentTimeMillis()
        webView.loadUrl("file:///android_asset/rutina_modelo.html?rutina=$nombreLimpio&ts=$timestamp")
        webView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
    }
}

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


private lateinit var tts: TextToSpeech
private lateinit var debugHSV: TextView

class SaludoActivity : AppCompatActivity() {
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
        textoEncabezado.text = "¡Los Saludos!"

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
                            val saludoDetectado = SaludoDetector.detectarSaludo(bitmap)

                            if (saludoDetectado != ultimaDeteccion && saludoDetectado != "Saludo desconocido") {
                                ultimaDeteccion = saludoDetectado

                                runOnUiThread {
                                    textoResultado.text = "¡Detectamos: $saludoDetectado!"
                                    val frasesParaSaludo = mapOf(
                                        "Hola o Adiós" to "¡Vamos a saludar diciendo Hola o Adiós!",
                                        "Buenos días" to "¡Es de día! Decimos: Buenos días",
                                        "Buenas tardes" to "¡Es la tarde! Decimos: Buenas tardes",
                                        "Buenas noches" to "Ya es hora de dormir. ¡Decimos buenas noches!",
                                        "Dar la mano" to "Nos damos la mano cuando saludamos a alguien.",
                                        "Abrazar" to "Un abrazo es una forma de demostrar cariño."
                                    )

                                    val fraseAmable = frasesParaSaludo[saludoDetectado] ?: "¡Es $saludoDetectado!"
                                    if (isTTSReady) {
                                        tts.speak(fraseAmable, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                    mostrarModelo3D(saludoDetectado)
                                }
                            } else if (saludoDetectado == "Saludo desconocido") {
                                runOnUiThread {
                                    textoResultado.text = "Esperando una tarjeta..."
                                    webView.visibility = View.GONE
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
            .replace(" ", "_") // Ejemplo: hola o adios → hola_o_adios

        val timestamp = System.currentTimeMillis()
        webView.loadUrl("file:///android_asset/saludo_modelo.html?saludo=$nombreLimpio&ts=$timestamp")
        webView.visibility = View.VISIBLE
    }



    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
    }
}

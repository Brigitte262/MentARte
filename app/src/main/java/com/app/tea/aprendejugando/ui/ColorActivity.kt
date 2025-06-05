package com.app.tea.aprendejugando.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
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
import android.widget.FrameLayout

private lateinit var debugHSV: TextView

private var isTTSReady = false

private lateinit var tts: TextToSpeech



class ColorActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var textoColor: TextView
    private lateinit var webView: WebView
    private lateinit var tts: TextToSpeech
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var ultimaDeteccion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        previewView = findViewById(R.id.previewView)
        textoColor = findViewById(R.id.textoResultado)
        webView = findViewById(R.id.webviewModelo)

        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("es", "ES")
                isTTSReady = true
                iniciarCamara() // ✅ se llama recién aquí
            }
        }

        val textoEncabezado = findViewById<TextView>(R.id.textoEncabezado)
        textoEncabezado.text = "¡Los colores!"

        
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
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor, ColorAnalyzer { color ->
                        if (color != ultimaDeteccion) {
                            ultimaDeteccion = color
                            runOnUiThread {
                                textoColor.text = "\uD83D\uDCF7 Color: ${color.replaceFirstChar { it.uppercase() }}"
                                if (isTTSReady) {
                                    tts.speak("¡Es $color!", TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                                mostrarModelo3D(color)
                            }
                        }
                    })
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun mostrarModelo3D(color: String) {
        val colorUrl = color.lowercase()
        val timestamp = System.currentTimeMillis()
        webView.loadUrl("file:///android_asset/color_animado.html?color=$colorUrl&ts=$timestamp")
        webView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
    }
}

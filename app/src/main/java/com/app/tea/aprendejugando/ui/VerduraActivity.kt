package com.app.tea.aprendejugando.ui
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.util.Size
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import com.app.tea.aprendejugando.R


class VerduraActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var previewView: PreviewView
    private lateinit var textoResultado: TextView
    private lateinit var webView: WebView
    private lateinit var tts: TextToSpeech
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var puedeDetectar = true
    private var verduraAnterior: String? = null
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

        tts = TextToSpeech(this, this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }

        val textoEncabezado = findViewById<TextView>(R.id.textoEncabezado)
        textoEncabezado.text = "¡Las Verduras!"

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

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(224, 224))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                if (puedeDetectar) {
                    puedeDetectar = false
                    val bitmap = imageProxy.toBitmap()
                    if (bitmap != null) {
                        val verdura = VerduraDetector.detectarVerdura(bitmap)

                        runOnUiThread {
                            if (verdura == "Verdura desconocida") {
                                textoResultado.text = "Esperando una tarjeta..."
                                webView.visibility = View.GONE
                            } else {
                                textoResultado.text = "\uD83D\uDCF7 ¡Detectamos: $verdura!"
                                if (isTTSReady) {
                                    tts.speak("¡Es una $verdura!", TextToSpeech.QUEUE_FLUSH, null, null)
                                }

                                mostrarVerduraEnWebView(verdura)
                            }
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        puedeDetectar = true
                    }, 3000)
                }
                imageProxy.close()
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun mostrarVerduraEnWebView(nombreVerdura: String) {
        val nombreNormalizado = nombreVerdura
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")

        if (nombreNormalizado == verduraAnterior) return
        verduraAnterior = nombreNormalizado

        val timestamp = System.currentTimeMillis()
        val url = "file:///android_asset/verdura_modelo.html?verdura=$nombreNormalizado&ts=$timestamp"
        webView.loadUrl(url)
        webView.visibility = View.VISIBLE
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
            isTTSReady = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
    }
}

package com.app.tea.aprendejugando.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.app.tea.aprendejugando.R
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout


private lateinit var webView: WebView

class OperacionActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var previewView: PreviewView
    private lateinit var textoOperacion: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var cameraExecutor: ExecutorService
    private var ultimaOperacion = ""
    private var puedeHablar = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar) // usa tu layout existente

        previewView = findViewById(R.id.previewView)
        textoOperacion = findViewById(R.id.textoResultado) // asegúrate que exista en tu XML

        tts = TextToSpeech(this, this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        webView = findViewById(R.id.webviewModelo)
        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(Color.TRANSPARENT)  // 👈 esto hace el fondo del WebView transparente
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null) // necesario en algunos dispositivos


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        }

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
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        procesarImagen(imageProxy)
                    }
                }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, selector, preview, analyzer)
        }, ContextCompat.getMainExecutor(this))
    }


    private fun ocultarModelo3D() {
        runOnUiThread {
            webView.loadUrl("about:blank") // limpia
            webView.visibility = View.GONE
        }
    }



    @OptIn(ExperimentalGetImage::class)
    private fun procesarImagen(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val texto = visionText.text
                    val operacionDetectada = Regex("""(\d+)\s*([\+\-])\s*(\d+)""").find(texto)

                    if (operacionDetectada != null) {
                        val (a, operador, b) = operacionDetectada.destructured
                        val operacionStr = "$a $operador $b"

                        if (operacionStr != ultimaOperacion && puedeHablar) {
                            ultimaOperacion = operacionStr
                            puedeHablar = false
                            ocultarModelo3D()

                            val numA = a.toInt()
                            val numB = b.toInt()
                            val resultado = if (operador == "+") numA + numB else numA - numB

                            val pregunta = if (operador == "+") {
                                "¿Cuánto es $numA más $numB?"
                            } else {
                                "¿Cuánto es $numA menos $numB?"
                            }

                            val concepto = if (operador == "+") {
                                "Vamos a contar juntos... " + (1..resultado).joinToString("... ")
                            } else {
                                "Si tienes $numA y quitas $numB, te quedan $resultado"
                            }

                            val respuesta = "La respuesta es $resultado"

                            // Paso 1: Mostrar pregunta
                            textoOperacion.text = pregunta
                            tts.speak(pregunta, TextToSpeech.QUEUE_FLUSH, null, null)

                            // Paso 2: Mostrar refuerzo (3.5s después)
                            Handler(Looper.getMainLooper()).postDelayed({
                                textoOperacion.text = concepto
                                tts.speak(concepto, TextToSpeech.QUEUE_ADD, null, null)
                            }, 3500)

                            // Paso 3: Mostrar respuesta y WebView (después del conteo)
                            val delayFinal = 3500L + if (operador == "+") resultado * 1000L else 3000L
                            Handler(Looper.getMainLooper()).postDelayed({
                                textoOperacion.text = respuesta
                                tts.speak(respuesta, TextToSpeech.QUEUE_ADD, null, null)

                                val timestamp = System.currentTimeMillis()
                                webView.loadUrl("file:///android_asset/numero_animado.html?numero=$resultado&ts=$timestamp")
                                webView.visibility = View.VISIBLE

                                puedeHablar = true
                            }, delayFinal)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("OCR_ERROR", "No se pudo procesar la imagen")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }



    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
            Handler(Looper.getMainLooper()).postDelayed({
                puedeHablar = true
            }, 800)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
    }

}

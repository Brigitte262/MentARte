package com.app.tea.aprendejugando.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.*
import android.util.Size
import android.widget.TextView
import android.speech.tts.TextToSpeech
import android.webkit.WebView
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.tea.aprendejugando.R
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.Executors
import androidx.core.text.toSpanned
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

private lateinit var webView: WebView
@androidx.annotation.OptIn(ExperimentalGetImage::class)
class AnimalActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var previewView: PreviewView
    private lateinit var textoColor: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var animalClassifier: AnimalClassifier
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var puedeDetectar = true
    private var isTTSReady = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        previewView = findViewById(R.id.previewView)
        textoColor = findViewById(R.id.textoResultado)
        webView = findViewById(R.id.webviewModelo)

        webView = findViewById(R.id.webviewModelo)
        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null) // importante para transparencia


        // Ocultar lo que no se usa
        findViewById<View>(R.id.debugHSV)?.visibility = View.GONE

        tts = TextToSpeech(this, this)
        animalClassifier = AnimalClassifier(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        }

        val textoEncabezado = findViewById<TextView>(R.id.textoEncabezado)
        textoEncabezado.text = "¡Los Animales!"


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

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(224, 224))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                if (puedeDetectar) {
                    puedeDetectar = false

                    val bitmap = imageProxy.toBitmap()
                    if (bitmap != null) {
                        val (etiqueta, confianza) = animalClassifier.classify(bitmap)
                        val resultadoLimpio = etiqueta.split(" ", ",", "_").firstOrNull()?.trim() ?: "Desconocido"
                        val resultadoFormateado = resultadoLimpio.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecaseChar() else it
                        }

                        // Umbral de seguridad aumentado
                        if (
                            resultadoFormateado == "Desconocido" ||
                            confianza < 0.80f ||
                            (resultadoFormateado in listOf("Mono", "Caballo", "Conejo") && confianza < 0.90f)
                        )
                        {

                            val image = InputImage.fromBitmap(bitmap, 0)
                            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

                            labeler.process(image)
                                .addOnSuccessListener { labels ->
                                    val respaldo = labels.firstOrNull {
                                        it.text.lowercase() in listOf("dog", "bear", "bird")
                                    }

                                    val traduccion = when (respaldo?.text?.lowercase()) {
                                        "dog" -> "Perro"
                                        "bear" -> "Oso"
                                        "bird" -> "Pajaro"
                                        else -> null
                                    }

                                    if (traduccion != null) {
                                        mostrarAnimalDetectado(traduccion)
                                    } else {
                                        mostrarMensajeNoDetectado()
                                    }
                                }
                                .addOnFailureListener {
                                    mostrarMensajeNoDetectado()
                                }

                        } else {
                            mostrarAnimalDetectado(resultadoFormateado)
                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        puedeDetectar = true
                    }, 3000)

                    imageProxy.close()
                } else {
                    imageProxy.close()
                }
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }




    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
            isTTSReady = true
        }
    }


    private fun mostrarAnimalEnWebView(nombreAnimal: String) {
        val nombreLimpio = nombreAnimal.lowercase()
        val timestamp = System.currentTimeMillis()
        val url = "file:///android_asset/animal_animado.html?animal=$nombreLimpio&ts=$timestamp"
        webView.loadUrl(url)
        webView.visibility = View.VISIBLE
    }

    private var ultimaDeteccion = ""

    private fun mostrarAnimalDetectado(nombre: String) {
        if (nombre == ultimaDeteccion) return // evitar repetición

        ultimaDeteccion = nombre
        runOnUiThread {
            textoColor.text = "\uD83D\uDCF7 Detectamos: $nombre"
            if (isTTSReady) {
                tts.speak("¡Es un $nombre!", TextToSpeech.QUEUE_FLUSH, null, null)
            }
            mostrarAnimalEnWebView(nombre)
        }
    }

    private fun mostrarMensajeNoDetectado() {
        runOnUiThread {
            textoColor.text = "Muéstrame mejor la tarjeta 😊"
            if (isTTSReady) {
                tts.speak("¿Puedes acercarme la tarjeta, por favor?", TextToSpeech.QUEUE_FLUSH, null, null)
            }
            webView.visibility = View.GONE
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        cameraExecutor.shutdown()
    }
}

// 🔧 Función de extensión para convertir ImageProxy a Bitmap
fun ImageProxy.toBitmap(): Bitmap? {
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




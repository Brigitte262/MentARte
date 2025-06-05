package com.app.tea.aprendejugando.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import android.app.Activity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.media.SoundPool
import com.app.tea.aprendejugando.R
import androidx.compose.ui.text.font.FontWeight
import android.content.Intent
import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.widget.Toast
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.ui.zIndex
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

private lateinit var tts: TextToSpeech

class ScreenOCRNumeros : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            launchNumerosOCRScreen()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchNumerosOCRScreen()
        } else {
            Toast.makeText(this, "Se requiere permiso de cámara para continuar 📷", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun launchNumerosOCRScreen() {
        setContent {
            val context = LocalContext.current
            var textoDetectado by remember { mutableStateOf("") }
            var ultimoNumero by remember { mutableStateOf<String?>(null) }
            var puedeDetectar by remember { mutableStateOf(true) }

            val tts = remember { mutableStateOf<TextToSpeech?>(null) }
            val isTTSReady = remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                tts.value = TextToSpeech(context) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        tts.value?.language = Locale("es", "ES")
                        isTTSReady.value = true
                    }
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    tts.value?.stop()
                    tts.value?.shutdown()
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply { keepScreenOn = true }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analyzer = TextAnalyzerNumeros(ctx) { numeroDetectado ->
                                if (!numeroDetectado.matches(Regex("^([0-9]|[1-9][0-9]|100)$"))) return@TextAnalyzerNumeros


                                if (isTTSReady.value && puedeDetectar) {
                                    // Corrección visual: evita confusión rápida entre 6 y 9
                                    if ((numeroDetectado == "6" && ultimoNumero == "9") ||
                                        (numeroDetectado == "9" && ultimoNumero == "6")) {
                                        return@TextAnalyzerNumeros
                                    }

                                    if (numeroDetectado != ultimoNumero) {
                                        ultimoNumero = numeroDetectado
                                        textoDetectado = numeroDetectado

                                        tts.value?.speak("Número $numeroDetectado", TextToSpeech.QUEUE_FLUSH, null, null)

                                        puedeDetectar = false
                                        GlobalScope.launch {
                                            delay(2000)
                                            puedeDetectar = true
                                        }
                                    }
                                }

                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build().apply {
                                    setAnalyzer(ContextCompat.getMainExecutor(ctx), analyzer)
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    this@ScreenOCRNumeros,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                Log.e("OCR", "Error al iniciar la cámara", e)
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .zIndex(3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Números",
                        fontSize = 26.sp,
                        color = Color.White,
                        modifier = Modifier
                            .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp))
                            .background(Color(0xFFA77449), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (textoDetectado.isNotEmpty()) "\uD83D\uDCF7 Número $textoDetectado" else "Detectando...",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp))
                            .background(Color(0xFFA77449), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    if (textoDetectado.isNotEmpty()) {
                        val numeroActual = textoDetectado
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.allowFileAccess = true
                                    settings.allowContentAccess = true
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                    isClickable = false
                                    isFocusable = false
                                    loadUrl("file:///android_asset/numero_animado.html?numero=$numeroActual")
                                }
                            },
                            update = { webView ->
                                val timestamp = System.currentTimeMillis()
                                webView.loadUrl("file:///android_asset/numero_animado.html?numero=$numeroActual&ts=$timestamp")
                            },
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .zIndex(2f)
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Regresar
                    Box(
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false) // 👈 sombra detrás
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                (context as? ComponentActivity)?.finish()
                            }
                            .height(56.dp)
                            .fillMaxWidth(0.65f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.boton),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = "Regresar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}





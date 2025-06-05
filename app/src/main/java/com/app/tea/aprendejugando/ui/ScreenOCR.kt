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
import android.widget.Toast
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.ui.zIndex
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


class ScreenOCR : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            launchVocalOCRScreen()
        }
    }

    // Función auxiliar para obtener la ruta del archivo HTML
    private fun vocalToHtmlFile(vocal: String): String {
        return "file:///android_asset/vocal_${vocal.lowercase()}.html"
    }

    private fun abrirARSimulado(context: Context, vocal: String) {
        val intent = Intent(context, ARSimuladaActivity::class.java).apply {
            putExtra("url", "file:///android_asset/aframe/ar_aframe.html?vocal=${vocal.lowercase()}")
        }
        context.startActivity(intent)
    }

    // Pantalla principal OCR con cámara + RA
    private fun launchVocalOCRScreen() {
        setContent {
            val context = LocalContext.current

            val soundPool = remember {
                SoundPool.Builder().setMaxStreams(5).build()
            }

            val soundMap = remember { mutableStateMapOf<String, Int>() }

            var textoDetectado by remember { mutableStateOf("") }
            var ultimaVocal by remember { mutableStateOf<String?>(null) }
            var puedeDetectar by remember { mutableStateOf(true) }

            Box(modifier = Modifier.fillMaxSize()) {
                // 📸 Cámara y análisis OCR
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply { keepScreenOn = true }

                        // Sonidos
                        soundMap["A"] = soundPool.load(ctx, R.raw.vocal_a, 1)
                        soundMap["E"] = soundPool.load(ctx, R.raw.vocal_e, 1)
                        soundMap["I"] = soundPool.load(ctx, R.raw.vocal_i, 1)
                        soundMap["O"] = soundPool.load(ctx, R.raw.vocal_o, 1)
                        soundMap["U"] = soundPool.load(ctx, R.raw.vocal_u, 1)

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analyzer = TextAnalyzer(ctx) { textoOCR ->
                                val vocal = textoOCR?.trim()?.uppercase() ?: ""
                                if (puedeDetectar && vocal.length == 1 && vocal in "AEIOU" && vocal != ultimaVocal) {
                                    ultimaVocal = vocal
                                    textoDetectado = vocal

                                    soundMap[vocal]?.let { soundId ->
                                        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                                    }

                                    puedeDetectar = false
                                    GlobalScope.launch {
                                        delay(2500)
                                        puedeDetectar = true
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
                                    this@ScreenOCR,
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

                // 🎨 Interfaz superpuesta
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .zIndex(3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFA77449), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Vocales",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFA77449), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (textoDetectado.isNotEmpty()) "\uD83D\uDCF7 La vocal $textoDetectado" else "Detectando...",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // 🌟 Letra animada en WebView (tamaño limitado para no tapar botones)
                    if (textoDetectado.isNotEmpty()) {
                        val vocalActual = textoDetectado.lowercase()
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.allowFileAccess = true
                                    settings.allowContentAccess = true
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                    isClickable = false
                                    isFocusable = false
                                    loadUrl("file:///android_asset/vocal_animada.html?vocal=$vocalActual")
                                }
                            },
                            update = { webView ->
                                webView.loadUrl("file:///android_asset/vocal_animada.html?vocal=$vocalActual")
                            },
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .zIndex(2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // 🔊 Botón de audio
                    Box(
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                textoDetectado.takeIf { it.isNotEmpty() }?.let {
                                    soundMap[it]?.let { id ->
                                        soundPool.play(id, 1f, 1f, 0, 0, 1f)
                                    }
                                }
                            }
                            .height(56.dp)
                            .fillMaxWidth(0.65f)
                            .align(Alignment.CenterHorizontally) // opcional si estás dentro de un Column
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.boton),
                            contentDescription = "Reproducir Audio",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = "🔊 Reproducir Audio",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔙 Botón de regreso
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


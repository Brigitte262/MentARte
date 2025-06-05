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

class ScreenOCRAbecedario : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            launchAbecedarioOCRScreen()
        }
    }

    private fun launchAbecedarioOCRScreen() {
        setContent {
            val context = LocalContext.current

            val cargados = remember { mutableSetOf<Int>() }

            val soundPool = remember {
                SoundPool.Builder().setMaxStreams(5).build().apply {
                    setOnLoadCompleteListener { _, sampleId, status ->
                        if (status == 0) {
                            cargados.add(sampleId)
                            Log.d("AUDIO", "✅ Audio cargado: ID = $sampleId")
                        }
                    }
                }
            }

            val soundMap = remember { mutableStateMapOf<String, Int>() }
            var textoDetectado by remember { mutableStateOf("") }
            var ultimaLetra by remember { mutableStateOf<String?>(null) }
            var puedeDetectar by remember { mutableStateOf(true) }

            // Precarga sonidos para todas las letras
            val letras = ('A'..'Z').map { it.toString() } + "Ñ"
            LaunchedEffect(Unit) {
                letras.forEach { letra ->
                    val resId = if (letra == "Ñ") R.raw.letra_enie
                    else context.resources.getIdentifier("letra_${letra.lowercase()}", "raw", context.packageName)
                    if (resId != 0) {
                        val soundId = soundPool.load(context, resId, 1)
                        soundMap[letra] = soundId
                    }
                }
            }



            Box(modifier = Modifier.fillMaxSize()) {
                // 📸 Cámara y OCR
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply { keepScreenOn = true }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analyzer = TextAnalyzer(ctx) { textoOCR ->
                                val letra = textoOCR?.trim()?.uppercase() ?: ""
                                if (puedeDetectar && letra.length == 1 && (letra[0] in 'A'..'Z' || letra == "Ñ")) {
                                    if (letra != ultimaLetra) {
                                        ultimaLetra = letra
                                        textoDetectado = letra

                                        soundMap[letra]?.let { soundId ->
                                            if (cargados.contains(soundId)) {
                                                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                                            } else {
                                                Log.d("AUDIO", "⚠️ Aún no cargado: $letra")
                                            }
                                        }


                                        puedeDetectar = false
                                        GlobalScope.launch {
                                            delay(2500)
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
                                    this@ScreenOCRAbecedario,
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

                // 🎨 Interfaz
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
                            text = "Abecedario",
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
                            text = if (textoDetectado.isNotEmpty()) "\uD83D\uDCF7 Letra $textoDetectado" else "Detectando...",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    if (textoDetectado.isNotEmpty()) {
                        val letraActual = textoDetectado.lowercase()
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.allowFileAccess = true
                                    settings.allowContentAccess = true
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                    isClickable = false
                                    isFocusable = false
                                    loadUrl("file:///android_asset/letra_animada.html?letra=$letraActual")
                                }
                            },
                            update = { webView ->
                                val timestamp = System.currentTimeMillis()
                                webView.loadUrl("file:///android_asset/letra_animada.html?letra=$letraActual&ts=$timestamp")
                            },
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .zIndex(2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // Botón Escuchar audio
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

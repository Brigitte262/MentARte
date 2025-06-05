package com.app.tea.aprendejugando.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.app.tea.aprendejugando.ui.theme.AprendeJugandoTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text


class RegistroExitosoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("RegistroExitoso", "✅ Entraste a RegistroExitosoActivity")

        val nombre = intent.getStringExtra("nombre") ?: "Usuario"

        setContent {
            AprendeJugandoTheme {
                RegistroExitosoPantalla(nombre) {
                    startActivity(Intent(this, MenuCursosActivity::class.java)) // O la pantalla que quieras
                    finish()
                }
            }
        }
    }
}

@Composable
fun RegistroExitosoPantalla(nombre: String, onFinalizar: () -> Unit) {
    // Espera 2.5 segundos y redirige
    LaunchedEffect(Unit) {
        delay(2500)
        onFinalizar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF2CC)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎉", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡Registro exitoso!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido/a $nombre a la aventura mágica 🧚",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF444444),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}



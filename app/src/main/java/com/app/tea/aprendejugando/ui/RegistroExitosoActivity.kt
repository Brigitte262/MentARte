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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text


class RegistroExitosoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nombre = intent.getStringExtra("nombre") ?: "Usuario"

        setContent {
            AprendeJugandoTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFF9F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "¡Bienvenido, $nombre!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )

                        Text(
                            text = "Tu cuenta ha sido creada exitosamente.",
                            fontSize = 16.sp,
                            color = Color(0xFF5D4037)
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Button(
                            onClick = {
                                val intent = Intent(this@RegistroExitosoActivity, MenuCursosActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB8A))
                        ) {
                            Text("Comenzar")
                        }
                    }
                }
            }
        }
    }
}



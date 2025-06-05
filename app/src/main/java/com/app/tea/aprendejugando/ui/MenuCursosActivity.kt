package com.app.tea.aprendejugando.ui

import com.app.tea.aprendejugando.ui.theme.AprendeJugandoTheme
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.app.tea.aprendejugando.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign

import com.app.tea.aprendejugando.ui.ScreenComunicacion
import com.app.tea.aprendejugando.ui.ScreenContenidoMatematica
import com.app.tea.aprendejugando.ui.ScreenHabilidades
// Estilos
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
// Imagen y recursos
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
// Sombra (shadow) desde Foundation
import androidx.compose.ui.draw.shadow


class MenuCursosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nombre = intent.getStringExtra("nombreUsuario") ?: "Usuario"

        setContent {
            AprendeJugandoTheme {
                MenuCursosScreen(nombre = nombre) // ✅ Aquí le pasas el nombre correctamente
            }
        }
    }
}

@Composable
fun MenuCursosScreen(nombre: String) {
    val context = LocalContext.current
    val cursos = listOf(
        "Comunicación" to R.drawable.ic_comunicacion,
        "Matemática" to R.drawable.ic_matematica,
        "Ciencia y Ambiente" to R.drawable.ic_ciencia,
        "Habilidades Sociales" to R.drawable.ic_habilidades
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2E2C9))
            .padding(18.dp)
    ) {
        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight
        val isTablet = screenWidth > 600.dp

        val cardWidth = if (isTablet) 160.dp else screenWidth * 0.48f
        val cardHeight = if (isTablet) 160.dp else screenWidth * 0.50f
        val iconSize = if (isTablet) 90.dp else screenWidth * 0.22f
        val textSize = if (isTablet) 18.sp else screenWidth.value.times(0.038).sp
        val buttonWidth = if (isTablet) 0.5f else 0.7f

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(55.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(Color(0xFFBA9380), RoundedCornerShape(16.dp))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¡Bienvenido, $nombre!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Elige una aventura mágica!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E342E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(cursos) { (nombreCurso, icono) ->
                        Box(
                            modifier = Modifier
                                .width(cardWidth)
                                .height(cardHeight)
                                .shadow(15.dp, RoundedCornerShape(16.dp)) // Sombra bien proyectada
                                .background(Color(0xFFFFF9F0), shape = RoundedCornerShape(16.dp))
                                .clickable {
                                    val intent = when (nombreCurso) {
                                        "Comunicación" -> Intent(context, ScreenComunicacion::class.java)
                                        "Matemática" -> Intent(context, ScreeMatematica::class.java)
                                        "Ciencia y Ambiente" -> Intent(context, SreenCiencia::class.java)
                                        "Habilidades Sociales" -> Intent(context, ScreenHabilidades::class.java)
                                        else -> Intent(context, MainActivity::class.java)
                                    }
                                    context.startActivity(intent)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp), // padding va por dentro
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = icono),
                                    contentDescription = nombreCurso,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(iconSize)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = nombreCurso,
                                    color = Color(0xFF67644F),
                                    fontSize = textSize,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        SessionManager.isLoggedIn = false
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                    .height(56.dp)
                    .fillMaxWidth(buttonWidth)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Cerrar sesión",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "Cerrar Sesión",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}












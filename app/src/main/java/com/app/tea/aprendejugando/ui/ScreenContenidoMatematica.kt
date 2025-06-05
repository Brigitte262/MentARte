package com.app.tea.aprendejugando.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.app.tea.aprendejugando.R
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.shadow
import com.app.tea.aprendejugando.ui.componentes.contenidoCard




class ScreenContenidoMatematica : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContenidoMatematicaScreen()
        }
    }
}

@Composable
fun ContenidoMatematicaScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2FFD2)) // Fondo verde claro
    ) {
        val isTablet = this@BoxWithConstraints.maxWidth > 600.dp
        val spacing = if (isTablet) 40.dp else 25.dp
        val paddingGeneral = if (isTablet) 50.dp else 30.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingGeneral)
        ) {
            Text(
                text = "¡Diviértete con las Matemáticas!",
                fontSize = if (isTablet) 32.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF67644F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing))

            // Primera fila: 2 contenidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                contenidoCard("Números del 1 - 100", R.drawable.numeros, context, ScreenNumeros::class.java, isTablet)
                contenidoCard("Suma/Resta", R.drawable.operacion, context, ScreenOperaciones::class.java, isTablet)
            }

            Spacer(modifier = Modifier.height(spacing))

            // Segunda fila: 1 contenido centrado
            contenidoCard("Colores", R.drawable.colores, context, ScreenColores::class.java, isTablet)

            Spacer(modifier = Modifier.height(spacing))

            // Botón Regresar
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        (context as? ComponentActivity)?.finish()
                    }
                    .height(56.dp)
                    .fillMaxWidth(if (isTablet) 0.45f else 0.65f)
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

            Spacer(modifier = Modifier.height(spacing))

            // Nube decorativa
            Image(
                painter = painterResource(id = R.drawable.nubep),
                contentDescription = "Decoración nube",
                modifier = Modifier
                    .align(Alignment.Start)
                    .graphicsLayer(scaleX = -1f)
                    .size(if (isTablet) 150.dp else 110.dp)
            )
        }
    }
}



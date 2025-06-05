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
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.app.tea.aprendejugando.ui.componentes.contenidoCard



// Sombra (shadow) desde Foundation
import androidx.compose.ui.draw.shadow

class ScreenContenidoComunicacion : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContenidoComunicacionScreen()
        }
    }
}

@Composable
fun ContenidoComunicacionScreen() {
    val context = LocalContext.current

    val contenidos = listOf(
        "Vocales" to R.drawable.vocales,
        "Abecedario" to R.drawable.abe,
        "Meses del Año" to R.drawable.meses
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6D3))
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
                text = "¡Explora el contenido mágico!",
                fontSize = if (isTablet) 32.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF67644F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                contenidoCard("Vocales", R.drawable.vocales, context, ScreenVocales::class.java, isTablet)
                contenidoCard("Abecedario", R.drawable.abe, context, ScreenAbecedario::class.java, isTablet)
            }

            Spacer(modifier = Modifier.height(spacing))

            contenidoCard("Meses del Año", R.drawable.meses, context, ScreenMeses::class.java, isTablet)

            Spacer(modifier = Modifier.height(spacing))

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


